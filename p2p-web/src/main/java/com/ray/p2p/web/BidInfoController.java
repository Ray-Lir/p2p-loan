package com.ray.p2p.web;

import com.ray.p2p.common.constants.Constants;
import com.ray.p2p.model.user.User;
import com.ray.p2p.model.vo.ResultObject;
import com.ray.p2p.service.loan.BidInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: Ray Li
 * @E-mail: ray.lir@outlook.com
 * @Date: 2020/5/17 13:53
 */
@Controller
public class BidInfoController {

    @Autowired
    private BidInfoService bidInfoService;

    @RequestMapping(value = "/loan/invest")
    /**
     * 投资
     *
     * @param request
	 * @param loanId
	 * @param bidMoney
     * @return java.lang.Object
     * @version 1.0.0
     * @author Ray Li
     */
    public @ResponseBody Object invest(HttpServletRequest request,
                                       @RequestParam (value = "loanId",required = true) Integer loanId,
                                       @RequestParam (value = "bidMoney",required = true) Double bidMoney) {
        Map<String,Object> retMap = new HashMap<String,Object>();

        //从session中获取用户的信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //准备投资的参数
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("uid",sessionUser.getId());
        paramMap.put("loanId",loanId);
        paramMap.put("bidMoney",bidMoney);

        //用户投资【1.更新产品剩余可投金额 2.更新帐户余额 3.新增投资记录 4.判断产品是否满标】(用户标识，产品标识，投资金额) -> 返回ResultObject
        ResultObject resultObject = bidInfoService.invest(paramMap);
        //测试投资的哪些情况
            //1.投资完成，产品未满标
            //2.投资完成，产品满标
            //3.超卖

        //判断投资是否成功
        if (StringUtils.equals(Constants.SUCCESS, resultObject.getErrorCode())) {

            retMap.put(Constants.ERROR_MESSAGE,Constants.OK);

        } else {
            retMap.put(Constants.ERROR_MESSAGE,"投资失败");
            return retMap;
        }


        return retMap;
    }

}


















