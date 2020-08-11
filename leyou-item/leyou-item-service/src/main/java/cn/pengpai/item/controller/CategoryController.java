package cn.pengpai.item.controller;

import cn.pengpai.item.pojo.Category;
import cn.pengpai.item.service.CategoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据parentId查询子类目
     * @param pid
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoriesByPid(@RequestParam( value = "pid", defaultValue = "0") Long pid){

        if (pid == null || pid.longValue() < 0){
            return ResponseEntity.badRequest().build();
        }

        List<Category> categories = this.categoryService.queryCategoriesByPid(pid);

        if (CollectionUtils.isEmpty(categories)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(categories);
    }

    /**
     * 根据品牌id查询分类对象
     * @param bid
     * @return
     */
    @GetMapping("bid/{bid}")
    public ResponseEntity<List<Category>> queryCategoriesByBid(@PathVariable Long bid){

        List<Category> categories = categoryService.queryCategoriesByBid(bid);
        if (CollectionUtils.isEmpty(categories)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categories);
    }

    /**
     * 根据分类id集合查询分类名称集合
     * @param ids
     * @return
     */
    @GetMapping("{ids}")
    public ResponseEntity<List<String>> queryCategoryNamesByIds(@PathVariable List<Long> ids){

        List<String> names = this.categoryService.queryNamesByIds(ids);

        if (CollectionUtils.isEmpty(names)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(names);

    }

    /**
     * 根据分类id查询分类名称
     * @param id
     * @return
     */
    @GetMapping()
    public ResponseEntity<String> queryCategoryNameById(@RequestParam("cid") Long id){

        String name = this.categoryService.queryNameById(id);

        if (StringUtils.isBlank(name)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(name);

    }
}
