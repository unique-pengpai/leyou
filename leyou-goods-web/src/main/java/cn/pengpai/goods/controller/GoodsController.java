package cn.pengpai.goods.controller;

import cn.pengpai.goods.service.GoodsHtmlService;
import cn.pengpai.goods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("item")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GoodsHtmlService goodsHtmlService;

    @GetMapping("{id}.html")
    public String toItemPage(Model model, @PathVariable("id")Long id){

        // 加载全部数据
        Map<String, Object> modelMap = this.goodsService.loadData(id);
        // 把数据放入模型
        model.addAllAttributes(modelMap);

        // 页面静态化
        this.goodsHtmlService.asyncExcute(id);

        return "item";
    }
}
