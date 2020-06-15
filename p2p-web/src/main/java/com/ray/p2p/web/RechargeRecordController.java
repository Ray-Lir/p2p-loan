package com.ray.p2p.web;

import com.alibaba.fastjson.JSONObject;
import com.ray.p2p.common.constants.Constants;
import com.ray.p2p.common.utils.DateUtils;
import com.ray.p2p.common.utils.HttpClientUtils;
import com.ray.p2p.model.loan.RechargeRecord;
import com.ray.p2p.model.user.User;
import com.ray.p2p.service.loan.RechargeRecordService;
import com.ray.p2p.service.loan.RedisService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: Ray Li
 * @E-mail: ray.lir@outlook.com
 * @Date: 2020/5/18 14:29
 */
@Controller
public class RechargeRecordController {

    @Autowired
    private RedisService redisService;

    @Autowired
    private RechargeRecordService rechargeRecordService;

    @RequestMapping(value = "/loan/toAlipayRecharge")
    public String toAlipayRecharge(HttpServletRequest request, Model model,
                                 @RequestParam (value = "rechargeMoney",required = true) Double rechargeMoney) {
        System.out.println("-----------toAlipayRecharge--------------");
        //从session中获取用户信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //生成一个全局唯一充值订单号 = 时间戳 + redis全局唯一数字
        String rechargeNo = DateUtils.getTimestamp() + redisService.getOnlyNumber();

        //生成充值记录，新增充值记录
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setUid(sessionUser.getId());
        rechargeRecord.setRechargeNo(rechargeNo);
        rechargeRecord.setRechargeMoney(rechargeMoney);
        rechargeRecord.setRechargeStatus("0");
        rechargeRecord.setRechargeDesc("支付宝充值");
        rechargeRecord.setRechargeTime(new Date());

        int addRechargeCount = rechargeRecordService.addRechargeRecord(rechargeRecord);

        if (addRechargeCount > 0) {

            model.addAttribute("p2p_pay_alipay_url","http://localhost:9090/pay/api/alipay");
            model.addAttribute("rechargeNo",rechargeNo);
            model.addAttribute("rechargeMoney",rechargeMoney);
            model.addAttribute("subject","支付宝充值");

        } else {
            model.addAttribute("trade_msg","充值异常，请稍后重试...");
            return "toRechargeBack";
        }

        return "p2pToPayAlipay";
    }

    @RequestMapping(value = "/loan/alipayBack")
    public String alipayBack(HttpServletRequest request,Model model,
                             @RequestParam (value = "out_trade_no",required = true) String out_trade_no,
                             @RequestParam (value = "total_amount",required = true) Double total_amount) throws Exception {

        System.out.println("--------out_trade_no----------"+out_trade_no);

        //获取到的是支付宝同步返回的参数，但是支付宝同步返回的参数是不包含业务处理结果
        //主动调用pay工程的订单查询接口，返回该订单的详情（包含业务处理结果）

        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("out_trade_no",out_trade_no);
        String jsonString = HttpClientUtils.doPost("http://localhost:9090/pay/api/alipayQuery", paramMap);

        //解析json格式的字符串
        JSONObject jsonObject = JSONObject.parseObject(jsonString);

        //获取指定key所对应的Value
        JSONObject tradeQueryResponse = jsonObject.getJSONObject("alipay_trade_query_response");

        //获取通信标识
        String code = tradeQueryResponse.getString("code");

        //判断通信结果
        if (StringUtils.equals("10000", code)) {

            //获取业务处理结果
            String tradeStatus = tradeQueryResponse.getString("trade_status");

            /*交易状态：
            WAIT_BUYER_PAY（交易创建，等待买家付款）
            TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）
            TRADE_SUCCESS（交易支付成功）
            TRADE_FINISHED（交易结束，不可退款）*/

            if (StringUtils.equals("TRADE_CLOSED", tradeStatus)) {
                //更新充值记录的状态为2
                RechargeRecord rechargeRecord = new RechargeRecord();
                rechargeRecord.setRechargeNo(out_trade_no);
                rechargeRecord.setRechargeStatus("2");
                int modifyRechargeCount = rechargeRecordService.modifyRechargeRecordByRechargeNo(rechargeRecord);

                if (modifyRechargeCount <= 0) {
                    model.addAttribute("trade_msg","充值失败，请稍后重试");
                    return "toRechargeBack";
                }
            }

            if (StringUtils.equals("TRADE_SUCCESS", tradeStatus)) {
                //从session中获取用户的信息
                User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

                paramMap.put("uid",sessionUser.getId());
                paramMap.put("rechargeMoney",total_amount);
                paramMap.put("rechargeNo",out_trade_no);

                //给用户充值【1.更新用户的帐户余额 2.更新充值记录的状态为1】
                int rechargeCount = rechargeRecordService.recharge(paramMap);
                if (rechargeCount <= 0) {
                    model.addAttribute("trade_msg","充值失败，请稍后重试");
                    return "toRechargeBack";
                }
            }



        } else {
            model.addAttribute("trade_msg","充值失败，请稍后重试");
            return "toRechargeBack";
        }

        //根据不同的结果，业务流程处理方式不一样



        return "redirect:/loan/myCenter";
    }


