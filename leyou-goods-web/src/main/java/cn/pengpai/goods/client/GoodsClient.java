package cn.pengpai.goods.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "item-service")
public interface GoodsClient extends cn.pengpai.item.api.GoodsApi {
}
