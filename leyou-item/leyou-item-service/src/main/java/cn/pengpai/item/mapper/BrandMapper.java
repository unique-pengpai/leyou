package cn.pengpai.item.mapper;

import cn.pengpai.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> {

    /**
     * 添加中间表关联id
     * @param cid
     * @param id
     */
    @Insert("INSERT INTO tb_category_brand(category_id, brand_id) VALUES (#{cid}, #{bid});")
    void insertCategoryAndBrand(@Param("cid") Long cid, @Param("bid")Long id);

    /**
     * 根据分类id查询品牌对象
     * @param cid
     * @return
     */
    @Select("SELECT b.* FROM tb_brand b INNER JOIN tb_category_brand cb ON b.id=cb.brand_id WHERE cb.category_id = #{cid}")
    List<Brand> queryBrandsByCid(@Param("cid") Long cid);

    @Select("SELECT * FROM tb_brand WHERE id = #{id}")
    Brand queryBrandById(Long id);
}