    @RequestMapping(value = "/loan/toWxpayRecharge")
    public String toWxpayRecharge(HttpServletRequest request,Model model,
                                @RequestParam (value = "rechargeMoney",required = true) Double rechargeMoney) {
        System.out.println("---------------toWxpayRecharge---------------------");

        //从session中获取用户信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //生成一个全局唯一充值订单号 = 时间戳 + redis全局唯一数字
        String rechargeNo = DateUtils.getTimestamp() + redisService.getOnlyNumber();

        //生成充值记录，新增充值记录
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setUid(sessionUser.getId());
        rechargeRecord.setRechargeNo(rechargeNo);
        rechargeRecord.setRechargeMoney(rechargeMoney);
        rechargeRecord.setRechargeStatus("0");
        rechargeRecord.setRechargeDesc("微信充值");
        rechargeRecord.setRechargeTime(new Date());

        int addRechargeCount = rechargeRecordService.addRechargeRecord(rechargeRecord);

        if (addRechargeCount > 0) {
            model.addAttribute("rechargeNo",rechargeNo);
            model.addAttribute("rechargeMoney",rechargeMoney);
            model.addAttribute("rechargeTime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        } else {
            model.addAttribute("trade_msg","充值异常，请稍后重试");
            return "toRechargeBack";
        }



        return "showQRCode";
    }


    @RequestMapping (value = "/loan/generateQRCode")
    public void generateQRCode(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam (value = "rechargeNo",required = true) String rechargeNo,
                               @RequestParam (value = "rechargeMoney",required = true) Double rechargeMoney) throws Exception {

        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("body","微信充值");
        paramMap.put("out_trade_no",rechargeNo);
        paramMap.put("total_fee",rechargeMoney);

        //调用pay工程的统一下单API接口，返回json格式的字符串
        String jsonString = HttpClientUtils.doPost("http://localhost:9090/pay/api/wxpay", paramMap);

        //解析json格式的字符串
        JSONObject jsonObject = JSONObject.parseObject(jsonString);

        //判断通信标识
        String returnCode = jsonObject.getString("return_code");

        if (StringUtils.equals(Constants.SUCCESS, returnCode)) {

            //判断业务处理结果
            String resultCode = jsonObject.getString("result_code");

            if (StringUtils.equals(Constants.SUCCESS, resultCode)) {

                //获取code_url字符串
                String codeUrl = jsonObject.getString("code_url");

                //将code_url生成一个二维码
                Map<EncodeHintType,Object> encodeHintTypeObjectMap = new HashMap<EncodeHintType, Object>();
                encodeHintTypeObjectMap.put(EncodeHintType.CHARACTER_SET,"UTF-8");

                //创建一个矩阵对象
                BitMatrix bitMatrix = new MultiFormatWriter().encode(codeUrl, BarcodeFormat.QR_CODE,200,200,encodeHintTypeObjectMap);

                OutputStream outputStream = response.getOutputStream();

                //将矩阵对象转换为图片
                MatrixToImageWriter.writeToStream(bitMatrix,"png",outputStream);

                outputStream.flush();
                outputStream.close();



            } else {
                response.sendRedirect(request.getContextPath() + "/toRechargeBack.jsp");
            }

        } else {
            response.sendRedirect(request.getContextPath() + "/toRechargeBack.jsp");
        }


    }
}
