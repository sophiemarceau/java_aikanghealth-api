package com.example.his.api.db.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @description 针对表【tb_checkup_report(体检报告表)】的数据库操作Mapper
 * @createDate 2023-07-06 12:32:56
 * @Entity com.example.his.api.db.pojo.CheckupReportEntity
 */
public interface CheckupReportDao {
    public int insert(Map param);

    public ArrayList<HashMap> searchByPage(Map param);

    public long searchCount(Map param);

    public HashMap searchById(int id);

    public int update(Map param);

    public ArrayList<Integer> searchWillGenerateReport();

    public int updateWaybill(Map param);
}




