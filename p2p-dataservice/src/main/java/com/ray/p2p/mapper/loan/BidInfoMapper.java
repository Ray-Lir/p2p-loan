package com.ray.p2p.mapper.loan;

import com.ray.p2p.model.loan.BidInfo;

import java.util.List;
import java.util.Map;

public interface BidInfoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Sat May 15 11:05:12 CST 2020
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Sat May 15 11:05:12 CST 2020
     */
    int insert(BidInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Sat May 15 11:05:12 CST 2020
     */
    int insertSelective(BidInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Sat May 15 11:05:12 CST 2020
     */
    BidInfo selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Sat May 15 11:05:12 CST 2020
     */
    int updateByPrimaryKeySelective(BidInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Sat May 15 11:05:12 CST 2020
     */
    int updateByPrimaryKey(BidInfo record);


    /**
     * 获取平台累计投资金额
     *
     * @param
     * @return java.lang.Double
     */
    Double selectAllBidMoney();


    /**
     * 根据产品标识获取产品的所有投资记录（包含：用户的信息）
     *
     * @param loanId
     * @return java.util.List<com.ray.p2p.model.loan.BidInfo>
     */
    List<BidInfo> selectBidInfoListByLoanId(Integer loanId);

    /**
     * 根据用户标识分页查询投资记录
     *
     * @param paramMap
     * @return java.util.List<com.ray.p2p.model.loan.BidInfo>
     */
    List<BidInfo> selectBidInfoByPage(Map<String, Object> paramMap);

    /**
     * 根据产品标识获取产品的所有投资记录
     *
     * @param loanId
     * @return java.util.List<com.ray.p2p.model.loan.BidInfo>
     */
    List<BidInfo> selectBidInfosByLoanId(Integer loanId);
}