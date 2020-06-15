package com.ray.p2p.web;

import com.ray.p2p.common.constants.Constants;
import com.ray.p2p.model.loan.LoanInfo;
import com.ray.p2p.service.loan.BidInfoService;
import com.ray.p2p.service.loan.LoanInfoService;
import com.ray.p2p.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: Ray Li
 * @E-mail: ray.lir@outlook.com
 * @Date: 2020/5/16 14:47
 */
@Controller
public class IndexController {

    @Autowired
    private LoanInfoService loanInfoService;

    @Autowired
    private UserService userService;

    @Autowired
    private BidInfoService bidInfoService;


    @RequestMapping(value = "/index")
    /**
     * 首页
     *
     * @param request
	 * @param model
     * @return java.lang.String
     * @version 1.0.0
     * @author Ray Li
     */
    public String index(HttpServletRequest request, Model model) {

        //获取平台历史平均年化收益率
        Double historyAverageRate = loanInfoService.queryHistoryAverageRate();
        model.addAttribute(Constants.HISTORY_AVERAGE_RATE,historyAverageRate);

        //获取平台注册总人数
        Long allUserCount = userService.queryAllUserCount();
        model.addAttribute(Constants.ALL_USER_COUNT,allUserCount);


        //获取平台累计投资金额
        Double allBidMoney = bidInfoService.queryAllBidMoney();
        model.addAttribute(Constants.ALL_BID_MONEY,allBidMoney);

        //将以下查询看作是一个分页，根据产品类型获取产品信息列表(产品类型，页码，显示条数) -> 返回List<产品>
        //使用mysql中的limit 起始下标,截取长度
        //(起始下标-1)*pageSize

        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("currentPage",0);

        //获取新手宝，产品类型为0，显示第1页，每页显示1条
        paramMap.put("productType",Constants.PRODUCT_TYPE_X);
        paramMap.put("pageSize",1);
        List<LoanInfo> xLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("xLoanInfoList",xLoanInfoList);

        //获取优选产品，产品类型为1，显示第1页，每页显示4条
        paramMap.put("productType",Constants.PRODUCT_TYPE_U);
        paramMap.put("pageSize",4);
        List<LoanInfo> uLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("uLoanInfoList",uLoanInfoList);

        //获取散标产品：产品类型为2，显示第1页，每页显示8条
        paramMap.put("productType",Constants.PRODUCT_TYPE_S);
        paramMap.put("pageSize",8);
        List<LoanInfo> sLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("sLoanInfoList",sLoanInfoList);



        return "index";
    }
}
