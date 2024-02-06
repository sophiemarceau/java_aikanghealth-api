package com.example.his.api.mis.controller.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class CheckinAppointmentForm {
    @NotBlank(message = "pid不能为空")
    @Pattern(regexp = "^[0-9Xx]{18}$", message = "身份证号码无效")
    private String pid;

    @NotBlank(message = "name不能为空")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{2,10}$", message = "name内容不正确")
    private String name;

    @NotBlank(message = "photo_1不能为空")
    private String photo_1;

    @NotBlank(message = "photo_2不能为空")
    private String photo_2;
}
