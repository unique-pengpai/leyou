package cn.pengpai.item.bo;

import cn.pengpai.item.pojo.Sku;
import cn.pengpai.item.pojo.Spu;
import cn.pengpai.item.pojo.SpuDetail;

import java.util.List;

public class SpuBo extends Spu {

    private String cname; // 商品分类名称

    private String bname; // 商品品牌名称

    private SpuDetail spuDetail; // 商详情

    private List<Sku> skus; // sku列表

    public SpuDetail getSpuDetail() {
        return spuDetail;
    }

    public void setSpuDetail(SpuDetail spuDetail) {
        this.spuDetail = spuDetail;
    }

    public List<Sku> getSkus() {
        return skus;
    }

    public void setSkus(List<Sku> skus) {
        this.skus = skus;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
    }

    @Override
    public String toString() {
        return "SpuBo{" +
                "cname='" + cname + '\'' +
                ", bname='" + bname + '\'' +
                ", spuDetail=" + spuDetail +
                ", skus=" + skus +
                '}';
    }
}
