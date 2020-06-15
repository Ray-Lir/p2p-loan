package com.ray.p2p.service.loan;

import com.ray.p2p.model.loan.LoanInfo;
import com.ray.p2p.model.vo.PaginationVO;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: Ray Li
 * @E-mail: ray.lir@outlook.com
 * @Date: 2020/5/17 15:36
 */
public interface LoanInfoService {

    /**
     * 获取历史平均年化收益率
     *
     * @param
     * @return java.lang.Double
     */
    Double queryHistoryAverageRate();

    /**
     * 根据产品类型获取产品信息列表
     *
     * @param paramMap
     * @return java.util.List<com.ray.p2p.model.loan.LoanInfo>
     */
    List<LoanInfo> queryLoanInfoListByProductType(Map<String, Object> paramMap);

    /**
     * 分页查询产品信息列表
     *
     * @param paramMap
     * @return com.ray.p2p.model.vo.PaginationVO<com.ray.p2p.model.loan.LoanInfo>
     */
    PaginationVO<LoanInfo> queryLoanInfoByPage(Map<String, Object> paramMap);

    /**
     * 根据产品标识获取产品详情
     *
     * @param id
     * @return com.ray.p2p.model.loan.LoanInfo
     */
    LoanInfo queryLoanInfoById(Integer id);
}
