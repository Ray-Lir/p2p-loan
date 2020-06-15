package com.ray.p2p.mapper.loan;

import com.ray.p2p.model.loan.IncomeRecord;

import java.util.List;

public interface IncomeRecordMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_income_record
     *
     * @mbggenerated Sat May 15 11:05:12 CST 2020
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_income_record
     *
     * @mbggenerated Sat May 15 11:05:12 CST 2020
     */
    int insert(IncomeRecord record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_income_record
     *
     * @mbggenerated Sat May 15 11:05:12 CST 2020
     */
    int insertSelective(IncomeRecord record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_income_record
     *
     * @mbggenerated Sat May 15 11:05:12 CST 2020
     */
    IncomeRecord selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_income_record
     *
     * @mbggenerated Sat May 15 11:05:12 CST 2020
     */
    int updateByPrimaryKeySelective(IncomeRecord record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_income_record
     *
     * @mbggenerated Sat May 15 11:05:12 CST 2020
     */
    int updateByPrimaryKey(IncomeRecord record);

    /**
     * 根据收益的状态及当前时间获取收益记录
     *
     * @param incomeStatus
     * @return java.util.List<com.ray.p2p.model.loan.IncomeRecord>
     */
    List<IncomeRecord> selectIncomeRecordByIncomeStatusAndCurdate(Integer incomeStatus);
}