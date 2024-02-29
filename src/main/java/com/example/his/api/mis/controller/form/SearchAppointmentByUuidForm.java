package com.example.his.api.mis.controller.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SearchAppointmentByUuidForm {
    @NotBlank(message = "uuid不能为空")
    @Pattern(regexp = "^[0-9A-Za-z]{32}$",message = "uuid内容不正确")
    private String uuid;
}

