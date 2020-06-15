package com.ray.p2p.service.user;

import com.ray.p2p.common.constants.Constants;
import com.ray.p2p.mapper.user.FinanceAccountMapper;
import com.ray.p2p.mapper.user.UserMapper;
import com.ray.p2p.model.user.FinanceAccount;
import com.ray.p2p.model.user.User;
import com.ray.p2p.model.vo.ResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: Ray Li
 * @E-mail: ray.lir@outlook.com
 * @Date: 2020/5/16 13:42
 */
@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Override
    /**
     * 获取平台注册总人数
     *
     * @param
     * @return java.lang.Long
     * @version 1.0.0
     * @author Ray Li
     */
    public Long queryAllUserCount() {

        //去redis缓存中查询

        //获取到的是指定某一个key的value的操作对象
        BoundValueOperations<String, Object> boundValueOps = redisTemplate.boundValueOps(Constants.ALL_USER_COUNT);

        //获取该key所对应的value
        Long allUserCount = (Long) boundValueOps.get();

        //判断是否有值
        if (null == allUserCount) {

            //去数据库查询，并存放到redis缓存中
            allUserCount = userMapper.selectAllUserCount();

            //将其存放到redis缓存
            boundValueOps.set(allUserCount,15, TimeUnit.SECONDS);

        }



        return allUserCount;
    }

    @Override
    /**
     * 根据手机号查询用户信息
     *
     * @param phone
     * @return com.ray.p2p.model.user.User
     * @version 1.0.0
     * @author Ray Li
     */
    public User queryUserByPhone(String phone) {
        return userMapper.selectUserByPhone(phone);
    }

    @Override
    /**
     * 注册
     *
     * @param phone
	 * @param loginPassword
     * @return com.ray.p2p.model.vo.ResultObject
     * @version 1.0.0
     * @author Ray Li
     */
    public ResultObject register(String phone, String loginPassword) {
        ResultObject resultObject = new ResultObject();
        resultObject.setErrorCode(Constants.SUCCESS);


        //1.新增用户
        User user = new User();
        user.setPhone(phone);
        user.setLoginPassword(loginPassword);
        user.setAddTime(new Date());
        user.setLastLoginTime(new Date());
        int insertUserCount = userMapper.insertSelective(user);

        if (insertUserCount > 0) {

            User userDetail = userMapper.selectUserByPhone(phone);

            //2.新增帐户
            FinanceAccount financeAccount = new FinanceAccount();
            financeAccount.setUid(userDetail.getId());
            financeAccount.setAvailableMoney(888.0);
            int insertFinanceCount = financeAccountMapper.insertSelective(financeAccount);
            if (insertFinanceCount <= 0) {
                resultObject.setErrorCode(Constants.FAIL);
            }

        } else {
            resultObject.setErrorCode(Constants.FAIL);
        }



        return resultObject;
    }

    @Override
    /**
     * 根据用户标识更新用户信息
     *
     * @param user
     * @return int
     * @version 1.0.0
     * @author Ray Li
     */
    public int modifyUserById(User user) {
        return userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    /**
     * 登录
     *
     * @param phone
	 * @param loginPassword
     * @return com.ray.p2p.model.user.User
     * @version 1.0.0
     * @author Ray Li
     */
    public User login(String phone, String loginPassword) {

        //根据手机号和密码查询用户信息
        User user = userMapper.selectUserByPhoneAndLoginPassword(loginPassword,phone);

        //判断用户是否存在
        if (null != user) {

            //更新最近登录时间
            User updateUser = new User();
            updateUser.setId(user.getId());
            updateUser.setLastLoginTime(new Date());
            userMapper.updateByPrimaryKeySelective(updateUser);
        }



        return user;
    }
}






















