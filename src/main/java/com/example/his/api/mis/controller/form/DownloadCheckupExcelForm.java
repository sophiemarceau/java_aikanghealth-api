package com.example.his.api.mis.controller.form;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class DownloadCheckupExcelForm {
    @NotNull(message = "id不能小于1")
    @Min(value = 1, message = "id不能小于1")
    private Integer id;
}
