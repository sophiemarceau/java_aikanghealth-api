package com.example.his.api.front.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import cn.hutool.json.JSONUtil;
import com.example.his.api.db.dao.GoodsDao;
import com.example.his.api.db.dao.GoodsSnapshotDao;
import com.example.his.api.db.dao.OrderDao;
import com.example.his.api.db.pojo.GoodsEntity;
import com.example.his.api.db.pojo.GoodsSnapshotEntity;
import com.example.his.api.db.pojo.OrderEntity;
import com.example.his.api.exception.HisException;
import com.example.his.api.front.service.OrderService;
import com.example.his.api.front.service.PaymentService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("FrontOrderServiceImpl")
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Resource
    private OrderDao orderDao;
    @Resource
    private GoodsDao goodsDao;
    @Resource
    private PaymentService paymentService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private GoodsSnapshotDao goodsSnapshotDao;

    private String paymentNotifyUrl = "/front/order/paymentCallback";


    @Override
    @Transactional
    public HashMap createPayment(Map param) {
        int goodsId = MapUtil.getInt(param, "goodsId");
        Integer number = MapUtil.getInt(param, "number");
        int customerId = MapUtil.getInt(param, "customerId");

        //若 当天 10+未付款or 5+退款订单 当天就无法下单
        boolean illegal = orderDao.searchIllegalCountInDay(customerId);
        if (illegal) {
            return null;
        }

        HashMap map = goodsDao.searchSnapshotNeededById(goodsId);
        String goodsCode = MapUtil.getStr(map, "code");
        String goodsTitle = MapUtil.getStr(map, "title");
        String goodsDescription = MapUtil.getStr(map, "description");
        String goodsImage = MapUtil.getStr(map, "image");
        BigDecimal goodsInitialPrice = new BigDecimal(MapUtil.getStr(map, "initialPrice"));
        BigDecimal goodsCurrentPrice = new BigDecimal(MapUtil.getStr(map, "currentPrice"));
        String goodsRuleName = MapUtil.getStr(map, "ruleName");
        String goodsRule = MapUtil.getStr(map, "rule");
        String goodsType = MapUtil.getStr(map, "type");
        String goodsMd5 = MapUtil.getStr(map, "md5");
        String temp = MapUtil.getStr(map, "checkup_1");
        List<Map> goodsCheckup_1 = temp != null ? JSONUtil.parseArray(temp).toList(Map.class) : null;
        temp = MapUtil.getStr(map, "checkup_2");
        List<Map> goodsCheckup_2 = temp != null ? JSONUtil.parseArray(temp).toList(Map.class) : null;

        temp = MapUtil.getStr(map, "checkup_3");
        List<Map> goodsCheckup_3 = temp != null ? JSONUtil.parseArray(temp).toList(Map.class) : null;

        temp = MapUtil.getStr(map, "checkup_4");
        List<Map> goodsCheckup_4 = temp != null ? JSONUtil.parseArray(temp).toList(Map.class) : null;

        temp = MapUtil.getStr(map, "checkup");
        List<Map> goodsCheckup = temp != null ? JSONUtil.parseArray(temp).toList(Map.class) : null;

        temp = MapUtil.getStr(map, "tag");
        List<Map> goodsTag = temp != null ? JSONUtil.parseArray(temp).toList(Map.class) : null;
        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("number", number.intValue());
        context.put("price", goodsCurrentPrice.toString());
        String amount = null;
        if (goodsRule != null) {
            try {
                //执行规则引擎计算支付结果
                amount = runner.execute(goodsRule, context, null, true, false).toString();
            } catch (Exception e) {
                throw new HisException("规则引擎计算价格失败", e);
            }
        } else {
            amount = goodsCurrentPrice.multiply(new BigDecimal(number)).toString();
        }
        //把付款金额从元转换成分
        int total = NumberUtil.mul(amount, "100").intValue();
        //生成商品订单流水号
        String outTradeNo = IdUtil.simpleUUID().toUpperCase();
        //付款过期 20分钟
        DateTime dateTime = new DateTime();
        dateTime.offset(DateField.MINUTE, 20);
        String timeExpire = dateTime.toString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        //生成订单
        ObjectNode objectNode = paymentService.unifiedOrder(outTradeNo, total, "购买体检套餐", paymentNotifyUrl, timeExpire);
        String codeUrl = objectNode.get("code_url").textValue();
        /**
         * 把支付单的codeUrl缓存到Redis中，用于将来检测未付款订单是否可以付款
         * 比如客户进入订单列表页面，想要对未付款的订单付款，我们可以根据是否存
         * 在缓存，判定用户能否付款。这样可以省去调用微信支付接口查询付款单状态。
         */
        String key = "codeUrl_" + customerId + "_" + outTradeNo;
        redisTemplate.opsForValue().set(key, codeUrl);
        redisTemplate.expireAt(key, dateTime);//设置缓存过期时间

        if (codeUrl != null) {
            //根据商品MD5 查询是否存在快照
            String _id = goodsSnapshotDao.hasGoodsSnapshot(goodsMd5);
            //不存在 去创建
            if (_id == null) {
                GoodsSnapshotEntity entity = new GoodsSnapshotEntity();
                entity.setId(goodsId);
                entity.setCode(goodsCode);
                entity.setDescription(goodsDescription);
                entity.setCheckup_1(goodsCheckup_1);
                entity.setCheckup_2(goodsCheckup_2);
                entity.setCheckup_3(goodsCheckup_3);
                entity.setCheckup_4(goodsCheckup_4);
                entity.setCheckup(goodsCheckup);
                entity.setImage(goodsImage);
                entity.setInitialPrice(goodsInitialPrice);
                entity.setCurrentPrice(goodsCurrentPrice);
                entity.setType(goodsType);
                entity.setRule(goodsRuleName);
                entity.setMd5(goodsMd5);
                //保存商品快照，拿到快照主键值
                _id = goodsSnapshotDao.insert(entity);
            }
            OrderEntity entity = new OrderEntity();
            entity.setCustomerId(customerId);
            entity.setGoodsId(goodsId);
            entity.setSnapshotId(_id);//关联商品快照
            entity.setGoodsTitle(goodsTitle);
            entity.setGoodsTitle(goodsTitle);
            entity.setGoodsPrice(goodsCurrentPrice);
            entity.setNumber(number);
            entity.setAmount(new BigDecimal(amount));
            entity.setGoodsImage(goodsImage);
            entity.setGoodsDescription(goodsDescription);
            entity.setOutTradeNo(outTradeNo);
            orderDao.insert(entity);//save 订单记录

            QrConfig qrConfig = new QrConfig();
            qrConfig.setWidth(230);
            qrConfig.setHeight(230);
            qrConfig.setMargin(2);
            String qrCodeBase64 = QrCodeUtil.generateAsBase64(codeUrl, qrConfig, "jpg");

            int rows = goodsDao.updateSalesVolume(goodsId);
            if (rows != 1) {
                throw new HisException("更新商品销量失败");
            }
            return new HashMap() {{
                put("qrCodeBase64", qrCodeBase64);
                put("outTradeNo", outTradeNo);
            }};
        } else {
            log.error("创建支付订单失败", objectNode);
            throw new HisException("创建支付订单失败");
        }
    }

    @Override
    public boolean updatePayment(Map param) {
        int rows = orderDao.updatePayment(param);
        return rows == 1;
    }
}
