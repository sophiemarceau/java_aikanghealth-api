package com.example.his.api.mis.controller.form;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class UpdateAppointmentStatusByUuidForm {
    @NotBlank(message = "uuid不能为空")
    @Pattern(regexp = "^[0-9a-zA-Z]{32}$", message = "uuid内容不正确")
    private String uuid;

    @NotNull(message = "status不能为空")
    @Range(min = 1, max = 4, message = "status内容不正确")
    private Integer status;
}