package com.example.his.api.db.dao;

import com.example.his.api.db.pojo.GoodsSnapshotEntity;
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
public class GoodsSnapshotDao {
    @Resource
    private MongoTemplate mongoTemplate;

    public String hasGoodsSnapshot(String md5) {
        Criteria criteria = Criteria.where("md5").is(md5);
        Query query = new Query(criteria);
        query.skip(0);
        query.limit(1);
        GoodsSnapshotEntity entity = mongoTemplate.findOne(query, GoodsSnapshotEntity.class);
        return entity != null ? entity.get_id() : null;
    }

    public String insert(GoodsSnapshotEntity entity) {
        String _id = mongoTemplate.save(entity).get_id();
        return _id;
    }

    public GoodsSnapshotEntity searchById(String id) {
        GoodsSnapshotEntity entity = mongoTemplate.findById(id, GoodsSnapshotEntity.class);
        return entity;
    }

    public List<Map> searchCheckup(String id, String sex) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project("checkup"),//规定节能过集体表
                Aggregation.unwind("$checkup"),//展开JSON 的数组内容，为了筛选其中元素
                Aggregation.match(
                        Criteria.where("_id")//定位拍照记录
                                .and("checkup.sex").in("无", sex)//筛选适合当前体检的人性别的检查项目
                )
        );
        AggregationResults<HashMap> results = mongoTemplate.aggregate(aggregation, "goods_snapshot", HashMap.class);
        List<Map> list = new ArrayList<>();
        results.getMappedResults().forEach(one -> {
            HashMap map = (HashMap) one.get("checkup");
            list.add(map);
        });
        return list;
    }
}
