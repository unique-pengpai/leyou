package cn.pengpai.goods.service;

import cn.pengpai.goods.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

@Service
public class GoodsHtmlService {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private TemplateEngine templateEngine;

    public void createHtml(Long spuId) {

        PrintWriter writer = null;
        try {
            // 文件输入流
            writer = new PrintWriter(new File("D:\\develop\\nginx-1.14.0\\html\\item\\" + spuId + ".html"));
            // 获取页面数据
            Map<String, Object> map = this.goodsService.loadData(spuId);
            // 创建上下文对象
            Context context = new Context();
            // 把数据放入上下文对象
            context.setVariables(map);
            // 执行页面静态方法
            this.templateEngine.process("item", context, writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if (writer!=null){
                writer.close();
            }
        }

    }

    /**
     * 删除静态页面
     * @param id
     */
    public void deleteHtml(Long id) {
        File file = new File("D:\\develop\\nginx-1.14.0\\html\\item\\" + id + ".html");
        file.deleteOnExit();
    }

    /**
     * 新建线程处理页面静态化
     * @param spuId
     */
    public void asyncExcute(Long spuId) {
        ThreadUtils.execute(()->createHtml(spuId));
        /*ThreadUtils.execute(new Runnable() {
            @Override
            public void run() {
                createHtml(spuId);
            }
        });*/
    }
}
