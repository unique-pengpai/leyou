package cn.pengpai.user.service;

import cn.pengpai.user.pojo.User;

public interface UserService {
    /**
     * 校验数据是否可用
     * @param data
     * @param type
     * @return
     */
    Boolean checkUserData(String data, Integer type);

    /**
     * 发送手机验证码
     * @param phone
     * @return
     */
    Boolean sendVerifyCode(String phone);

    /**
     * 用户注册
     * @param user
     * @param code
     * @return
     */
    Boolean register(User user, String code);

    /**
     * 据用户名密码查询用户
     * @param username
     * @param password
     * @return
     */
    User queryUser(String username, String password);
}
