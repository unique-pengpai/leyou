package cn.pengpai.search.controller;

import cn.pengpai.commo.pojo.PageResult;
import cn.pengpai.search.pojo.Goods;
import cn.pengpai.search.pojo.SearchRequest;
import cn.pengpai.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 关键字分页搜索
     * @param searchRequest
     * @return
     */
    @PostMapping("page")
    public ResponseEntity<PageResult<Goods>> search(@RequestBody SearchRequest searchRequest){
        PageResult<Goods> goodsPageResult = this.searchService.search(searchRequest);
        if (goodsPageResult == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(goodsPageResult);
    }

}
