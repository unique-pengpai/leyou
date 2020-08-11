package cn.pengpai.item.api;

import cn.pengpai.commo.pojo.PageResult;
import cn.pengpai.item.bo.SpuBo;
import cn.pengpai.item.pojo.Sku;
import cn.pengpai.item.pojo.Spu;
import cn.pengpai.item.pojo.SpuDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface GoodsApi {
    /**
     * 根据spuid查询sku对象集合
     *
     * @param spuId
     * @return
     */
    @GetMapping("sku/list")
    public List<Sku> querySkusBySpuId(@RequestParam("id") Long spuId);

    /**
     * 根据spuId查询spuDetail对象
     *
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail/{spuId}")
    public SpuDetail querySpuDetailBySpuId(@PathVariable Long spuId);

    /**
     * 查询商品
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("spu/page")
    public PageResult<SpuBo> querySpuBoByPage(
            @RequestParam(value = "key", required = false)String key,
            @RequestParam(value = "saleable", required = false)Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1")Integer page,
            @RequestParam(value = "rows", defaultValue = "5")Integer rows);

    /**
     * 根据spuId查询Spu
     * @param spuId
     * @return
     */
    @GetMapping("{spuId}")
    public Spu querySpuBySpuid(@PathVariable("spuId") Long spuId);

    /**
     * 根据skuId查询Sku
     * @param skuId
     * @return
     */
    @GetMapping("/sku/{skuId}")
    public Sku querySkuBySkuId(@PathVariable("skuId") Long skuId);

}
