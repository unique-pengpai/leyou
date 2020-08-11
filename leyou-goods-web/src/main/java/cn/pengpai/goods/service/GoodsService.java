package cn.pengpai.goods.service;

import cn.pengpai.goods.client.BrandClient;
import cn.pengpai.goods.client.CategoryClient;
import cn.pengpai.goods.client.GoodsClient;
import cn.pengpai.goods.client.SpecificationClient;
import cn.pengpai.item.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;

    /**
     * 根绝spuId查询商品所需要的的数据信息
     * @param spuId
     * @return
     */
    public Map<String, Object> loadData(Long spuId){

        Map<String, Object> map = new HashMap<>();

        // 查询Spu
        Spu spu = this.goodsClient.querySpuBySpuid(spuId);
        // 查询SpuDetail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spuId);
        // 查询Sku集合
        List<Sku> skus = this.goodsClient.querySkusBySpuId(spuId);
        // 查询Brand
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());
        // 查询SpecGroup
        List<SpecGroup> specGroups = this.specificationClient.querySpecGroupsWithParamByCid(spu.getCid3());
        // 查询分类名称
        List<Long> cids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<String> names = cids.stream().map(cid -> this.categoryClient.queryCategoryNameById(cid)).collect(Collectors.toList());
        List<Map<String, Object>> categories  = new ArrayList<>();
        for (int i = 0; i < cids.size(); i++) {
            Map<String, Object> categoryMap = new HashMap<>();
            categoryMap.put("id", cids.get(i));
            categoryMap.put("name", names.get(i));
            categories.add(categoryMap);
        }
        // 查询特殊的规格参数
        List<SpecParam> specParams = this.specificationClient.querySpecParams(null, spu.getCid3(), false, null);
        Map<Long, String> specialParamMap = new HashMap<>();
        specParams.forEach(param -> specialParamMap.put(param.getId(), param.getName()));
        // 查询通用规格参数
        List<SpecParam> genericParams = this.specificationClient.querySpecParams(null, spu.getCid3(), true, null);
        Map<Long, String> genericParamMap = new HashMap<>();
        genericParams.forEach(param -> genericParamMap.put(param.getId(), param.getName()));

        // 封装数据
        map.put("spu", spu);
        map.put("spuDetail", spuDetail);
        map.put("skus", skus);
        map.put("categories", categories);
        map.put("brand", brand);
        map.put("groups", specGroups);
        map.put("specialParamMap", specialParamMap);
        map.put("genericParamMap", genericParamMap);


        return  map;


    }
}
