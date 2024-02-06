package com.example.his.api.mis.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.example.his.api.common.FaceAuthUtil;
import com.example.his.api.common.MinioUtil;
import com.example.his.api.common.PageUtils;
import com.example.his.api.db.dao.AppointmentDao;
import com.example.his.api.db.dao.CheckupResultDao;
import com.example.his.api.db.dao.GoodsSnapshotDao;
import com.example.his.api.exception.HisException;
import com.example.his.api.mis.service.AppointmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service("MisAppointmentServiceImpl")
public class AppointmentServiceImpl implements AppointmentService {
    @Resource
    private AppointmentDao appointmentDao;
    @Resource
    private GoodsSnapshotDao goodsSnapshotDao;
    @Resource
    private CheckupResultDao checkupResultDao;
    @Resource
    private FaceAuthUtil faceAuthUtil;
    @Resource
    private MinioUtil minioUtil;

    @Override
    public ArrayList<HashMap> searchByOrderId(int orderId) {
        ArrayList<HashMap> list = appointmentDao.searchByOrderId(orderId);
        return list;
    }

    @Override
    public PageUtils searchByPage(Map param) {
        ArrayList<HashMap> list = new ArrayList<>();
        long count = appointmentDao.searchCount(param);
        if (count > 0) {
            list = appointmentDao.searchByPage(param);
        }
        int start = (Integer) param.get("start");
        int length = (Integer) param.get("length");
        PageUtils pageUtils = new PageUtils(list, count, start, length);
        return pageUtils;
    }

    @Override
    public int deleteByIds(Integer[] ids) {
        int rows = appointmentDao.deleteByIds(ids);
        return rows;
    }

    @Override
    public int hasAppointInToday(Map param) {
        HashMap map = appointmentDao.hasAppointInToday(param);
        if (map == null) {
            return 0;//没有预约
        } else if (MapUtil.getInt(map, "status") != 1) {
            return -1;//已经签到
        } else {
            return 1;//有预约，未签到
        }
    }

    @Override
    @Transactional
    public boolean checkin(Map param) {
        String pid = MapUtil.getStr(param, "pid");
        String name = MapUtil.getStr(param, "name");
        String sex = IdcardUtil.getGenderByIdCard(pid) == 1 ? "男" : "女";
        String photo_1 = MapUtil.getStr(param, "photo_1");
        String photo_2 = MapUtil.getStr(param, "photo_2");

        //执行人脸识别
        boolean result = faceAuthUtil.verifyFaceModel(name, pid, sex, photo_1, photo_2);
        if (result) {
            boolean result1 = faceAuthUtil.selectFaceModelByPid(pid);
            if (!result1) {
                faceAuthUtil.createFaceModel(name, pid, sex, photo_1, photo_2);
            }
            //把用户照片保存到Minio 保留体检人签到证据
            String filename = pid + new DateTime().toDateStr() + ".jpg";
            String path = "checkin/" + filename;
            minioUtil.uploadImage(path, photo_2);
            //更新体检预约状态为已签到
            int rows = appointmentDao.checkin(param);
            if (rows != 1) {
                throw new HisException("保存签到记录失败");
            }
            //查询体检流水号和订单快照ID
            HashMap map = appointmentDao.searchUuidAndSnapshotId(param);
            String uuid = MapUtil.getStr(map, "uuid");
            String snapshotId = MapUtil.getStr(map, "snapshotId");
            List<Map> checkup = goodsSnapshotDao.searchCheckup(snapshotId, sex);
            //添加体检结果记录
            boolean bool = checkupResultDao.insert(uuid, checkup);
            if (!bool) {
                throw new HisException("添加体检结果失败");
            }
        }
        return result;
    }

    @Override
    public HashMap searchGuidanceInfo(int id) {
        HashMap map = appointmentDao.searchSummaryById(id);
        String snapshotId = MapUtil.getStr(map, "snapshotId");
        String sex = MapUtil.getStr(map, "sex");
        //创建2维码 base64字符串
        String uuid = MapUtil.getStr(map, "uuid");
        QrConfig qrConfig = new QrConfig();
        qrConfig.setWidth(100);
        qrConfig.setHeight(100);
        qrConfig.setMargin(0);
        String qrCodeBase64 = QrCodeUtil.generateAsBase64(uuid, qrConfig, "jpg");
        map.put("qrCodeBase64", qrCodeBase64);
        List<Map> list = goodsSnapshotDao.searchCheckup(snapshotId, sex);
        LinkedHashSet<Map> set = new LinkedHashSet();
        list.forEach(one -> {
            HashMap temp = new HashMap() {{
                put("place", MapUtil.getStr(one, "place"));
                put("name", MapUtil.getStr(one, "name"));
            }};
            set.add(temp);
        });
        map.put("checkup", set);
        return map;
    }

}
