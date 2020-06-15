package com.ray.p2p.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description:
 * @Author: Ray Li
 * @E-mail: ray.lir@outlook.com
 * @Date: 2020/5/17 14:36
 */
@Data
public class ResultObject implements Serializable {

    /**
     * 错误码:SUCCESS|FAIL
     */
    private String errorCode;

}
