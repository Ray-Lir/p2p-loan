package com.ray.p2p.service.loan;

import com.ray.p2p.common.constants.Constants;
import com.ray.p2p.mapper.loan.BidInfoMapper;
import com.ray.p2p.mapper.loan.LoanInfoMapper;
import com.ray.p2p.mapper.user.FinanceAccountMapper;
import com.ray.p2p.model.loan.BidInfo;
import com.ray.p2p.model.loan.LoanInfo;
import com.ray.p2p.model.vo.ResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: Ray Li
 * @E-mail: ray.lir@outlook.com
 * @Date: 2020/5/16 11:08
 */
@Service("bidInfoServiceImpl")
public class BidInfoServiceImpl implements BidInfoService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private BidInfoMapper bidInfoMapper;

    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Override
    /**
     * 获取平台累计投资金额
     *
     * @param
     * @return java.lang.Double
     * @version 1.0.0
     * @author Ray Li
     */
    public Double queryAllBidMoney() {

        //首先去redis缓存中获取该值
        Double allBidMoney = (Double) redisTemplate.opsForValue().get(Constants.ALL_BID_MONEY);

        //判断是否有值
        if (null == allBidMoney) {

            //去数据库查询
            allBidMoney = bidInfoMapper.selectAllBidMoney();

            //将该值存放到redis缓存中
            redisTemplate.opsForValue().set(Constants.ALL_BID_MONEY,allBidMoney,15, TimeUnit.MINUTES);

        }


        return allBidMoney;
    }

    @Override
    /**
     * 根据产品标识获取产品的所有投资记录（包含：用户的信息）
     *
     * @param loanId
     * @return java.util.List<com.ray.p2p.model.loan.BidInfo>
     * @version 1.0.0
     * @author Ray Li
     */
    public List<BidInfo> queryBidInfoListByLoanId(Integer loanId) {

        return bidInfoMapper.selectBidInfoListByLoanId(loanId);
    }

    @Override
    /**
     * 根据用户标识查询最近投资记录
     *
     * @param paramMap
     * @return java.util.List<com.ray.p2p.model.loan.BidInfo>
     * @version 1.0.0
     * @author Ray Li
     */
    public List<BidInfo> queryBidInfoListByUid(Map<String, Object> paramMap) {
        return bidInfoMapper.selectBidInfoByPage(paramMap);
    }

    @Override
    /**
     * 用户投资
     *
     * @param paramMap
     * @return com.ray.p2p.model.vo.ResultObject
     * @version 1.0.0
     * @author Ray Li
     */
    public ResultObject invest(Map<String, Object> paramMap) {
        ResultObject resultObject = new ResultObject();
        resultObject.setErrorCode(Constants.SUCCESS);

        Integer uid = (Integer) paramMap.get("uid");
        Integer loanId = (Integer) paramMap.get("loanId");
        Double bidMoney = (Double) paramMap.get("bidMoney");


        //1.更新产品剩余可投金额
        //超卖现象：实际销售的数量超过库存数量
        //解决方案：使用数据库乐观机制
        //获取产品的版本号
        LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey(loanId);
        paramMap.put("version",loanInfo.getVersion());

        int updateLeftProductMoneyCount = loanInfoMapper.updateLeftProductMoneyByInvest(paramMap);

        if (updateLeftProductMoneyCount > 0) {

            //2.更新帐户可用余额
            int updateFinanceCount = financeAccountMapper.updateFinanceAccountByInvest(paramMap);

            if (updateFinanceCount > 0) {
                //3.新增投资记录
                BidInfo bidInfo = new BidInfo();

                bidInfo.setUid(uid);
                bidInfo.setLoanId(loanId);
                bidInfo.setBidMoney(bidMoney);
                bidInfo.setBidTime(new Date());
                bidInfo.setBidStatus(1);

                int insertBidInfoCount = bidInfoMapper.insertSelective(bidInfo);

                if (insertBidInfoCount > 0) {
                    //获取当前产品的剩余可投金额
                    LoanInfo loanDetail = loanInfoMapper.selectByPrimaryKey(loanId);

                    //4.判断产品是否满标
                    if (0 == loanDetail.getLeftProductMoney()) {

                        //该产品已满标，更新产品的状态及满标时间
                        LoanInfo updateLoanInfo = new LoanInfo();
                        updateLoanInfo.setId(loanId);
                        updateLoanInfo.setProductStatus(1);
                        updateLoanInfo.setProductFullTime(new Date());
                        int updateCount = loanInfoMapper.updateByPrimaryKeySelective(updateLoanInfo);
                        if (updateCount <= 0) {
                            resultObject.setErrorCode(Constants.FAIL);
                        }

                    }


                } else {
                    resultObject.setErrorCode(Constants.FAIL);

                }

            } else {
                resultObject.setErrorCode(Constants.FAIL);

            }

        } else {
            resultObject.setErrorCode(Constants.FAIL);
        }






        return resultObject;
    }
}



















