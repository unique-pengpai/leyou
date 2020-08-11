package cn.pengpai.cart.intercepter;

import cn.pengpai.auth.entity.UserInfo;
import cn.pengpai.auth.utils.JwtUtils;
import cn.pengpai.cart.config.JwtProperties;
import cn.pengpai.commo.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@EnableConfigurationProperties(JwtProperties.class)
public class LoginIntercepter extends HandlerInterceptorAdapter {

    @Autowired
    private JwtProperties jwtProperties;

    public static final ThreadLocal<UserInfo> tl = new ThreadLocal<>();

    /**
     * 同意登陆校验
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取token
        String token = CookieUtils.getCookieValue(request, "LY_TOKEN");
        // 判断是否有token
        if (StringUtils.isBlank(token)){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        UserInfo user = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
        tl.set(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        tl.remove();
    }

    /**
     * 返回user信息
     * @return
     */
    public static UserInfo getUserInfo(){
        return tl.get();
    }
}
