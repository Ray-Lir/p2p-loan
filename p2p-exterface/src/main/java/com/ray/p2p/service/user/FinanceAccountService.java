package com.ray.p2p.service.user;

import com.ray.p2p.model.user.FinanceAccount;

/**
 * @Description:
 * @Author: Ray Li
 * @E-mail: ray.lir@outlook.com
 * @Date: 2020/5/17 15:25
 */
public interface FinanceAccountService {
    /**
     * 根据用户标识获取帐户信息
     *
     * @param uid
     * @return com.ray.p2p.model.user.FinanceAccount
     */
    FinanceAccount queryFinanceAccountByUid(Integer uid);
}
