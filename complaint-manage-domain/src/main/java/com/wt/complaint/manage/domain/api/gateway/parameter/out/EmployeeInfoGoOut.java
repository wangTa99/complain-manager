package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import com.alibaba.cloud.commons.lang.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeInfoGoOut {
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

    /**
     * 邮箱前缀
     */
    private String emailPrefix;

    public void fillEmailPrefix() {
        if (StringUtils.isNotBlank(email) && email.contains("@")) {
            this.emailPrefix = email.substring(0, email.indexOf("@"));
        }
    }
}
