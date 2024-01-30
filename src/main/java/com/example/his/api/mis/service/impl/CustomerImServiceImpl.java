package com.example.his.api.mis.service.impl;

import com.example.his.api.db.dao.CustomerDao;
import com.example.his.api.db.dao.CustomerImDao;
import com.example.his.api.db.dao.OrderDao;
import com.example.his.api.mis.service.CustomerImService;
import com.tencentyun.TLSSigAPIv2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;

@Service("MisCustomerImServiceImpl")
public class CustomerImServiceImpl implements CustomerImService {
    @Value("${tencent.im.sdkAppId}")
    private Long sdkAppId;
    @Value("${tencent.im.secretKey}")
    private String secretKey;
    @Value("${tencent.im.managerId}")
    private String managerId;
    @Value("${tencent.im.customerServiceId}")
    private String customerServiceId;
    @Resource
    private CustomerDao customerDao;
    @Resource
    private CustomerImDao customerImDao;
    @Resource
    private OrderDao orderDao;

    @Override
    public HashMap searchServiceAccount() {
        TLSSigAPIv2 api = new TLSSigAPIv2(sdkAppId, secretKey);
        //生成客户账号签名
        String useSig = api.genUserSig(customerServiceId, 180 * 6400);
        //保存返回的结果
        HashMap result = new HashMap();
        result.put("sdkAppid", sdkAppId);
        result.put("account", customerServiceId);
        result.put("userSig", useSig);
        return result;
    }

    @Override
    public HashMap searchSummary(int id) {
        HashMap map = customerDao.searchById(id);
        map.putAll(orderDao.searchFrontStatistic(id));
        return map;
    }
}
