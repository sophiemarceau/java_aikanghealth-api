package com.example.his.api.mis.service;

import java.util.ArrayList;
import java.util.HashMap;

public interface CheckupService {
    public HashMap searchCheckupByPlace(String uuid, String place);
    public void addResult(int userId, String name, String uuid, String place, String template, ArrayList item);
}
