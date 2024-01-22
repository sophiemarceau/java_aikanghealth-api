package com.example.his.api.mis.controller.form;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class UploadCheckupExcelForm {
    @NotNull(message = "id不能小于")
    @Min(value = 1, message = "id不能小于1")
    private Integer id;
}
