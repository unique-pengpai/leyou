package cn.pengpai.item.api;

import cn.pengpai.item.pojo.SpecGroup;
import cn.pengpai.item.pojo.SpecParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("spec")
public interface SpecificationApi {

    /**
     * 根据分类id查询规格组
     *
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    List<SpecGroup> querySpecGroupsByCid(@PathVariable("cid") Long cid);

    /**
     * 通用条件查询
     *
     * @param gid
     * @param cid
     * @param generic
     * @param searching
     * @return
     */
    @GetMapping("params")
    List<SpecParam> querySpecParams(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "generic", required = false) Boolean generic,
            @RequestParam(value = "searching", required = false) Boolean searching);

    /**
     * 根据分类id查询规格组和规格参数
     * @param cid
     * @return
     */
    @GetMapping("groups/param/{cid}")
    List<SpecGroup> querySpecGroupsWithParamByCid(@PathVariable("cid") Long cid);
}
