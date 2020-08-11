package cn.pengpai.item.controller;

import cn.pengpai.item.pojo.SpecGroup;
import cn.pengpai.item.pojo.SpecParam;
import cn.pengpai.item.service.SpecificationService;
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
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;

    /**
     * 根据分类id查询规格组
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroupsByCid(@PathVariable("cid") Long cid){

        List<SpecGroup> specGroups = this.specificationService.querySpecGroupsByCid(cid);
        if (CollectionUtils.isEmpty(specGroups)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(specGroups);

    }

    /**
     * 通用条件查询
     * @param gid
     * @param cid
     * @param generic
     * @param searching
     * @return
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> querySpecParams(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "generic", required = false) Boolean generic,
            @RequestParam(value = "searching", required = false) Boolean searching){

        List<SpecParam> specParams = this.specificationService.querySpecParams(gid, cid, generic, searching);
        if (CollectionUtils.isEmpty(specParams)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(specParams);
    }

    /**
     * 根据分类id查询规格组和规格参数
     * @param cid
     * @return
     */
    @GetMapping("groups/param/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroupsWithParamByCid(@PathVariable("cid") Long cid){

        List<SpecGroup> specGroups = this.specificationService.querySpecGroupsWithParamByCid(cid);
        if (CollectionUtils.isEmpty(specGroups)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(specGroups);

    }




}
