package com.ray.pay.config;

import lombok.Data;

/**
 * @Description:
 * @Author: Ray Li
 * @E-mail: ray.lir@outlook.com
 * @Date: 2020/5/19 14:23
 */
@Data
public class PayConfig {


    private String charset;
    private String alipayGatewayUrl;
    private String appId;
    private String merchantPrivateKey;
    private String alipayPublickey;
    private String signType;
    private String format;
    private String alipayReturnUrl;
    private String alipayNotifyUrl;
}
