package com.wt.complaint.manage.domain.api.service.parameter.in;

import com.wt.complaint.manage.domain.constant.MrRoleConstant;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class OrderUpdateHandlerSoIn {
    /**
     * 客诉单号
     */
    private String complaintNo;

    /**
     * 被改派的处理人mid
     */
    private String handlerMid;

    /**
     * 被改派的处理人姓�?
     */
    private String handlerName;

    /**
     * 派工人mid
     */
    private String dispatcherMid;

    /**
     * 派工人名�?
     */
    private String dispatcherName;

    /**
     * 派工人角�?
     */
    private String loginRole;

    public void checkUpdateHandlerSoIn() {
        if (this.complaintNo == null) {
            log.error("complaintNo is null, complaintNo: {}, pickUpMid: {}", this.complaintNo, this.dispatcherMid);
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "客诉单号为空");
        }
        if (this.dispatcherMid == null) {
            log.error("dispatcherMid is null, complaintNo: {}, pickUpMid: {}", this.complaintNo, this.dispatcherMid);
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "派单人mid不能为空");
        }
        if (this.loginRole == null) {
            log.error("loginRole is null, complaintNo: {}, pickUpMid: {}", this.complaintNo, this.dispatcherMid);
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "派单人岗位为�?);
        }
    }
}
