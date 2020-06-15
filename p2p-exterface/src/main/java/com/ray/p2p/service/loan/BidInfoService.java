package com.ray.p2p.service.loan;

import com.ray.p2p.model.loan.BidInfo;
import com.ray.p2p.model.vo.ResultObject;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: Ray Li
 * @E-mail: ray.lir@outlook.com
 * @Date: 2020/5/16 11:08
 */
public interface BidInfoService {

    /**
     * 获取平台累计投资金额
     *
     * @param
     * @return java.lang.Double
     */
    Double queryAllBidMoney();

    /**
     * 根据产品标识获取产品的所有投资记录（包含：用户的信息）
     *
     * @param loanId
     * @return java.util.List<com.ray.p2p.model.loan.BidInfo>
     */
    List<BidInfo> queryBidInfoListByLoanId(Integer loanId);

    /**
     * 根据用户标识查询最近投资记录
     *
     * @param paramMap
     * @return java.util.List<com.ray.p2p.model.loan.BidInfo>
     */
    List<BidInfo> queryBidInfoListByUid(Map<String, Object> paramMap);

    /**
     * 用户投资
     *
     * @param paramMap
     * @return com.ray.p2p.model.vo.ResultObject
     */
    ResultObject invest(Map<String, Object> paramMap);
}
