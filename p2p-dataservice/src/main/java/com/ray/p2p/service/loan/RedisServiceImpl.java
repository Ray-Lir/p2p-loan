package com.ray.p2p.service.loan;

import com.ray.p2p.common.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: Ray Li
 * @E-mail: ray.lir@outlook.com
 * @Date: 2020/5/18 14:43
 */
@Service("redisServiceImpl")
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    /**
     * 使用Redis将value存放到指定key中
     *
     * @param key
	 * @param value
     * @return void
     * @version 1.0.0
     * @author Ray Li
     */
    public void put(String key, String value) {

        redisTemplate.opsForValue().set(key,value,60, TimeUnit.SECONDS);
    }

    @Override
    /**
     * 从redis中获取指定key的value值
     *
     * @param key
     * @return java.lang.String
     * @version 1.0.0
     * @author Ray Li
     */
    public String get(String key) {
        String messageCode = (String) redisTemplate.opsForValue().get(key);
        return messageCode;
    }


    @Override
    /**
     * 获取redis的唯一数字
     *
     * @param
     * @return java.lang.Long
     * @version 1.0.0
     * @author Ray Li
     */
    public Long getOnlyNumber() {
        return redisTemplate.opsForValue().increment(Constants.ONLY_NUMBER,1);
    }
}
