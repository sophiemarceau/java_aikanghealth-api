package com.example.his.api.mis.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import com.example.his.api.common.MinioUtil;
import com.example.his.api.common.OcrUtil;
import com.example.his.api.common.PageUtils;
import com.example.his.api.db.dao.AppointmentDao;
import com.example.his.api.db.dao.CheckupReportDao;
import com.example.his.api.db.dao.CheckupResultDao;
import com.example.his.api.db.pojo.CheckupResultEntity;
import com.example.his.api.exception.HisException;
import com.example.his.api.mis.service.CheckupReportService;
import com.example.his.api.report.CheckupReportUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

@Service
@Slf4j
public class CheckupReportServiceImpl implements CheckupReportService {
    @Resource
    private CheckupReportDao checkupReportDao;
    @Resource
    private AppointmentDao appointmentDao;
    @Resource
    private CheckupResultDao checkupResultDao;
    @Resource
    private MinioUtil minioUtil;
    @Resource
    private CheckupReportUtil checkupReportUtil;
    @Resource
    private OcrUtil ocrUtil;

    @Override
    public PageUtils searchByPage(Map param) {
        ArrayList<HashMap> list = new ArrayList<>();
        long count = checkupReportDao.searchCount(param);
        if (count > 0) {
            list = checkupReportDao.searchByPage(param);
        }
        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return pageUtils;
    }

    @SneakyThrows
    @Override
    @Transactional
    public boolean createReport(Integer id) {
        HashMap result = checkupReportDao.searchById(id);
        if (result == null || result.size() == 0) {
            throw new HisException("不存在体检报告记录");
        }
        String resultId = MapUtil.getStr(result, "resultId");
        int appointmentId = MapUtil.getInt(result, "appointmentId");
        int status = MapUtil.getInt(result, "status");
        DateTime date = DateUtil.parseDate(MapUtil.getStr(result, "date"));
        DateTime today = new DateTime(DateUtil.today());
        //判断当前日期距离体检日期是否在10天以内
        if (today.offsetNew(DateField.DAY_OF_MONTH, -10).isBefore(date)) {
            throw new HisException("无法生成10天以内的体检报告");
        }
        //根据状态判读是否已经生成过体检报告
        if (status != 1) {
            log.debug("主键为" + id + "" +
                    "的体检报告已经生成，当前任务自动结束");
            return true;
        }
        //查询体检人信息、体检套餐名称
        HashMap map = appointmentDao.searchDataForReport(appointmentId);
        //查询体检项目
        CheckupResultEntity entity = checkupResultDao.searchById(resultId);
        List<Map> checkup = entity.getCheckup();
        map.put("checkup", checkup);
        map.put("result", entity.getResult());

        HashSet set = new HashSet();
        //所有体检科室添加到set中
        checkup.forEach(one -> {
            String place = MapUtil.getStr(one, "place");
            set.add(place);
        });
//        //获取已经录入体检结果的科室列表
//        List<String> placeList = entity.getPlace();
//        //若 体检存在逾期未录入的体检结果的科室，发送email给相关人通知
//        if (placeList.size() < set.size()) {
//            log.debug("主键为" + id + "的体检存在没有录入的体检结果");
//            //TODO 发送告警邮件or短信
//            return false;
//        }
        //创建体检报告
        XWPFDocument report = checkupReportUtil.createReport(map);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        report.write(out);
        out.flush();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        String filePath = "/report/checkup/" + resultId + ".docx";
        //把体检报告上传到Mino
        minioUtil.uploadWord(filePath, in);
        //更新体检结果状态
        checkupReportDao.update(new HashMap() {{
            put("status", 2);
            put("filePath", filePath);
            put("id", id);
        }});
        return true;
    }

    @Override
    public HashMap identifyWaybill(String imgBase64) {
        try {
            HashMap map = ocrUtil.identifyWaybill(imgBase64);
            return map;
        } catch (Exception e) {
            log.error("OCR识别异常", e);
            throw new HisException("OCR识别异常");
        }
    }

    @Override
    @Transactional
    public boolean addWaybill(Map param) {
        int rows = checkupReportDao.update(param);
        return rows == 1;
    }

