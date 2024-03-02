package com.example.his.api.mis.controller.form;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateFlowRegulationModeForm {
    @NotNull(message = "mode不能为空")
    private Boolean mode;
}

