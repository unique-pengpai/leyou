package cn.pengpai.item.service.impl;

import cn.pengpai.item.mapper.CategoryMapper;
import cn.pengpai.item.pojo.Category;
import cn.pengpai.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 根据parentId查询子类目
     * @param pid
     * @return
     */
    @Override
    public List<Category> queryCategoriesByPid(Long pid) {

        Category record = new Category();
        record.setParentId(pid);
        return this.categoryMapper.select(record);

    }

    /**
     * 根据品牌id查询分类对象
     * @param bid
     * @return
     */
    @Override
    public List<Category> queryCategoriesByBid(Long bid) {
       return categoryMapper.queryCategoriesByBid(bid);
    }

    /**
     * 根据id集合查询分类名称集合
     * @param ids
     * @return
     */
    @Override
    public List<String> queryNamesByIds(List<Long> ids) {
        List<Category> categories = this.categoryMapper.selectByIdList(ids);
        return categories.stream().map(category -> category.getName()).collect(Collectors.toList());
    }

    /**
     * 根据分类id查询分类名称
     * @param id
     * @return
     */
    @Override
    public String queryNameById(Long id) {
        Category category = this.categoryMapper.selectByPrimaryKey(id);
        return category.getName();
    }

}
