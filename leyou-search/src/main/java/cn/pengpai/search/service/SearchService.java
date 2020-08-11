package cn.pengpai.search.service;

import cn.pengpai.commo.pojo.PageResult;
import cn.pengpai.item.pojo.Spu;
import cn.pengpai.search.pojo.Goods;
import cn.pengpai.search.pojo.SearchRequest;

import java.io.IOException;

public interface SearchService {

    /**
     * 构建Goods对象
     * @param spu
     * @return
     * @throws IOException
     */
    public Goods buildGoods(Spu spu) throws IOException;

    /**
     * 关键字分页搜索
     * @param searchRequest
     * @return
     */
    PageResult<Goods> search(SearchRequest searchRequest);
}
