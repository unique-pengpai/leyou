package cn.pengpai.item.service;

import cn.pengpai.item.pojo.SpecGroup;
import cn.pengpai.item.pojo.SpecParam;

import java.util.List;

public interface SpecificationService {

    /**
     * 根据分类id查询规格组
     * @param cid
     * @return
     */
    List<SpecGroup> querySpecGroupsByCid(Long cid);

    /**
     * 通用条件查询
     * @param gid
     * @param cid
     * @param generic
     * @param searching
     * @return
     */
    List<SpecParam> querySpecParams(Long gid, Long cid, Boolean generic, Boolean searching);

    /**
     * 根据分类id查询规格组和规格参数
     * @param cid
     * @return
     */
    List<SpecGroup> querySpecGroupsWithParamByCid(Long cid);
}
