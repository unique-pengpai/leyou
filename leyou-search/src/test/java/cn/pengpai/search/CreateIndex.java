package cn.pengpai.search;

import cn.pengpai.commo.pojo.PageResult;
import cn.pengpai.item.bo.SpuBo;
import cn.pengpai.search.client.GoodsClient;
import cn.pengpai.search.pojo.Goods;
import cn.pengpai.search.repository.GoodsRepository;
import cn.pengpai.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CreateIndex {

    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SearchService searchService;

    @Autowired
    private GoodsRepository goodsRepository;

    @Test
    public void createIndex() {

        // 创建索引和映射
        this.template.createIndex(Goods.class);
        this.template.putMapping(Goods.class);

        Integer page = 1;
        Integer rows = 50;

        do {
            // 查询出所有的spu
            PageResult<SpuBo> spuBoPageResult = this.goodsClient.querySpuBoByPage(null, true, page, rows);
            // 转换为Goods对象
            List<Goods> goodsList = spuBoPageResult.getItems().stream().map(spuBo -> {
                try {
                    return this.searchService.buildGoods(spuBo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
            System.out.println(goodsList.size());
            // 保存数据
            this.goodsRepository.saveAll(goodsList);

            rows = spuBoPageResult.getItems().size();
            page++;
        } while (rows == 50);

    }
}
