package com.example.his.api.front.service;

import java.util.HashMap;
import java.util.Map;

public interface OrderService {
    public HashMap createPayment(Map param);

    public boolean updatePayment(Map param);
}
