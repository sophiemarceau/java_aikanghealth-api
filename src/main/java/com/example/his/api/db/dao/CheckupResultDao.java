package com.example.his.api.db.dao;

import cn.hutool.core.lang.hash.Hash;
import cn.hutool.core.map.MapUtil;
import com.example.his.api.db.pojo.CheckupResultEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CheckupResultDao {
    @Resource
    private MongoTemplate mongoTemplate;

    public boolean insert(String uuid, List<Map> checkup) {
        CheckupResultEntity entity = new CheckupResultEntity();
        entity.setUuid(uuid);
        entity.setCheckup(checkup);
        entity.setPlace(new ArrayList<>());
        entity.setResult(new ArrayList<>());
        //添加新记录
        entity = mongoTemplate.insert(entity);
        return entity.get_id() != null;
    }

    public String searchByUuid(String uuid) {
        Criteria criteria = Criteria.where("uuid").is(uuid);
        Query query = new Query(criteria);
        CheckupResultEntity entity = mongoTemplate.findOne(query, CheckupResultEntity.class);
        return entity.get_id();
    }

    public List<Map> searchCheckupByPlace(String uuid, String place) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project("checkup", "uuid"),
                Aggregation.unwind("$checkup"),
                Aggregation.match(Criteria.where("uuid").is(uuid).and("checkup.place").is(place))
        );
        AggregationResults<HashMap> results = mongoTemplate.aggregate(aggregation, "checkup_result", HashMap.class);
        List<Map> list = new ArrayList<>();
        results.getMappedResults().forEach(one -> {
            HashMap map = (HashMap) one.get("checkup");
            list.add(map);
        });
        return list;
    }

    public boolean hasAlreadyCheckup(String uuid, String place) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project("result", "uuid"),
                Aggregation.unwind("$result"),
                Aggregation.match(Criteria.where("uuid").is(uuid).and("result.place").is(place))
        );
        AggregationResults<HashMap> results = mongoTemplate.aggregate(aggregation, "checkup_result", HashMap.class);
        List<HashMap> list = results.getMappedResults();
        if (list.size() >= 1) {
            return true;
        } else {
            return false;
        }
    }

    public void addResult(String uuid, String place, Map map) {
        Criteria criteria = Criteria.where("uuid").is(uuid);
        Query query = new Query(criteria);
        //根据UUID查找记录
        CheckupResultEntity entity = mongoTemplate.findOne(query, CheckupResultEntity.class);
        List<String> placeList = entity.getPlace();
        List<Map> resultList = entity.getResult();
        //判断是否存在该科室的体检结果
        int index = 0;
        //判断是否存在该科室结果
        if (placeList.contains(place)) {
            for (int i = 0; i < resultList.size(); i++) {
                Map one = resultList.get(i);
                String temp = MapUtil.getStr(one, "place");
                if (place.equals(temp)) {
                    index = i;
                    break;
                }
            }
            //用该科室的体检结果覆盖有结果的
            resultList.set(index, map);
        } else {
            //添加体检过的科室
            placeList.add(place);
            //添加体检结果
            resultList.add(map);
        }
        //更新数据
        mongoTemplate.save(entity);
    }

    public CheckupResultEntity searchById(String id) {
        CheckupResultEntity entity = mongoTemplate.findById(id, CheckupResultEntity.class);
        return entity;
    }
}
