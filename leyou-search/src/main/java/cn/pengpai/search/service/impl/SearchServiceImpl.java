package cn.pengpai.search.service.impl;

import cn.pengpai.commo.pojo.PageResult;
import cn.pengpai.item.pojo.*;
import cn.pengpai.search.client.BrandClient;
import cn.pengpai.search.client.CategoryClient;
import cn.pengpai.search.client.GoodsClient;
import cn.pengpai.search.client.SpecificationClient;
import cn.pengpai.search.pojo.Goods;
import cn.pengpai.search.pojo.SearchRequest;
import cn.pengpai.search.pojo.SearchResult;
import cn.pengpai.search.repository.GoodsRepository;
import cn.pengpai.search.service.SearchService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private GoodsRepository goodsRepository;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 构建Goods对象
     *
     * @param spu
     * @return
     * @throws IOException
     */
    public Goods buildGoods(Spu spu) throws IOException {

        Goods goods = new Goods();

        // 查询分类名名称集合
        List<Long> cids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<String> categoryNames = cids.stream().map(cid -> this.categoryClient.queryCategoryNameById(cid)).collect(Collectors.toList());
        //List<String> categoryNames = this.categoryClient.queryCategoryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        // 查询品牌对象c
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());
        // 查询spu下的所有的sku
        List<Sku> skus = goodsClient.querySkusBySpuId(spu.getId());
        // 价格集合
        List<Long> prices = new ArrayList<>();
        // skus集合
        List<Map<String, Object>> skuMapList = new ArrayList<>();
        skus.forEach(sku -> {
            // 添加价格集合
            prices.add(sku.getPrice());
            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("id", sku.getId());
            skuMap.put("title", sku.getTitle());
            skuMap.put("price", sku.getPrice());
            skuMap.put("image", StringUtils.isNotBlank(sku.getImages()) ? StringUtils.split(sku.getImages(), ",")[0] : "");
            skuMapList.add(skuMap);
        });

        // 查询所有规格对象
        List<SpecParam> specParams = specificationClient.querySpecParams(null, spu.getCid3(), null, true);
        // 查询spudetail
        SpuDetail spuDetail = goodsClient.querySpuDetailBySpuId(spu.getId());
        // 查询通用规格参数
        Map<String, Object> genericSpecMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<String, Object>>() {
        });
        // 查询特殊规格参数
        Map<String, List<Object>> specialSpecMap = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<String, List<Object>>>() {
        });

        Map<String, Object> specs = new HashMap<>();
        specParams.forEach(param -> {
            // 判断是否为通用规格参数
            if (param.getGeneric()) {
                // 获取通用规格参数值
                String genericValue = genericSpecMap.get(param.getId().toString()).toString();
                // 判断搜索字段是否是是数值
                if (param.getNumeric()) {
                    genericValue = chooseSegment(genericValue, param);
                }
                specs.put(param.getName(), genericValue);
            } else {
                specs.put(param.getName(), specialSpecMap.get(param.getId().toString()));
            }
        });

        goods.setId(spu.getId());
        goods.setCid1(spu.getCid3());
        goods.setCid2(spu.getCid3());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        goods.setPrice(prices);
        goods.setAll(StringUtils.join(categoryNames, " ") + " " + brand.getName() + " " + spu.getTitle());
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        goods.setSpecs(specs);

        return goods;
    }

    /**
     * 关键字分页搜索
     *
     * @param searchRequest
     * @return
     */
    @Override
    public PageResult<Goods> search(SearchRequest searchRequest) {
        // 判断搜索关键字是否为空
        String key = searchRequest.getKey();
        if (StringUtils.isBlank(key)) {
            return null;
        }
        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加查询条件
        //QueryBuilder basicQuery = QueryBuilders.matchQuery("all", key).operator(Operator.AND);
        BoolQueryBuilder basicQuery = buildBoolQueryBuilder(searchRequest);
        queryBuilder.withQuery(basicQuery);
        // 过滤查询结果字段
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "subTitle", "skus"}, null));
        // 准备分页参数
        queryBuilder.withPageable(PageRequest.of(searchRequest.getPage() - 1, searchRequest.getSize()));
        // 聚合
        String categoryAggName = "categories";
        String brandAggName = "brands";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));
        // 查询结果
        AggregatedPage<Goods> search = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());
        List<Map<String, Object>> categories = getCategoryAggResult(search.getAggregation(categoryAggName));
        List<Brand> brands = getBrandAggResult(search.getAggregation(brandAggName));
        // 分类结果是一个，进行聚合
        List<Map<String, Object>> spec = null;
        if (categories.size() == 1) {
            spec = getParamAggResult((Long) categories.get(0).get("id"), basicQuery);
        }
        // 封装结果并返回
        return new SearchResult(search.getTotalElements(), search.getTotalPages(), search.getContent(), categories, brands, spec);
    }

    /**
     * 构建布尔查询
     * @param request
     * @return
     */
    private BoolQueryBuilder buildBoolQueryBuilder(SearchRequest request) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 添加基本查询条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND));
        // 获取过滤查询条件
        Map<String, String> filter = request.getFilter();
        for (Map.Entry<String, String> entry : filter.entrySet()) {
            String key = entry.getKey();
            // 如果过滤条件是“品牌”, 过滤的字段名：brandId
            if (StringUtils.equals("品牌", key)) {
                key = "brandId";
            } else if (StringUtils.equals("分类", key)) {
                // 如果是“分类”，过滤字段名：cid3
                key = "cid3";
            } else {
                // 如果是规格参数名，过滤字段名：specs.key.keyword
                key = "specs." + key + ".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key, entry.getValue()));
        }

        return boolQueryBuilder;
    }

    /**
     * 规格结果集
     * @param id
     * @param basicQuery
     * @return
     */
    private List<Map<String, Object>> getParamAggResult(Long id, QueryBuilder basicQuery) {
        // 创建自定义查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(basicQuery);
        // 查询要聚合的规格参数
        List<SpecParam> specParams = this.specificationClient.querySpecParams(null, id, null, true);
        // 添加聚合
        specParams.forEach(param -> queryBuilder.addAggregation(AggregationBuilders.terms(param.getName()).field("specs." + param.getName() + ".keyword")));
        // 只需要聚合结果集，不需要查询结果集
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{}, null));
        // 执行聚合查询
        AggregatedPage<Goods> search = (AggregatedPage<Goods>)this.goodsRepository.search(queryBuilder.build());

        // 定义一个集合收集结果集
        List<Map<String, Object>> paramMapList = new ArrayList<>();
        // 解析查询结果集
        Map<String, Aggregation> aggregationMap = search.getAggregations().asMap();
        for (Map.Entry<String, Aggregation> entry : aggregationMap.entrySet()) {
            Map<String, Object> map = new HashMap<>();
            List<Object> options = new ArrayList<>();
            // 解析每个聚合
            StringTerms terms = (StringTerms) entry.getValue();
            // 遍历每个聚合中桶，把桶中key放入收集规格参数的集合中
            terms.getBuckets().forEach(bucket -> options.add(bucket.getKey()));
            map.put("k", entry.getKey());
            map.put("options", options);
            paramMapList.add(map);
        }
        return paramMapList;
    }

    /**
     * 解析品牌结果集
     *
     * @param aggregation
     * @return
     */
    private List<Brand> getBrandAggResult(Aggregation aggregation) {
        // 解析结果集
        LongTerms terms = (LongTerms) aggregation;
        // 获取品牌id桶
        List<LongTerms.Bucket> buckets = terms.getBuckets();
        // 品牌结合对象
        List<Brand> brands = new ArrayList<>();
        buckets.forEach(bucket -> brands.add(this.brandClient.queryBrandById(bucket.getKeyAsNumber().longValue())));
        return brands;
    }

    /**
     * 解析分类聚合结果集
     *
     * @param aggregation
     * @return
     */
    private List<Map<String, Object>> getCategoryAggResult(Aggregation aggregation) {
        // 解析结果集
        LongTerms terms = (LongTerms) aggregation;
        // 获取分类的id桶
        List<LongTerms.Bucket> buckets = terms.getBuckets();
        // 获得分类id的集合
        return buckets.stream().map(bucket -> {
            // 获取id
            long id = bucket.getKeyAsNumber().longValue();
            //List<Long> cid = Arrays.asList(id);
            // 根据id查新分类名称
            String name = this.categoryClient.queryCategoryNameById(id);
            Map<String, Object> map = new HashMap<>();
            map.put("id", id);
            map.put("name", name);
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * 数字范围转换
     *
     * @param value
     * @param p
     * @return
     */
    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    /**
     * 创建索引
     * @param id
     * @throws IOException
     */
    public void createIndex(Long id) throws IOException {
        Spu spu = this.goodsClient.querySpuBySpuid(id);
        // 构建商品
        Goods goods = this.buildGoods(spu);
        // 保存数据到索引库
        this.goodsRepository.save(goods);
    }

    /**
     * 删除索引
     * @param id
     */
    public void deleteIndex(Long id) {
        this.goodsRepository.deleteById(id);
    }




}
