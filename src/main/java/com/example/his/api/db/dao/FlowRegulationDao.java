package com.example.his.api.db.dao;

import com.example.his.api.db.pojo.FlowRegulationEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @description 针对表【tb_flow_regulation(人员调流表)】的数据库操作Mapper
 * @createDate 2023-07-06 12:32:56
 * @Entity com.example.his.api.db.pojo.FlowRegulationEntity
 */
public interface FlowRegulationDao {
    public ArrayList<String> searchPlaceList();

    public ArrayList<FlowRegulationEntity> searchByPage(Map param);

    public long searchCount(Map param);

    public int insert(FlowRegulationEntity entity);

    public FlowRegulationEntity searchById(int id);

    public int update(Map param);

    public int updateRealNum(Map param);

    public ArrayList<HashMap> searchRecommendWithWeight();

    public ArrayList<HashMap> searchRecommendWithPriority();

    public ArrayList<HashMap> searchAllByPlace();

    public int deleteByIds(Integer[] ids);
}




