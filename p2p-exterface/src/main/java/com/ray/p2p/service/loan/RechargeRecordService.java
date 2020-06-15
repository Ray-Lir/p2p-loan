package com.ray.p2p.service.loan;

import com.ray.p2p.model.loan.RechargeRecord;

import java.util.Map;

/**
 * @Description:
 * @Author: Ray Li
 * @E-mail: ray.lir@outlook.com
 * @Date: 2020/5/19 14:43
 */
public interface RechargeRecordService {
    /**
     * 新增充值记录
     *
     * @param rechargeRecord
     * @return int
     */
    int addRechargeRecord(RechargeRecord rechargeRecord);

    /**
     * 根据充值订单号更新充值记录
     *
     * @param rechargeRecord
     * @return int
     */
    int modifyRechargeRecordByRechargeNo(RechargeRecord rechargeRecord);

    /**
     * 用户充值
     *
     * @param paramMap
     * @return int
     */
    int recharge(Map<String, Object> paramMap);
}
