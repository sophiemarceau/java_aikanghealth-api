package com.example.his.api.front.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.his.api.db.dao.CustomerDao;
import com.example.his.api.db.dao.CustomerImDao;
import com.example.his.api.exception.HisException;
import com.example.his.api.front.service.CustomerImService;
import com.tencentyun.TLSSigAPIv2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service("FrontCustomerImServiceImpl")
@Slf4j
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
    private String baseUrl = "https://console.tim.qq.com/";

    @Override
    @Transactional
    public HashMap createAccount(int customerId) {
        HashMap map = customerDao.searchById(customerId);
        String tel = MapUtil.getStr(map, "tel");
        String photo = MapUtil.getStr(map, "photo");
        String account = "customer_" + customerId;
        String nickname = "客户_" + tel;

        TLSSigAPIv2 api = new TLSSigAPIv2(sdkAppId, secretKey);
        String userSig = api.genUserSig(account, 180 * 86400);//生成 客户账号签名
        //保存返回结果
        HashMap result = new HashMap<>();
        result.put("sdkAppId", sdkAppId);
        result.put("account", account);
        result.put("userSig", userSig);

        userSig = api.genUserSig(managerId, 180 * 86400);//生成管理员账号签名
        //select 是否 创建了客户IM账号
        String url = baseUrl + "v4/im_open_login_svc/account_check?sdkappid=" +
                sdkAppId + "&identifier=" + managerId + "&usersig=" + userSig +
                "&random=" + RandomUtil.randomInt(1, 99999999) + "&contenttype=json";
        JSONObject json = new JSONObject();
        json.set("CheckItem", new ArrayList<>() {{
            add(new HashMap<>() {{
                put("UserID", account);
            }});
        }});
        String response = HttpUtil.post(url, json.toString());
        JSONObject entries = JSONUtil.parseObj(response);
        Integer errorCode = entries.getInt("ErrorCode");
        String errorInfo = entries.getStr("ErrorInfo");
        if (errorCode != 0) {
            log.error("查询客户IM账号失败：" + errorInfo);
            throw new HisException("客服系统异常");
        }
        JSONArray list = (JSONArray) entries.get("ResultItem");
        JSONObject object = (JSONObject) list.get(0);
        String accountStatus = object.getStr("AccountStatus");
        if ("Imported".equals(accountStatus)) {//判断是否存在客户IM账号
            //TODO 更新登录时间； 发送欢迎词
            int rows = customerImDao.insert(customerId);
            if (rows == 0) {
                log.error("无法更新客户IM账号登陆时间，客户账号ID：" + customerId);
                throw new HisException("客服系统异常");
            }
            //send welcome message
            this.sendWelcomeMessage(account);
            return result;
        }
        //创建客户IM账号
        url = baseUrl + "v4/im_open_login_svc/account_import?sdkappid=" +
                sdkAppId + "&identifier=" + managerId + "&usersig=" +
                userSig + "&random=" + RandomUtil.randomInt(1, 99999999) +
                "&contenttype=json";
        json = new JSONObject();
        json.set("UserID", account);
        json.set("Nick", nickname);
        if (photo != null) {
            json.set("FaceUrl", photo);
        }
        response = HttpUtil.post(url, json.toString());
        entries = JSONUtil.parseObj(response);
        errorCode = entries.getInt("ErrorCode");
        errorInfo = entries.getStr("ErrorInfo");
        if (errorCode != 0) {
            log.error("创建客户IM账号失败：" + errorInfo);
            throw new HisException("客服系统异常");
        }
        //为客户IM账号添加客服好友
        url = baseUrl + "v4/sns/friend_add?sdkappId=" +
                sdkAppId + "&identifier=" + managerId + "&usersig=" +
                userSig + "&random=" + RandomUtil.randomInt(1, 99999999) +
                "&contenttype=json";
        json = new JSONObject();
        json.set("From_Account", account);
        json.set("AddFriendItem", new ArrayList<>() {{
            add(new HashMap<>() {{
                put("To_account", customerServiceId);
                put("AddSource", "AddSource_Type_Web");
            }});
        }});
        response = HttpUtil.post(url, json.toString());
        entries = JSONUtil.parseObj(response);
        errorCode = entries.getInt("ErrorCode");
        errorInfo = entries.getStr("ErrorInfo");
        if (errorCode != 0) {
            log.error("添加客服IM账号失败：" + errorInfo);
            throw new HisException("客服系统异常");
        }
        list = (JSONArray) entries.get("ResultItem");
        object = (JSONObject) list.get(0);

        int resultCode = object.getInt("ResultCode");
        String resultInfo = object.getStr("ResultInfo");
        if (resultCode != 0) {
            log.error("添加客服IM好友失败：" + resultInfo);
            throw new HisException("客服系统异常");
        }
        //保存IM账号登录时间
        int rows = customerImDao.insert(customerId);
        if (rows == 0) {
            log.error("无法创建客户IM账号， 客户账号ID：" + customerId);
            throw new HisException("客服系统异常");
        }
        //send welcome message
        this.sendWelcomeMessage(account);
        return result;
    }

    private void sendWelcomeMessage(String account) {
        TLSSigAPIv2 api = new TLSSigAPIv2(sdkAppId, secretKey);
        String userSig = api.genUserSig(customerServiceId, 180 * 86400);
        String url = baseUrl + "v4/openim/sendmsg?sdkappid=" +
                sdkAppId + "&identifier=" + customerServiceId + "&usersig=" +
                userSig + "&random=" + RandomUtil.randomInt(1, 99999999) +
                "&contenttype=json";
        JSONObject json = new JSONObject();
        json.set("SyncOtherMachine", 2);
        json.set("To_Account", account);
        json.set("MsgLifeTime", 120);
        json.set("MsgRandom", RandomUtil.randomInt(1, 99999999));
        json.set("MsgBody", new ArrayList<>() {{
            add(new HashMap<>() {{
                put("MsgType", "TIMTextElem");
                put("MsgContent", new HashMap<>() {{
                    put("Text", "亲，您好，非常高兴为您服务，有什么可以为您效劳的呢?");
                }});
            }});
        }});
        String response = HttpUtil.post(url, json.toString());
        JSONObject entries = JSONUtil.parseObj(response);
        int errorCode = entries.getInt("ErrorCode");
        String errorInfo = entries.getStr("ErrorInfo");
        if (errorCode != 0) {
            log.error("发送欢迎词失败：" + errorInfo.toString());
            throw new HisException("客服系统异常");
        }
    }
}
