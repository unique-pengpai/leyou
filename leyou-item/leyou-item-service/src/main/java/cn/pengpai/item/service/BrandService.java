package cn.pengpai.item.service;

import cn.pengpai.commo.pojo.PageResult;
import cn.pengpai.item.pojo.Brand;

import java.util.List;

public interface BrandService {

    /**
     * 分页查询品牌对象集合
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    public PageResult<Brand> queryBrandsByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc);

    /**
     * 保存品牌
     * @param brand
     * @param cids
     * @return
     */
    void saveBrand(Brand brand, List<Long> cids);

    /**
     * 根据分类id查询品牌对象
     * @param cid
     * @return
     */
    List<Brand> queryBrandsByCid(Long cid);

    /**
     * 根据品牌id查新品牌对象
     * @param id
     * @return
     */
    Brand queryBrandById(Long id);
}
