package com.example.his.api.mis.controller.form;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class SearchRuleByIdForm {
    @NotNull(message = "id不能为空")
    @Min(value = 1, message = "id不能小于")
    private Integer id;
}
