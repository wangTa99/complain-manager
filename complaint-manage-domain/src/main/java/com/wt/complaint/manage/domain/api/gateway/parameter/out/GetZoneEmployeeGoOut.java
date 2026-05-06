package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取大区员工信息返回结果
 * 封装小米id和姓�?
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetZoneEmployeeGoOut {
    /**
     * 米聊�?
     */
    private Long miId;
    /**
     * 姓名
     */
    private String name;
}
