package com.ray.p2p.service.loan;

import com.ray.p2p.common.constants.Constants;
import com.ray.p2p.mapper.loan.LoanInfoMapper;
import com.ray.p2p.model.loan.LoanInfo;
import com.ray.p2p.model.vo.PaginationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: Ray Li
 * @E-mail: ray.lir@outlook.com
 * @Date: 2020/5/17 15:36
 */
@Service("loanInfoServiceImpl")
public class LoanInfoServiceImpl implements LoanInfoService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Override
    /**
     * 获取历史平均年化收益率
     *
     * @param
     * @return java.lang.Double
     * @version 1.0.0
     * @author Ray Li
     */
    public Double queryHistoryAverageRate() {

        //设置redisTemplate对象key的序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        //首先去redis缓存中查询，有：直接使用，没有：去数据库查询并存放到redis缓存中
        //好处：提升系统的性能，提升用户的体验

        //去redis缓存中获取该值
        Double historyAverageRate = (Double) redisTemplate.opsForValue().get(Constants.HISTORY_AVERAGE_RATE);

        //判断是否有值
        if (null == historyAverageRate) {

            //去数据库查询，并存放到redis缓存中
            historyAverageRate = loanInfoMapper.selectHistoryAverageRate();

            //存放
            redisTemplate.opsForValue().set(Constants.HISTORY_AVERAGE_RATE,historyAverageRate,15, TimeUnit.MINUTES);
        }



        return historyAverageRate;
    }

    @Override
    /**
     * 根据产品类型获取产品信息列表
     *
     * @param paramMap
     * @return java.util.List<com.ray.p2p.model.loan.LoanInfo>
     * @version 1.0.0
     * @author Ray Li
     */
    public List<LoanInfo> queryLoanInfoListByProductType(Map<String, Object> paramMap) {
        return loanInfoMapper.selectLoanInfoByPage(paramMap);
    }

    @Override
    /**
     * 分页查询产品信息列表
     *
     * @param paramMap
     * @return com.ray.p2p.model.vo.PaginationVO<com.ray.p2p.model.loan.LoanInfo>
     * @version 1.0.0
     * @author Ray Li
     */
    public PaginationVO<LoanInfo> queryLoanInfoByPage(Map<String, Object> paramMap) {
        PaginationVO<LoanInfo> paginationVO = new PaginationVO<LoanInfo>();

        Long total = loanInfoMapper.selectTotal(paramMap);

        paginationVO.setTotal(total);

        List<LoanInfo> loanInfoList = loanInfoMapper.selectLoanInfoByPage(paramMap);

        paginationVO.setDataList(loanInfoList);


        return paginationVO;
    }

    @Override
    /**
     * 根据产品标识获取产品详情
     *
     * @param id
     * @return com.ray.p2p.model.loan.LoanInfo
     * @version 1.0.0
     * @author Ray Li
     */
    public LoanInfo queryLoanInfoById(Integer id) {
        return loanInfoMapper.selectByPrimaryKey(id);
    }


}
