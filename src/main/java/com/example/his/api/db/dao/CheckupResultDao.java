package com.example.his.api.db.dao;

import com.example.his.api.db.pojo.CheckupResultEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class CheckupResultDao {
    @Resource
    private MongoTemplate mongoTemplate;

    public boolean insert(String uuid, List<Map> checkup) {
        CheckupResultEntity entity = new CheckupResultEntity();
        entity.setUuid(uuid);
        entity.setCheckup(new ArrayList<>());
        entity.setPlace(new ArrayList<>());
        entity.setResult(new ArrayList<>());
        entity = mongoTemplate.insert(entity);
        return entity.get_id() != null;
    }
}
