package com.example.his.api.mis.service;

import com.example.his.api.common.PageUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

public interface CheckupReportService {
    public PageUtils searchByPage(Map param);

    public boolean createReport(Integer id);

    public HashMap identifyWaybill(String imgBase64);

    public boolean addWaybill(Map param);

    public XSSFWorkbook importWaybills(MultipartFile file);
}
