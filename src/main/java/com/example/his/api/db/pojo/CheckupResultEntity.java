package com.example.his.api.db.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@Document(collection = "checkup_result")
public class CheckupResultEntity {
    @Id
    private String _id;
    @Indexed
    private String uuid;
    private List<Map> checkup;
    private List<String> place;
    private List<Map> result;
}
