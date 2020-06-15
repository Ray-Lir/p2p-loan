package com.ray.p2p.web;

import com.ray.p2p.common.constants.Constants;
import com.ray.p2p.model.loan.BidInfo;
import com.ray.p2p.model.loan.LoanInfo;
import com.ray.p2p.model.user.FinanceAccount;
import com.ray.p2p.model.user.User;
import com.ray.p2p.model.vo.PaginationVO;
import com.ray.p2p.service.loan.BidInfoService;
import com.ray.p2p.service.loan.LoanInfoService;
import com.ray.p2p.service.user.FinanceAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: Ray Li
 * @E-mail: ray.lir@outlook.com
 * @Date: 2020/5/16 14:28
 */
@Controller
public class LoanInfoController {

    @Autowired
    private LoanInfoService loanInfoService;

    @Autowired
    private BidInfoService bidInfoService;

    @Autowired
    private FinanceAccountService financeAccountService;

    /**
     * 产品页
     *
     * @param request
	 * @param model
	 * @param ptype
	 * @param currentPage
     * @return java.lang.String
     * @version 1.0.0
     * @author Ray Li
     * @date 2020/6/16 1:44
     */
    @RequestMapping(value = "/loan/loan")
    public String loan(HttpServletRequest request, Model model,
                       @RequestParam (value = "ptype",required = false) Integer ptype,
                       @RequestParam (value = "currentPage",required = false) Integer currentPage) {

        if (null == currentPage) {
            //默认为第1页
            currentPage = 1;
        }

        //准备分页查询的参数
        Map<String,Object> paramMap = new HashMap<String,Object>();
        int pageSize = 9;
        paramMap.put("currentPage",(currentPage-1)*pageSize);
        paramMap.put("pageSize",pageSize);

        //判断产品类型是否有值
        if (null != ptype) {
            paramMap.put("productType",ptype);
        }


        //分页查询产品信息列表(产品类型，页码，每页显示的条数) -> 返回每页显示的数据，总记录数;分页模型对象PaginationVO
        PaginationVO<LoanInfo> paginationVO = loanInfoService.queryLoanInfoByPage(paramMap);

        //计算总页数
        int totalPage = paginationVO.getTotal().intValue() / pageSize;
        int mod = paginationVO.getTotal().intValue() % pageSize;
        if (mod > 0) {
            totalPage = totalPage + 1;
        }

        model.addAttribute("totalRows",paginationVO.getTotal());
        model.addAttribute("totalPage",totalPage);
        model.addAttribute("loanInfoList",paginationVO.getDataList());
        model.addAttribute("currentPage",currentPage);
        if (null != ptype) {
            model.addAttribute("ptype",ptype);
        }


        return "loan";
    }


    /**
     * 产品信息
     *
     * @param request
	 * @param model
	 * @param id
     * @return java.lang.String
     * @version 1.0.0
     * @author Ray Li
     */
    @RequestMapping(value = "/loan/loanInfo")
    public String loanInfo(HttpServletRequest request,Model model,
                           @RequestParam (value = "id",required = true) Integer id) {

        //根据产品标识获取产品详情
        LoanInfo loanInfo = loanInfoService.queryLoanInfoById(id);


        //根据产品标识获取该产品的所有投资记录
        List<BidInfo> bidInfoList = bidInfoService.queryBidInfoListByLoanId(id);

        model.addAttribute("loanInfo",loanInfo);
        model.addAttribute("bidInfoList",bidInfoList);

        //从session中获取用户信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //判断用户是否登录
        if (null != sessionUser) {

            //根据用户标识获取帐户资金信息
            FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(sessionUser.getId());
            model.addAttribute("financeAccount",financeAccount);
        }



        return "loanInfo";
    }
}























