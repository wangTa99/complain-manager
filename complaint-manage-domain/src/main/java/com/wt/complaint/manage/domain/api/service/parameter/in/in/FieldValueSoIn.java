package com.wt.complaint.manage.domain.api.service.parameter.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FieldValueSoIn {
    /**
     * 针对选项有枚举值的场景
     */
    private String code;
    /**
     * 选项�?
     */
    private String desc;
    /**
     * 级联全路径id
     */
    private String pathId;
    /**
     * 级联全路径名�?
     */
    private String pathName;
}
