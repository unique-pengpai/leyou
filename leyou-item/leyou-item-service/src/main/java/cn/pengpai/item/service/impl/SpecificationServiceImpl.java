package cn.pengpai.item.service.impl;

import cn.pengpai.item.mapper.SpecGroupMapper;
import cn.pengpai.item.mapper.SpecParamMapper;
import cn.pengpai.item.pojo.SpecGroup;
import cn.pengpai.item.pojo.SpecParam;
import cn.pengpai.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private SpecGroupMapper specGroupMapper;
    @Autowired
    private SpecParamMapper specParamMapper;

    /**
     * 根据分类id查询规格组
     *
     * @param cid
     * @return
     */
    @Override
    public List<SpecGroup> querySpecGroupsByCid(Long cid) {

        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        return this.specGroupMapper.select(specGroup);
    }

    /**
     * 通用条件查询
     *
     * @param gid
     * @param cid
     * @param generic
     * @param searching
     * @return
     */
    @Override
    public List<SpecParam> querySpecParams(Long gid, Long cid, Boolean generic, Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setGeneric(generic);
        specParam.setSearching(searching);
        return this.specParamMapper.select(specParam);
    }

    /**
     * 根据分类id查询规格组和规格参数
     * @param cid
     * @return
     */
    @Override
    public List<SpecGroup> querySpecGroupsWithParamByCid(Long cid) {
        List<SpecGroup> groups = this.querySpecGroupsByCid(cid);
        for (SpecGroup group : groups) {
            group.setParams(this.querySpecParams(group.getId(), null, null, null));
        }
        return groups;
    }
}
