package com.example.his.api.mis.controller.form;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class SearchRoleByIdForm {
    @NotNull(message = "id不能为空")
    @Min(value = 1, message = "id不能小于")
    private Integer id;
}