    @Override
    @Transactional
    public XSSFWorkbook importWaybills(MultipartFile file) {
        try (InputStream in = file.getInputStream()) {

            XSSFWorkbook workbook = new XSSFWorkbook(in);
            XSSFSheet sheet = workbook.getSheetAt(0);
            //用于保存没有更新成功的记录
            ArrayList<HashMap> list = new ArrayList();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                XSSFRow row = sheet.getRow(i);
                String uuid = row.getCell(1).getStringCellValue();
                row.getCell(2).setCellType(CellType.STRING);
                String recName = row.getCell(2).getStringCellValue();
                row.getCell(3).setCellType(CellType.STRING);
                String recTel = row.getCell(3).getStringCellValue();
                String waybillCode = row.getCell(4).getStringCellValue();

                //查询体检人信息
                HashMap map = appointmentDao.searchDataForWaybill(uuid);
                if (map == null || map.size() == 0) {
                    //记录uuid有误的运单信息
                    list.add(new HashMap() {{
                        put("uuid", uuid);
                        put("recName", recName);
                        put("recTel", recTel);
                        put("waybillCode", waybillCode);
                        put("result", "收件人姓名或者电话与体检人不符");
                    }});
                    continue;
                }
                Integer appointmentId = MapUtil.getInt(map, "id");
                String name = MapUtil.getStr(map, "name");
                String tel = MapUtil.getStr(map, "tel");

                HashMap param = new HashMap<>() {{
                    put("waybillCode", waybillCode);
                    put("waybillDate", DateUtil.today());
                    put("appointmentId", appointmentId);
                }};
                //保存运单信息
                int rows = checkupReportDao.updateWaybill(param);
                if (rows == 0) {
                    //记录更新失败的运单
                    list.add(new HashMap() {{
                        put("uuid", uuid);
                        put("recName", recName);
                        put("recTel", recTel);
                        put("waybillCode", waybillCode);
                        put("result", "更新失败");
                    }});
                }
            }




            if (list.size() == 0) {
                return null;
            }
            //把更新失败的运单导出到Excel文件中
            workbook = new XSSFWorkbook();
            //创建单元格公共样式对象
            XSSFCellStyle style = workbook.createCellStyle();
            //设置边框
            style.setBorderBottom(BorderStyle.MEDIUM);
            style.setBorderLeft(BorderStyle.MEDIUM);
            style.setBorderTop(BorderStyle.MEDIUM);
            style.setBorderRight(BorderStyle.MEDIUM);
            //设置字体
            XSSFFont font = workbook.createFont();
            font.setFontName("微软雅黑");
            style.setFont(font);
            //设置单元格数据格式为文本类型
            XSSFDataFormat dataFormat = workbook.createDataFormat();
            style.setDataFormat(dataFormat.getFormat("@"));
            //创建Sheet页
            sheet = workbook.createSheet("导入失败的运单");
            //设置列宽
            sheet.setColumnWidth(0, 3000);
            sheet.setColumnWidth(1, 7000);
            sheet.setColumnWidth(2, 5000);
            sheet.setColumnWidth(3, 5000);
            sheet.setColumnWidth(4, 7000);
            sheet.setColumnWidth(5, 7000);
            //设置表头行
            XSSFRow row = sheet.createRow(0);
            XSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.cloneStyleFrom(style);
            //设置表头行背景色
            XSSFColor color = new XSSFColor(new Color(255, 255, 0), new DefaultIndexedColorMap());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setFillForegroundColor(color);
            //设置表头行单元格文字
            XSSFCell cell = row.createCell(0);
            cell.setCellStyle(headerStyle);
            cell.setCellValue("序号");

            cell = row.createCell(1);
            cell.setCellStyle(headerStyle);
            cell.setCellValue("体检编号");

            cell = row.createCell(2);
            cell.setCellStyle(headerStyle);
            cell.setCellValue("收件人");

            cell = row.createCell(3);
            cell.setCellStyle(headerStyle);
            cell.setCellValue("收件人电话");

            cell = row.createCell(4);
            cell.setCellStyle(headerStyle);
            cell.setCellValue("运单号码");

            cell = row.createCell(5);
            cell.setCellStyle(headerStyle);
            cell.setCellValue("导入失败原因");

            //输出表头行下方的行记录
            for (int i = 1; i <= list.size(); i++) {
                Map map = list.get(i - 1);


                String uuid = MapUtil.getStr(map, "uuid");
                String recName = MapUtil.getStr(map, "recName");
                String recTel = MapUtil.getStr(map, "recTel");
                String waybillCode = MapUtil.getStr(map, "waybillCode");
                String result = MapUtil.getStr(map, "result");


                row = sheet.createRow(i);

                cell = row.createCell(0);
                cell.setCellStyle(style);
                cell.setCellValue("" + i);

                cell = row.createCell(1);
                cell.setCellStyle(style);
                cell.setCellValue("" + uuid);

                cell = row.createCell(2);
                cell.setCellStyle(style);
                cell.setCellValue("" + recName);

                cell = row.createCell(3);
                cell.setCellStyle(style);
                cell.setCellValue("" + recTel);

                cell = row.createCell(4);
                cell.setCellStyle(style);
                cell.setCellValue("" + waybillCode);

                cell = row.createCell(5);
                cell.setCellStyle(style);
                cell.setCellValue(result);
            }
            return workbook;
        } catch (Exception e) {
            log.error("更新运单失败", e);
            throw new HisException("更新运单失败");
        }
    }
}
