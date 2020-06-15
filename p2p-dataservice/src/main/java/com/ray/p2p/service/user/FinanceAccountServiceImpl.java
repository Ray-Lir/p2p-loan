package com.ray.p2p.service.user;

import com.ray.p2p.mapper.user.FinanceAccountMapper;
import com.ray.p2p.model.user.FinanceAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: Ray Li
 * @E-mail: ray.lir@outlook.com
 * @Date: 2020/5/17 15:25
 */
@Service("financeAccountServiceImpl")
public class FinanceAccountServiceImpl implements FinanceAccountService {

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Override
    /**
     * 根据用户标识获取帐户信息
     *
     * @param uid
     * @return com.ray.p2p.model.user.FinanceAccount
     * @version 1.0.0
     * @author Ray Li
     */
    public FinanceAccount queryFinanceAccountByUid(Integer uid) {
        return financeAccountMapper.selectFinanceAccountByUid(uid);
    }
}
