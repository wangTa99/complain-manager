package com.wt.complaint.manage.domain.api.service.parameter.in;

import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import lombok.Data;

@Data
public class OrderRemindSoIn {
    /**
     * 客诉单号
     */
    private String complaintNo;

    /**
     * 客诉类单�?
     */
    private String ucNo;

    /**
     * 咨询单号
     */
    private String consultNo;

    /**
     * 催单信息
     */
    private String orderRemindInfo;
    /**
     * 催单人mid
     */
    private String reminderMid;

    /**
     * 催单人姓�?
     */
    private String reminderName;

    public void checkOrderRemind() {
        if (complaintNo == null || complaintNo.isEmpty()) {
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "客诉单号不可为空");
        }
        if (orderRemindInfo == null || orderRemindInfo.isEmpty()) {
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "催单信息不可为空");
        }
        if (reminderMid == null || reminderMid.isEmpty()) {
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "催单人不可为�?);
        }
    }
}
