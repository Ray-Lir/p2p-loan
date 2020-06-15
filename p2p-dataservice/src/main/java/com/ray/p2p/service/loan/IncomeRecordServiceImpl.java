package com.ray.p2p.service.loan;

import com.ray.p2p.common.constants.Constants;
import com.ray.p2p.common.utils.DateUtils;
import com.ray.p2p.mapper.loan.BidInfoMapper;
import com.ray.p2p.mapper.loan.IncomeRecordMapper;
import com.ray.p2p.mapper.loan.LoanInfoMapper;
import com.ray.p2p.mapper.user.FinanceAccountMapper;
import com.ray.p2p.model.loan.BidInfo;
import com.ray.p2p.model.loan.IncomeRecord;
import com.ray.p2p.model.loan.LoanInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: Ray Li
 * @E-mail: ray.lir@outlook.com
 * @Date: 2020/5/18 13:36
 */
@Service("incomeRecordServiceImpl")
public class IncomeRecordServiceImpl implements IncomeRecordService {

    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private BidInfoMapper bidInfoMapper;

    @Autowired
    private IncomeRecordMapper incomeRecordMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Override
    /**
     * 生成收益计划
     *
     * @param
     * @return void
     * @version 1.0.0
     * @author Ray Li
     */
    public void generateIncomeRecordPlan() {
        //查询产品状态为1的已满标产品 -> 返回List<已满标产品>
        List<LoanInfo> loanInfoList = loanInfoMapper.selectLoanInfoListByProductStatus(1);

        //循环遍历，获取到每一个产品
        for (LoanInfo loanInfo : loanInfoList) {

            //根据产品标识获取产品的所有投资记录 -> 返回List<投资记录>
            List<BidInfo> bidInfoList = bidInfoMapper.selectBidInfosByLoanId(loanInfo.getId());

            //循环遍历投资记录
            for (BidInfo bidInfo : bidInfoList) {

                //获取每一条投资记录，将其生成对应的收益记录
                IncomeRecord incomeRecord = new IncomeRecord();

                incomeRecord.setUid(bidInfo.getUid());
                incomeRecord.setLoanId(loanInfo.getId());
                incomeRecord.setBidId(bidInfo.getId());
                incomeRecord.setBidMoney(bidInfo.getBidMoney());
                incomeRecord.setIncomeStatus(0);

                //收益时间(Date) = 满标时间(Date) + 周期(int)
                Date incomeDate = null;

                //收益金额 = 投资金额 * 日利率 * 投资天数
                Double incomeMoney = null;

                if (loanInfo.getProductType() == Constants.PRODUCT_TYPE_X) {
                    //新手宝
                    incomeDate = DateUtils.getDateByAddDays(loanInfo.getProductFullTime(),loanInfo.getCycle());
                    incomeMoney = bidInfo.getBidMoney() * loanInfo.getRate() / 100 / 365 * loanInfo.getCycle();
                } else {
                    //优选和散标
                    incomeDate = DateUtils.getDateByAddMonths(loanInfo.getProductFullTime(),loanInfo.getCycle());
                    incomeMoney = bidInfo.getBidMoney() * loanInfo.getRate() / 100 / 365 * loanInfo.getCycle() * 30;
                }

                incomeMoney = Math.round(incomeMoney * Math.pow(10,2)) / Math.pow(10,2);


                incomeRecord.setIncomeDate(incomeDate);
                incomeRecord.setIncomeMoney(incomeMoney);

                incomeRecordMapper.insertSelective(incomeRecord);

            }


            //将当前产品的状态更新为2满标且生成收益计划
            LoanInfo loanDetail = new LoanInfo();
            loanDetail.setId(loanInfo.getId());
            loanDetail.setProductStatus(2);
            loanInfoMapper.updateByPrimaryKeySelective(loanDetail);
        }





    }


    @Override
    /**
     * 收益返还
     *
     * @param
     * @return void
     * @version 1.0.0
     * @author Ray Li
     */
    public void generateIncomeBack() {
        //查询收益状态为0且收益时间与当前时间相同的收益记录 -> 返回List<收益记录>
        List<IncomeRecord> incomeRecordList = incomeRecordMapper.selectIncomeRecordByIncomeStatusAndCurdate(0);

        //循环遍历，将当前的收益记录返给对应的帐户
        for (IncomeRecord incomeRecord : incomeRecordList) {

            Map<String,Object> paramMap = new HashMap<String,Object>();
            paramMap.put("uid",incomeRecord.getUid());
            paramMap.put("bidMoney",incomeRecord.getBidMoney());
            paramMap.put("incomeMoney",incomeRecord.getIncomeMoney());

            //更新当前收益记录用户的帐户余额
            int updateCount = financeAccountMapper.updateFinanceAccountByIncomeBack(paramMap);

            if (updateCount > 0) {
                //更新当前收益记录的状态为1已返还
                IncomeRecord updateIncomeRecord = new IncomeRecord();
                updateIncomeRecord.setId(incomeRecord.getId());
                updateIncomeRecord.setIncomeStatus(1);
                incomeRecordMapper.updateByPrimaryKeySelective(updateIncomeRecord);
            }

        }

    }

}
