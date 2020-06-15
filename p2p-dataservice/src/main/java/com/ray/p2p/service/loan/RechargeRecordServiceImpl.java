package com.ray.p2p.service.loan;

import com.ray.p2p.mapper.loan.RechargeRecordMapper;
import com.ray.p2p.mapper.user.FinanceAccountMapper;
import com.ray.p2p.model.loan.RechargeRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Description:
 * @Author: Ray Li
 * @E-mail: ray.lir@outlook.com
 * @Date: 2020/5/19 14:43
 */
@Service("rechargeRecordServiceImpl")
public class RechargeRecordServiceImpl implements RechargeRecordService {

    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Override
    /**
     * 新增充值记录
     *
     * @param rechargeRecord
     * @return int
     * @version 1.0.0
     * @author Ray Li
     */
    public int addRechargeRecord(RechargeRecord rechargeRecord) {
        return rechargeRecordMapper.insertSelective(rechargeRecord);
    }

    @Override
    /**
     * 根据充值订单号更新充值记录
     *
     * @param rechargeRecord
     * @return int
     * @version 1.0.0
     * @author Ray Li
     */
    public int modifyRechargeRecordByRechargeNo(RechargeRecord rechargeRecord) {
        return rechargeRecordMapper.updateRechargeRecordByRechargeNo(rechargeRecord);
    }

    @Override
    /**
     * 用户充值
     *
     * @param paramMap
     * @return int
     * @version 1.0.0
     * @author Ray Li
     */
    public int recharge(Map<String, Object> paramMap) {

        //更新帐户可用余额
        int updateFinanceCount = financeAccountMapper.updateFinanceAccountByRecharge(paramMap);

        if (updateFinanceCount > 0) {
            //更新充值记录的状态
            RechargeRecord rechargeRecord = new RechargeRecord();
            rechargeRecord.setRechargeNo((String) paramMap.get("rechargeNo"));
            rechargeRecord.setRechargeStatus("1");
            int updateRechargeCount = rechargeRecordMapper.updateRechargeRecordByRechargeNo(rechargeRecord);

            if (updateFinanceCount <= 0) {
                return 0;
            }
        } else {
            return 0;
        }


        return 1;
    }
}
