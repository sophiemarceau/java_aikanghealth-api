package com.example.his.api.mis.controller.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CheckPaymentResultForm {
    @NotEmpty(message = "ourTradeArray不能为空")
    private String[] outTradeNoArray;
}
