package com.ray.p2p.service.loan;

/**
 * @Description:
 * @Author: Ray Li
 * @E-mail: ray.lir@outlook.com
 * @Date: 2020/5/18 14:43
 */
public interface RedisService {

    /**
     * 使用Redis将value存放到指定key中
     *
     * @param key
	 * @param value
     * @return void
     */
    void put(String key, String value);

    /**
     * 从redis中获取指定key的value值
     *
     * @param key
     * @return java.lang.String
     */
    String get(String key);

    /**
     * 获取redis的唯一数字
     *
     * @param
     * @return java.lang.Long
     */
    Long getOnlyNumber();
}
