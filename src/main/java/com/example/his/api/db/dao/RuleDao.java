package com.example.his.api.db.dao;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
* @author Administrator
* @description 针对表【tb_rule(规则表)】的数据库操作Mapper
* @createDate 2023-07-06 12:32:56
* @Entity com.example.his.api.db.pojo.RuleEntity
*/
public interface RuleDao {
    public ArrayList<HashMap> searchAllRule();
}




