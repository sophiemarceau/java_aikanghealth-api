package com.example.his.api.mis.controller.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SearchCheckupByPlaceForm {
    @NotBlank(message = "uuid不能为空")
    @Pattern(regexp = "^[0-9a-zA-Z]{32}$", message = "uuid内容不正确")
    private String uuid;

    @NotBlank(message = "place不能为空")
    @Pattern(regexp = "^[0-9a-zA-Z\\u4e00-\\u9fa5]{2,30}$", message = "place内容不正确")
    private String place;
}
