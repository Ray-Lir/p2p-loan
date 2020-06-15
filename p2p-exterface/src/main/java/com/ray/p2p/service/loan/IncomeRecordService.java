package com.ray.p2p.service.loan;

/**
 * @Description:
 * @Author: Ray Li
 * @E-mail: ray.lir@outlook.com
 * @Date: 2020/5/18 13:36
 */
public interface IncomeRecordService {

    /**
     * 生成收益计划
     *
     * @param
     * @return void
     */
    void generateIncomeRecordPlan();

    /**
     * 收益返还
     *
     * @param
     * @return void
     */
    void generateIncomeBack();
}
