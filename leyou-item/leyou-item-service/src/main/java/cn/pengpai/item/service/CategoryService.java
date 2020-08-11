package cn.pengpai.item.service;

import cn.pengpai.item.pojo.Category;

import java.util.List;

public interface CategoryService {

    /**
     * 根据parentId查询子类目
     * @param pid
     * @return
     */
    List<Category> queryCategoriesByPid(Long pid);

    /**
     * 根据品牌id查询分类对象
     * @param bid
     * @return
     */
    List<Category> queryCategoriesByBid(Long bid);

    /**
     * 根据id集合查询分类名称
     * @param ids
     * @return
     */
    List<String> queryNamesByIds(List<Long> ids);

    /**
     * 根据分类id查询分类名称
     * @param id
     * @return
     */
    String queryNameById(Long id);
}
