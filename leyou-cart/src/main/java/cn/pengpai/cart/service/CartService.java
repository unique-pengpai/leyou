package cn.pengpai.cart.service;

import cn.pengpai.auth.entity.UserInfo;
import cn.pengpai.cart.client.GoodsClient;
import cn.pengpai.cart.intercepter.LoginIntercepter;
import cn.pengpai.cart.pojo.Cart;
import cn.pengpai.commo.utils.JsonUtils;
import cn.pengpai.item.pojo.Sku;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GoodsClient goodsClient;

    static final String KEY_PREFIX = "leyou:cart:uid:";

    /**
     * 添加购物车
     * @param cart
     */
    public void addCart(Cart cart) {
        // 获取用户信息
        UserInfo user = LoginIntercepter.getUserInfo();
        // 购物车的key
        String key = KEY_PREFIX + user.getId();
        // 判断redis中是否有该条数据
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        Boolean boo = hashOps.hasKey(cart.getSkuId().toString());
        Integer num = cart.getNum();
        if (boo) {
            String json = hashOps.get(cart.getSkuId().toString()).toString();
            cart = JsonUtils.parse(json, Cart.class);
            cart.setNum(cart.getNum() + num);
        }else {
            // 不存在，新增购物车数据
            cart.setUserId(user.getId());
            // 其它商品信息，需要查询商品服务
            Sku sku = this.goodsClient.querySkuBySkuId(cart.getSkuId());
            cart.setImage(StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
            cart.setPrice(sku.getPrice());
            cart.setTitle(sku.getTitle());
            cart.setOwnSpec(sku.getOwnSpec());
        }
        // 保存cart到redis
        hashOps.put(cart.getSkuId().toString(), JsonUtils.serialize(cart));
    }

    /**
     * 查询购物车
     * @return
     */
    public List<Cart> queryCartList() {
        // 获取用户信息
        UserInfo user = LoginIntercepter.getUserInfo();
        // 购物车的key
        String key = KEY_PREFIX + user.getId();
        // 判断用户是否有购物车
        Boolean boo = redisTemplate.hasKey(key);
        if (!boo){
            return null;
        }
        // 获取购物车数据
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);
        List<Object> carts = hashOps.values();
        if (CollectionUtils.isEmpty(carts)){
            return null;
        }
        return carts.stream().map(cart -> JsonUtils.parse(cart.toString(), Cart.class)).collect(Collectors.toList());
    }

    /**
     * 修改商品数量
     * @param cart
     */
    public void updateNum(Cart cart) {
        // 获取用户信息
        UserInfo user = LoginIntercepter.getUserInfo();
        // 购物车的key
        String key = KEY_PREFIX + user.getId();
        // 获取修改的商品
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);
        String json = hashOps.get(cart.getSkuId().toString()).toString();
        Cart cart1 = JsonUtils.parse(json, Cart.class);
        cart1.setNum(cart.getNum());
        // 保存数据
        hashOps.put(cart.getSkuId().toString(), JsonUtils.serialize(cart1));

    }

    /**
     * 删除购物车
     * @param skuId
     */
    public void deleteCart(Long skuId) {
        // 获取用户信息
        UserInfo user = LoginIntercepter.getUserInfo();
        // 购物车的key
        String key = KEY_PREFIX + user.getId();
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        hashOps.delete(skuId);
    }
}
