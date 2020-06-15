package com.ray.p2p.model.vo;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @Author: Ray Li
 * @E-mail: ray.lir@outlook.com
 * @Date: 2020/5/16 13:51
 */
public class PaginationVO<T> implements Serializable {

    /**
     * 数据总记录数
     */
    private Long total;

    /**
     * 每页显示的数据
     */
    private List<T> dataList;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }
}
