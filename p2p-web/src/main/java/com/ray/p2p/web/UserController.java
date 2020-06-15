package com.ray.p2p.web;

import com.alibaba.fastjson.JSONObject;
import com.ray.p2p.common.utils.HttpClientUtils;
import com.ray.p2p.model.loan.BidInfo;
import com.ray.p2p.model.user.FinanceAccount;
import com.ray.p2p.model.user.User;
import com.ray.p2p.model.vo.ResultObject;
import com.ray.p2p.service.loan.BidInfoService;
import com.ray.p2p.service.loan.LoanInfoService;
import com.ray.p2p.service.loan.RedisService;
import com.ray.p2p.service.user.FinanceAccountService;
import com.ray.p2p.service.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: Ray Li
 * @E-mail: ray.lir@outlook.com
 * @Date: 2020/5/18 14:19
 */
@Controller
//@RestController // 类上加@Controller + 该类中所有方法返回的都是jsonc对象，方法上都有@ResponseBody
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FinanceAccountService financeAccountService;

    @Autowired
    private LoanInfoService loanInfoService;

    @Autowired
    private BidInfoService bidInfoService;

    @Autowired
    private RedisService redisService;


    /**
     * 验证手机号
     *
     * URL:http://localhost:8080/p2p/loan/checkPhone
     * @param request
	 * @param phone
     * @return JSON对象{"errorMessage":"OK"}
     * @version 1.0.0
     * @author Ray Li
     */
    @RequestMapping (value = "/loan/checkPhone")
    @ResponseBody
    public Object checkPhone(HttpServletRequest request,
                                           @RequestParam (value = "phone",required = true) String phone) {
        Map<String,Object> retMap = new HashMap<String,Object>();


        //根据手机号查询用户是否存在【根据手机号查询用户信息】(手机号码) -> 返回boolean|int|User
        User user = userService.queryUserByPhone(phone);

        //判断用户是否存在
        if (null != user) {
            retMap.put(Constants.ERROR_MESSAGE,"该手机号码已被注册，请更新手机号码");
            return retMap;
        }

        retMap.put(Constants.ERROR_MESSAGE,Constants.OK);

        return retMap;
    }



    /**
     * 验证码
     *
     * @param request
	 * @param captcha
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @version 1.0.0
     * @author Ray Li
     */
    @GetMapping(value = "/loan/checkCaptcha")
    @ResponseBody
    public Map<String,Object> checkCaptcha(HttpServletRequest request,
                                           @RequestParam (value = "captcha",required = true) String captcha) {
        Map<String,Object> retMap = new HashMap<String,Object>();

        //从session中获取图形验证码
        String sessionCaptcha = (String) request.getSession().getAttribute(Constants.CAPTCHA);

        //用户输入的验证码与session中作对比
        if (!StringUtils.equalsIgnoreCase(sessionCaptcha, captcha)) {
            retMap.put(Constants.ERROR_MESSAGE,"请输入正确的图形验证码");
            return retMap;
        }

        retMap.put(Constants.ERROR_MESSAGE,Constants.OK);

        return retMap;
    }

    /**
     * 注册
     *
     * @param request
	 * @param phone
	 * @param loginPassword
     * @return java.lang.Object
     * @version 1.0.0
     * @author Ray Li
     */
    @RequestMapping (value = "/loan/register")
    @ResponseBody
    public Object register(HttpServletRequest request,
                           @RequestParam (value = "phone",required = true) String phone,
                           @RequestParam (value = "loginPassword",required = true) String loginPassword) {
        Map<String,Object> retMap = new HashMap<String,Object>();

        //用户注册【1.新增用户信息 2.新增帐户信息】（手机号，登录密码） -> 返回业务处理对象ResultObject
        ResultObject resultObject = userService.register(phone,loginPassword);

        //判断是否注册成功
        if (StringUtils.equals(resultObject.getErrorCode(), Constants.SUCCESS)) {

            //将用户的信息存放到session中
            request.getSession().setAttribute(Constants.SESSION_USER,userService.queryUserByPhone(phone));

            retMap.put(Constants.ERROR_MESSAGE,Constants.OK);
        } else {
            retMap.put(Constants.ERROR_MESSAGE,"注册失败");
            return retMap;
        }

        return retMap;
    }

    @RequestMapping(value = "/loan/myFinanceAccount")
    @ResponseBody
    public FinanceAccount myFinanceAccount(HttpServletRequest request) {
        //从session中获取用户信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //根据用户标识获取帐户信息
        FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(sessionUser.getId());

        return financeAccount;
    }



    /**
     * 实名认证
     *
     * @param request
	 * @param idCard
	 * @param realName
     * @return java.lang.Object
     * @version 1.0.0
     * @author Ray Li
     */
    @PostMapping(value = "/loan/verifyRealName")
    @ResponseBody
    public Object verifyRealName(HttpServletRequest request,
                                 @RequestParam (value = "idCard",required = true) String idCard,
                                 @RequestParam (value = "realName",required = true) String realName) throws Exception {
        Map<String,Object> retMap = new HashMap<String,Object>();

        //进行实名认证

        //准备实名认证的参数
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("appkey","cfc4a9a91550b7d0a4edde1450");
        paramMap.put("cardNo",idCard);
        paramMap.put("realName",realName);

        //调用互联网接口，认证用户的真实姓名和身份证号码是否匹配
        String jsonString = HttpClientUtils.doPost("https://way.jd.com/youhuoBeijing/test", paramMap);
//        String jsonString = "{\"code\":\"10000\",\"charge\":false,\"remain\":1305,\"msg\":\"查询成功\",\"result\":{\"error_code\":0,\"reason\":\"成功\",\"result\":{\"realname\":\"乐天磊\",\"idcard\":\"350721197702134399\",\"isok\":true}}}";

        //该互联网接口，返回的是json格式的字符串（从该字符串中获取业务处理结果）
        //解析json格式的字符串，使用fastjson来解析json格式的字符串
        //将json格式的字符串转换为JSON对象
        JSONObject jsonObject = JSONObject.parseObject(jsonString);

        //获取通信标识
        String code = jsonObject.getString("code");

        //判断通信是否成功
        if (StringUtils.equals("10000", code)) {

            //获取业务处理结果isok
            Boolean isok = jsonObject.getJSONObject("result").getJSONObject("result").getBoolean("isok");

            if (isok) {
                //实名认证成功
                //从session中获取用户信息
                User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

                //匹配：更新用户的信息
                User updateUser = new User();
                updateUser.setId(sessionUser.getId());
                updateUser.setName(realName);
                updateUser.setIdCard(idCard);
                int updateUserCount = userService.modifyUserById(updateUser);

                if (updateUserCount > 0) {

                    retMap.put(Constants.ERROR_MESSAGE,Constants.OK);
                    request.getSession().setAttribute(Constants.SESSION_USER,userService.queryUserByPhone(sessionUser.getPhone()));

                } else {
                    retMap.put(Constants.ERROR_MESSAGE,"实名认证失败，请稍后重试...");
                    return retMap;
                }

            } else {
                retMap.put(Constants.ERROR_MESSAGE,"实名认证失败，请稍后重试...");
                return retMap;
            }

        } else {
            //通信失败
            retMap.put(Constants.ERROR_MESSAGE,jsonObject.getString("msg"));
            return retMap;
        }



        return retMap;
    }


    /**
     * 退出
     *
     * @param request
     * @return java.lang.String
     * @version 1.0.0
     * @author Ray Li
     */
    @RequestMapping(value = "/loan/logout")
    public String logout(HttpServletRequest request) {

        //1.让session失效 2.指定移除session中key所对应的值
        request.getSession().removeAttribute(Constants.SESSION_USER);
//        request.getSession().invalidate();

        return "redirect:/index";
    }

    @RequestMapping(value = "/loan/loadStat")
    public @ResponseBody Object loadStat(HttpServletRequest request) {
        Map<String,Object> retMap = new HashMap<String,Object>();

        //获取历史平均年化收益率
        Double historyAverageRate = loanInfoService.queryHistoryAverageRate();

        //获取平台注册总人数
        Long allUserCount = userService.queryAllUserCount();

        //获取平台累计投资金额
        Double allBidMoney = bidInfoService.queryAllBidMoney();

        retMap.put(Constants.HISTORY_AVERAGE_RATE,historyAverageRate);
        retMap.put(Constants.ALL_USER_COUNT,allUserCount);
        retMap.put(Constants.ALL_BID_MONEY,allBidMoney);

        return retMap;
    }


    /**
     * 登录
     *
     * @param request
	 * @param phone
	 * @param loginPassword
	 * @param messageCode
     * @return java.lang.Object
     * @version 1.0.0
     * @author Ray Li
     */
    @RequestMapping(value = "/loan/login")
    public @ResponseBody Object login(HttpServletRequest request,
                                      @RequestParam (value = "phone",required = true) String phone,
                                      @RequestParam (value = "loginPassword",required = true) String loginPassword,
                                      @RequestParam (value = "messageCode",required = true) String messageCode) {
        Map<String,Object> retMap = new HashMap<String,Object>();


        //获取redis中手机号对应的短信验证码
        String redisMessageCode = redisService.get(phone);

        //判断短信验证码是否正确
        if (StringUtils.equals(messageCode, redisMessageCode)) {

            //登录的业务【1.根据手机号和登录密码查询用户信息 2.更新用户的登录时间】(手机号，登录密码) -> 返回User
            User user = userService.login(phone, loginPassword);

            //判断用户是否登录
            if (null == user) {
                retMap.put(Constants.ERROR_MESSAGE, "用户名或密码有误，请重新输入");
                return retMap;
            }

            //将用户的信息存放到session中
            request.getSession().setAttribute(Constants.SESSION_USER, user);

            retMap.put(Constants.ERROR_MESSAGE, Constants.OK);
        } else {
            retMap.put(Constants.ERROR_MESSAGE,"请输入正确的短信验证码");
            return retMap;
        }



        return retMap;
    }


    /**
     * 个人中心
     *
     * @param request
	 * @param model
     * @return java.lang.String
     * @version 1.0.0
     * @author Ray Li
     */
    @RequestMapping(value = "/loan/myCenter")
    public String myCenter(HttpServletRequest request, Model model) {


        //从session中获取用户信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //根据用户标识获取帐户余额
//        ctrl+Q显示方法的说明
        FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(sessionUser.getId());

        //将以下查询看作是一个分页，显示第1页，每页显示5条记录，都当前登录的用户
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("uid",sessionUser.getId());
        paramMap.put("currentPage",0);
        paramMap.put("pageSize",5);

        //根据用户标识获取最近的投资记录
        List<BidInfo> bidInfoList = bidInfoService.queryBidInfoListByUid(paramMap);


        //根据用户标识获取最近的充值记录


        //根据用户标识获取最近的收益记录

        model.addAttribute("financeAccount",financeAccount);
        model.addAttribute("bidInfoList",bidInfoList);


        return "myCenter";
    }


    /**
     * 短信验证
     *
     * @param request
	 * @param phone
     * @return java.lang.Object
     * @version 1.0.0
     * @author Ray Li
     */
    @RequestMapping(value = "/loan/messageCode")
    public @ResponseBody Object messageCode(HttpServletRequest request,
                                            @RequestParam (value = "phone",required = true) String phone) throws Exception {
        Map<String,Object> retMap = new HashMap<String,Object>();

        //生成一个随机数字
        String messageCode = this.getRandomNumber(4);

        //发送短信的内容=短信签名+短信正文(包含：一个随机数字)
        String content = "【凯信通】您的验证码是："+messageCode;

        //准备发送短信的参数
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("appkey","dd39489ee52a85359edbe3b51b");
        paramMap.put("mobile",phone);
        paramMap.put("content",content);

        //发送短信：调用互联网接口发送
//        String jsonString = HttpClientUtils.doPost("https://way.jd.com/kaixintong/kaixintong", paramMap);
        String jsonString = "{\"code\":\"10000\",\"charge\":false,\"remain\":0,\"msg\":\"查询成功\",\"result\":\"<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\" ?><returnsms>\\n <returnstatus>Success</returnstatus>\\n <message>ok</message>\\n <remainpoint>-616780</remainpoint>\\n <taskID>78950259</taskID>\\n <successCounts>1</successCounts></returnsms>\"}";

        //解析json格式的字符串
        //将json字符串转换为json对象
        JSONObject jsonObject = JSONObject.parseObject(jsonString);

        //获取通信标识
        String code = jsonObject.getString("code");

        //判断通信结果
        if (StringUtils.equals("10000", code)) {

            //获取result的值,result是一个xml格式的字符串
            String result = jsonObject.getString("result");

            //使用dom4j+xpath解析xml格式的字符串
            //将xml格式的字符串转换为dom对象
            Document document = DocumentHelper.parseText(result);

            //获取returnstatus
            Node node = document.selectSingleNode("//returnstatus");

            //获取节点对象的文本内容
            String returnStatus = node.getText();

            //判断短信是否发送成功
            if (StringUtils.equals(returnStatus, "Success")) {

                retMap.put(Constants.ERROR_MESSAGE,Constants.OK);

                //将生成的验证码存放到redis缓存中
                redisService.put(phone,messageCode);

                retMap.put("messageCode",messageCode);


            } else {
                retMap.put(Constants.ERROR_MESSAGE,"短信发送失败，请稍后重试...");
                return retMap;
            }


        } else {
            retMap.put(Constants.ERROR_MESSAGE,jsonObject.getString("msg"));
            return retMap;
        }


        return retMap;
    }

    private String getRandomNumber(int count) {
        String[] arr = {"1","2","3","4","5","6","7","8","9","0"};

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < count; i++) {
            int index = (int) Math.round(Math.random()*9);
            String number = arr[index];
            sb.append(number);
        }

        return sb.toString();
    }


}








































