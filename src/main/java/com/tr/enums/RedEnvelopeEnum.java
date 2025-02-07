package com.tr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedEnvelopeEnum {
    DEFAULT(0,"默认"),

    STARTED(1,"已开始"),

    END(2, "已结束")
    ;

    private Integer code;

    private String message;
}
