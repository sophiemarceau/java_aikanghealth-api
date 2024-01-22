package com.example.his.api.mis.controller.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;


import javax.validation.constraints.*;

@Data
public class SearchDeptByIdForm {

    @NotNull(message = "id不能为空")
    @Min(value = 1, message = "id不能小于1")
    private Integer id;

}
