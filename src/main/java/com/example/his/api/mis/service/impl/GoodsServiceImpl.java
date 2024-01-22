package com.example.his.api.mis.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.his.api.common.MinioUtil;
import com.example.his.api.common.PageUtils;
import com.example.his.api.db.dao.GoodsDao;
import com.example.his.api.db.pojo.GoodsEntity;
import com.example.his.api.exception.HisException;
import com.example.his.api.mis.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service("MisGoodsServiceImpl")
@Slf4j
public class GoodsServiceImpl implements GoodsService {
    @Resource
    private GoodsDao goodsDao;
    @Resource
    private MinioUtil minioUtil;


    @Override
    public PageUtils searchByPage(Map param) {
        ArrayList<HashMap> list = new ArrayList<>();
        long count = goodsDao.searchCount(param);
        if (count > 0) {
            list = goodsDao.searchByPage(param);
        }
        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return pageUtils;
    }

    @Override
    public String uploadImage(MultipartFile file) {
        //生产新的文件名
        String filename = IdUtil.simpleUUID() + ".jpg";
        String path = "front/goods/" + filename;
        minioUtil.uploadImage(path, file);
        return path;
    }

    @Override
    public int insert(GoodsEntity entity) {
        //计算商品信息的MD5值
        String md5 = this.getEntityMd5(entity);
        entity.setMd5(md5);
        int rows = goodsDao.insert(entity);
        return rows;
    }

    @Override
    public HashMap searchById(int id) {
        Map param = new HashMap() {{
            put("id", id);
        }};
        HashMap map = goodsDao.searchById(param);
        String[] column = {"tag", "checkup_1", "checkup_2", "checkup_3", "checkup_4"};
        for (String one : column) {
            String temp = MapUtil.getStr(map, one);
            if (temp != null) {
                JSONArray array = JSONUtil.parseArray(temp);
                map.replace(one, array);
            }
        }
        return map;
    }

    private String getEntityMd5(GoodsEntity entity) {
        JSONObject json = JSONUtil.parseObj(entity);
        //去除不属于计算商品信息的MD5值范畴
        json.remove("id");
        json.remove("partId");
        json.remove("salesVolume");
        json.remove("status");
        json.remove("md5");
        json.remove("updateTime");
        json.remove("createTime");
        return MD5.create().digestHex(json.toString()).toUpperCase();
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "goods", key = "#entity.id")
    public int update(GoodsEntity entity) {
        //x修改商品重新计算md5值
        String md5 = this.getEntityMd5(entity);
        entity.setMd5(md5);
        int rows = goodsDao.update(entity);
        return rows;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "goods", key = "#id")
    public void updateCheckup(Integer id, MultipartFile file) {
        //用于保存excel中解析出来的数据
        ArrayList list = new ArrayList();
        //读取文件内容
        try (InputStream in = file.getInputStream();
             BufferedInputStream bin = new BufferedInputStream(in);
        ) {
            XSSFWorkbook workbook = new XSSFWorkbook(bin);
            //获取excel文档中的第一个sheet页
            XSSFSheet sheet = workbook.getSheetAt(0);
            //从第2行开始 因为第一行是表头
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                XSSFRow row = sheet.getRow(i);
                //检查地点
                XSSFCell cell_1 = row.getCell(0);
                String value_1 = cell_1.getStringCellValue();
                //导诊名称
                XSSFCell cell_2 = row.getCell(1);
                String value_2 = cell_2.getStringCellValue();
                //检查项目
                XSSFCell cell_3 = row.getCell(2);
                String value_3 = cell_3.getStringCellValue();
                //采集方式
                XSSFCell cell_4 = row.getCell(3);
                String value_4 = cell_4.getStringCellValue();
                //模板编码
                XSSFCell cell_5 = row.getCell(4);
                String value_5 = cell_5.getStringCellValue();
                //性别要求
                XSSFCell cell_6 = row.getCell(5);
                String value_6 = cell_6.getStringCellValue();
                //模板值
                XSSFCell cell_7 = row.getCell(6);
                String value_7 = cell_7.getStringCellValue();
                //输出模板
                XSSFCell cell_8 = row.getCell(7);
                String value_8 = cell_8.getStringCellValue();

                //顺序保存值的容器
                LinkedHashMap map = new LinkedHashMap() {{
                    put("place", value_1);
                    put("name", value_2);
                    put("item", value_3);
                    put("type", value_4);
                    put("code", value_5);
                    put("sex", value_6);
                    put("value", value_7);
                    put("template", value_8);
                }};
                list.add(map);
            }
            System.out.println(list);
        } catch (Exception e) {
            throw new HisException("处理excel文件失败", e);
        }
        if (list.size() == 0) {
            throw new HisException("文档内容无效！");
        }
        //把文件存储到Minio服务器
        String path = "/mis/goods/checkup/" + id + ".xlsx";
        minioUtil.uploadExcel(path, file);
        //根据商品ID查询商品记录，然后重新计算MD5值，更新商品记录的checkup和MD5字段
        GoodsEntity entity = goodsDao.searchEntityById(id);
        String temp = JSONUtil.parseArray(list).toString();
        entity.setCheckup(temp);
        String md5 = this.getEntityMd5(entity);
        HashMap param = new HashMap() {{
            put("id", id);
            put("checkup", temp);
            put("md5", md5);//更新md5值
        }};
        //保存数据
        int rows = goodsDao.updateCheckup(param);
        if (rows != 1) {
            throw new HisException("更新体检内容失败");
        }
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "goods", key = "#param.get('id')", condition = "#param.get('status') == false")
    public boolean updateStatus(Map param) {
        int rows = goodsDao.updateStatus(param);
        boolean bool = (rows == 1);
        return bool;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "goods", key = "#ids")
    public int deleteByIds(Integer[] ids) {
        //删除记录之前先查询商品的封面
        ArrayList<String> list = goodsDao.searchImageByIds(ids);
        //删除记录
        int rows = goodsDao.deleteByIds(ids);
        if (rows > 0) {
            //删除商品对应的封面图片
            list.forEach((path) -> {
                minioUtil.deleteFile(path);
            });
        }
        return rows;
    }


}
