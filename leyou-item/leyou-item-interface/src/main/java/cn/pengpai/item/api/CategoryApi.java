package cn.pengpai.item.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("category")
public interface CategoryApi {

    /**
     * 根据分类id集合查询分类名称集合
     * @param ids
     * @return
     */
    @GetMapping("{ids}")
    public List<String> queryCategoryNamesByIds(@PathVariable List<Long> ids);

    /**
     * 根据分类id查询分类名称
     * @param id
     * @return
     */
    @GetMapping()
    public String queryCategoryNameById(@RequestParam("cid") Long id);
}
