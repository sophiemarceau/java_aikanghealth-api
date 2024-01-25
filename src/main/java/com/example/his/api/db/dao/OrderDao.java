package com.example.his.api.db.dao;

import com.example.his.api.db.pojo.OrderEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @description 针对表【tb_order(订单表)】的数据库操作Mapper
 * @createDate 2023-07-06 12:32:56
 * @Entity com.example.his.api.db.pojo.OrderEntity
 */
public interface OrderDao {
    public HashMap searchFrontStatistic(int customerId);

    public boolean searchIllegalCountInDay(int customerId);

    public int closeOrder();

    public int insert(OrderEntity entity);

    public int updatePayment(Map param);

    public Integer searchCustomerId(String outTradeNo);

    public ArrayList<HashMap> searchFrontOrderByPage(Map param);

    public long searchFrontOrderCount(Map param);

    public String searchAlreadyRefund(int id);

    public HashMap searchRefundNeeded(Map param);

    public int updateOutRefundNo(Map param);

    public int updateRefundsByOutRefundNo(String outRefundNo);
}




