package com.example.his.api.front.service;

import java.util.ArrayList;
import java.util.HashMap;

public interface AppointmentService {
    public ArrayList<HashMap> searchByOrderId(int orderId);
}
