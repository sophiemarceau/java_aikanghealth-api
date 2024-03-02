package com.example.his.api.mis.controller.form;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class UpdateFlowRegulationForm {
    @NotNull(message = "id不能为空")
    @Min(value = 1, message = "id不能小于1")
    private Integer id;

    @NotBlank(message = "place不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5\\(\\)]{2,40}$", message = "place内容不正确")
    private String place;

    @NotBlank(message = "blueUuid不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{32}$", message = "blueUuid内容不正确")
    private String blueUuid;

    @NotNull(message = "maxNum不能为空")
    @Range(min = 1, max = 1000, message = "maxNum内容不正确")
    private Integer maxNum;

    @NotNull(message = "weight不能为空")
    @Range(min = 1, max = 10, message = "weight内容不正确")
    private Integer weight;

    @NotNull(message = "priority不能为空")
    @Range(min = 1, max = 10, message = "priority内容不正确")
    private Integer priority;
}
