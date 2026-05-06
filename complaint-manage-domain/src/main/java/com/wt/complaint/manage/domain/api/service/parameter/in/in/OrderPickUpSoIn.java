package com.wt.complaint.manage.domain.api.service.parameter.in;

import com.wt.complaint.manage.domain.constant.MrRoleConstant;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class OrderPickUpSoIn {
    /**
     * 客诉单号
     */
    private String complaintNo;

    /**
     * 客诉类单�?
     */
    private String ucNo;

    /**
     * 接单人mid
     */
    private String pickUpMid;

    /**
     * 接单人姓�?
     */
    private String pickUpName;

    /**
     * 登录角色
     */
    private String loginRole;

    public void checkPickUpSoIn() {
        if (this.complaintNo == null) {
            log.error("complaintNo is null, complaintNo: {}, pickUpMid: {}", this.complaintNo, this.pickUpMid);
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "客诉单号为空");
        }
        if (this.pickUpMid == null) {
            log.error("pickUpMid is null, complaintNo: {}, pickUpMid: {}", this.complaintNo, this.pickUpMid);
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "接单人mid不能为空");
        }
        if (this.loginRole == null) {
            log.error("loginRole is null, complaintNo: {}, pickUpMid: {}", this.complaintNo, this.pickUpMid);
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "接单人职位信息有�?);
        }
    }
}
