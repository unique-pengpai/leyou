package cn.pengpai.item.service.impl;

import cn.pengpai.commo.pojo.PageResult;
import cn.pengpai.item.bo.SpuBo;
import cn.pengpai.item.mapper.*;
import cn.pengpai.item.pojo.*;
import cn.pengpai.item.service.CategoryService;
import cn.pengpai.item.service.GoodsService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsServiceImpl.class);

    /**
     * 查询商品
     *
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @Override
    public PageResult<SpuBo> querySpuBoByPage(String key, Boolean saleable, Integer page, Integer rows) {

        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        // 添加搜索条件
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);
        }
        // 添加分页条件
        PageHelper.startPage(page, rows);

        List<Spu> spus = this.spuMapper.selectByExample(example);
        PageInfo<Spu> spuPageInfo = new PageInfo<>(spus);

        // 将spus集合对象转换成spubo集合对象
        List<Spu> spuBos = spus.stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            BeanUtils.copyProperties(spu, spuBo);

            // 查询品牌名称
            Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());

            // 查询分类名称
            List<String> names = categoryService.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuBo.setCname(StringUtils.join(names, ", "));

            return spuBo;
        }).collect(Collectors.toList());

        return new PageResult(spuPageInfo.getTotal(), spuBos);
    }

    /**
     * 添加商品
     *
     * @param spuBo
     */
    @Override
    public void saveGoods(SpuBo spuBo) {

        // add spu
        spuBo.setId(null);
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        this.spuMapper.insertSelective(spuBo);

        // add spudetail
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        this.spuDetailMapper.insertSelective(spuDetail);

        // add spu
        addSpuAndStock(spuBo);

        this.sendMessage(spuBo.getId(), "insert");
    }

    /**
     * 添加sku和stock对象
     *
     * @param spuBo
     */
    private void addSpuAndStock(SpuBo spuBo) {
        spuBo.getSkus().forEach(sku -> {
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.insertSelective(sku);

            // add stock
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insertSelective(stock);
        });
    }

    /**
     * 根据spuId查询spuDetail对象
     *
     * @param spuId
     * @return
     */
    @Override
    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        return this.spuDetailMapper.selectByPrimaryKey(spuId);
    }

    /**
     * 根据spuid查询sku对象集合
     *
     * @param spuId
     * @return
     */
    @Override
    public List<Sku> querySkusBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = this.skuMapper.select(sku);
        skus.forEach(sku1 -> {
            Stock stock = this.stockMapper.selectByPrimaryKey(sku1.getId());
            sku1.setStock(stock.getStock());
        });
        return skus;
    }

    /**
     * 更新商品对象
     *
     * @param spuBo
     */
    @Override
    public void updateGoods(SpuBo spuBo) {

        // 查询以前的sku对象
        List<Sku> skus = this.querySkusBySpuId(spuBo.getId());
        if (!CollectionUtils.isEmpty(skus)) {
            // 删除以前的库存
            List<Long> skuIds = skus.stream().map(sku -> sku.getId()).collect(Collectors.toList());
            Example example = new Example(Stock.class);
            example.createCriteria().andIn("skuId", skuIds);
            this.stockMapper.deleteByExample(example);

            // 删除sku对象
            Sku sku = new Sku();
            sku.setSpuId(spuBo.getId());
            this.skuMapper.delete(sku);
        }

        // 新增sku和库存
        this.addSpuAndStock(spuBo);

        // 更新spu对象
        spuBo.setLastUpdateTime(new Date());
        spuBo.setValid(null);
        spuBo.setSaleable(null);
        spuBo.setCreateTime(null);
        this.spuMapper.updateByPrimaryKeySelective(spuBo);

        // 更新spudetail对象
        this.spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());

        this.sendMessage(spuBo.getId(), "update");

    }

    /**
     * 根据spuId查询Spu
     *
     * @param spuId
     * @return
     */
    @Override
    public Spu querySpuBySpuid(Long spuId) {
        return this.spuMapper.selectByPrimaryKey(spuId);
    }

    /**
     * 根据skuId查询Sku
     * @param skuId
     * @return
     */
    @Override
    public Sku querySkuBySkuId(Long skuId) {
        Sku sku = new Sku();
        sku.setId(skuId);
        return this.skuMapper.selectByPrimaryKey(sku);
    }


    /**
     * 消息发送
     * @param id
     * @param type
     */
    private void sendMessage(Long id, String type) {

        try {
            this.amqpTemplate.convertAndSend("item." + type, id);
        } catch (AmqpException e) {
            LOGGER.error("{}商品消息发送异常，商品id：{}", type, id, e);
        }

    }
}
