package com.ray.p2p.service.user;

import com.ray.p2p.model.user.User;
import com.ray.p2p.model.vo.ResultObject;

/**
 * @Description:
 * @Author: Ray Li
 * @E-mail: ray.lir@outlook.com
 * @Date: 2020/5/16 13:42
 */
public interface UserService {

    /**
     * 获取平台注册总人数
     *
     * @param
     * @return java.lang.Long
     */
    Long queryAllUserCount();

    /**
     * 根据手机号查询用户信息
     *
     * @param phone
     * @return com.ray.p2p.model.user.User
     */
    User queryUserByPhone(String phone);

    /**
     * 注册
     *
     * @param phone
	 * @param loginPassword
     * @return com.ray.p2p.model.vo.ResultObject
     */
    ResultObject register(String phone, String loginPassword);

    /**
     * 根据用户标识更新用户信息
     *
     * @param user
     * @return int
     */
    int modifyUserById(User user);

    /**
     * 登录
     *
     * @param phone
	 * @param loginPassword
     * @return com.ray.p2p.model.user.User
     */
    User login(String phone, String loginPassword);
}
