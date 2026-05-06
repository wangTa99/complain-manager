package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeInfoBO {
    /**
     * miId
     */
    private Long miId;

    /**
     * 名称
     */
    private String name;

    /**
     * 岗位id
     */
    private Integer positionId;
    /**
     * 电话
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
}
