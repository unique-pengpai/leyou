package cn.pengpai.auth.service;

import cn.pengpai.auth.client.UserClient;
import cn.pengpai.auth.entity.UserInfo;
import cn.pengpai.auth.properties.JwtProperties;
import cn.pengpai.auth.utils.JwtUtils;
import cn.pengpai.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AuthService {

    @Autowired
    private UserClient userClient;
    @Autowired
    private JwtProperties jwtProperties;

    public String authentication(String username, String password) {
        try {
            // 查询用户名和密码
            User user = userClient.queryUser(username, password);
            if (user == null){
                return null;
            }
            String token = JwtUtils.generateToken(new UserInfo(user.getId(), user.getUsername()), jwtProperties.getPrivateKey(), jwtProperties.getExpire());
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
