package com.example.his.api.front.service;

import com.example.his.api.common.PageUtils;

import java.util.HashMap;
import java.util.Map;

public interface GoodsService {
    public HashMap searchById(int id);

    public HashMap searchIndexGoodsByPart(Integer[] partIds);

    public PageUtils searchListByPage(Map param);

    public HashMap searchSnapshotById(String snapshotId, Integer customerId);
}
