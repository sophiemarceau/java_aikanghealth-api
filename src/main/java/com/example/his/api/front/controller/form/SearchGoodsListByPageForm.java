package com.example.his.api.front.controller.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class SearchGoodsListByPageForm {
    @Length(min = 1, max = 50, message = "keyword字数超出范围")
    private String keyword;

    @Pattern(regexp = "^父母体检$|^入职体检$|^职场白领$|^个人高端$|^中青年体检$",
            message = "type内容不正确")
    private String type;

    @Pattern(regexp = "^男性$|^女性$")
    private String sex;

    @Range(min = 1, max = 4, message = "priceType范围不正确")
    private Integer priceType;

    @Range(min = 1, max = 4, message = "orderType范围不正确")
    private Integer orderType;

    @NotNull(message = "page不能为空")
    @Min(value = 1, message = "page不能小于1")
    private Integer page;

    @NotNull(message = "length不能为空")
    @Range(min = 10, max = 50, message = "length必须为10~50之间")
    private Integer length;
}
