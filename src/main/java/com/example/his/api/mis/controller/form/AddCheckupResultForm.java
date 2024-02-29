package com.example.his.api.mis.controller.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;

@Data
public class AddCheckupResultForm {
    @NotBlank(message = "name不能为空")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{2,10}$", message = "name内容不正确")
    private String name;

    @NotBlank(message = "uuid不能为空")
    @Pattern(regexp = "^[0-9A-Za-z]{32}$", message = "uuid内容不正确")
    private String uuid;

    @NotBlank(message = "place不能为空")
    @Pattern(regexp = "^[0-9A-Za-z\\u4e00-\\u9fa5]{2,30}$", message = "place内容不正确")
    private String place;

    @NotEmpty(message = "item不能为空")
    private ArrayList item;

    @NotEmpty(message = "template不能为空")
    private String template;
}
