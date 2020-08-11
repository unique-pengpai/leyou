package cn.pengpai.item.service;

import cn.pengpai.commo.pojo.PageResult;
import cn.pengpai.item.bo.SpuBo;
import cn.pengpai.item.pojo.Sku;
import cn.pengpai.item.pojo.Spu;
import cn.pengpai.item.pojo.SpuDetail;

import java.util.List;

public interface GoodsService {

    /**
     * 查询商品
     * @param key
     * @param saleable
     * @param page
     * @param rows
     */
    PageResult<SpuBo> querySpuBoByPage(String key, Boolean saleable, Integer page, Integer rows);

    /**
     * 添加商品
     * @param spuBo
     */
    void saveGoods(SpuBo spuBo);

    /**
     * 根据spuId查询spuDetail对象
     * @param spuId
     * @return
     */
    SpuDetail querySpuDetailBySpuId(Long spuId);

    /**
     * 根据spuid查询sku对象集合
     * @param spuId
     * @return
     */
    List<Sku> querySkusBySpuId(Long spuId);

    /**
     * 更新商品对象
     * @param spuBo
     */
    void updateGoods(SpuBo spuBo);

    /**
     * 根据spuId查询Spu
     * @param spuId
     * @return
     */
    Spu querySpuBySpuid(Long spuId);

    /**
     * 根据skuId查询Sku
     * @param skuId
     * @return
     */
    Sku querySkuBySkuId(Long skuId);
}
