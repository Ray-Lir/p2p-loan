package com.ray.p2p.mapper.loan;

import com.ray.p2p.model.loan.RechargeRecord;

public interface RechargeRecordMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_recharge_record
     *
     * @mbggenerated Sat May 15 11:05:12 CST 2020
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_recharge_record
     *
     * @mbggenerated Sat May 15 11:05:12 CST 2020
     */
    int insert(RechargeRecord record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_recharge_record
     *
     * @mbggenerated Sat May 15 11:05:12 CST 2020
     */
    int insertSelective(RechargeRecord record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_recharge_record
     *
     * @mbggenerated Sat May 15 11:05:12 CST 2020
     */
    RechargeRecord selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_recharge_record
     *
     * @mbggenerated Sat May 15 11:05:12 CST 2020
     */
    int updateByPrimaryKeySelective(RechargeRecord record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_recharge_record
     *
     * @mbggenerated Sat May 15 11:05:12 CST 2020
     */
    int updateByPrimaryKey(RechargeRecord record);

    /**
     * 根据充值订单号更新充值记录
     *
     * @param rechargeRecord
     * @return int
     */
    int updateRechargeRecordByRechargeNo(RechargeRecord rechargeRecord);
}