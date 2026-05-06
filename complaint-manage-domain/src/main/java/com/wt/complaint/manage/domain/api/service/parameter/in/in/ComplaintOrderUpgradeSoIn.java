package com.wt.complaint.manage.domain.api.service.parameter.in;

import com.wt.car.soc.api.dto.GroupValueDto;
import com.wt.complaint.manage.domain.api.enums.ComplaintTypeEnum;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 投诉订单升级输入�?
 */
@Data
@Slf4j
public class ComplaintOrderUpgradeSoIn {

    /**
     * 投诉单号
     */
    private String complaintNo;

    /**
     * 目标投诉类型: 1-产品投诉, 2-服务投诉
     */
    private Integer targetType;

    /**
     * 升级原因
     */
    private String upgradeReason;

    /**
     * 操作人MID
     */
    private Long operatorMid;

    /**
     * 操作人名�?
     */
    private String operatorName;

    /**
     * 投诉字段详情
     */
    private List<GroupValueDto> complaintContent;

    /**
     * 操作来源：PAD_DETAIL:Pad零售�? CUSTOMER_SERVICE_WORKBENCH:客服工作�?
     */
    private String operateSource;

    /**
     * 检查客诉升级参�?
     */
    public void checkUpgradeSoIn() {
        if (Objects.isNull(this.getOperatorMid()) || this.getOperatorMid() == 0L) {
            log.warn("operatorMid required, complaintNo: {}, operatorMid: {}", this.complaintNo, this.getOperatorMid());
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "操作人不能为�?);
        }
        if (StringUtils.isEmpty(this.getComplaintNo())) {
            log.warn("complaintNo is null, complaintNo: {}", this.complaintNo);
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "投诉单号不能为空");
        }
        if (StringUtils.isEmpty(this.getUpgradeReason())) {
            log.warn("upgradeReason is null, complaintNo: {}, upgradeReason: {}", this.complaintNo, this.getUpgradeReason());
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "升级原因不能为空");
        }
        if (this.getUpgradeReason().length() > 200) {
            log.warn("upgradeReason exceed limit, complaintNo: {}, upgradeReason: {}", this.complaintNo, this.getUpgradeReason());
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "升级原因不能超过200�?);
        }
        ComplaintTypeEnum complaintTypeEnum = ComplaintTypeEnum.getEnumByCode(this.getTargetType());
        if (complaintTypeEnum == null || complaintTypeEnum == ComplaintTypeEnum.PRODUCT_RISK) {
            log.warn("targetType can not be PRODUCT_RISK, complaintNo: {}, targetType: {}", this.complaintNo, this.getTargetType());
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "升级目标投诉类型不能为产品风�?);
        }
        if (Objects.isNull(this.getOperatorMid())) {
            log.warn("operatorMid is null, complaintNo: {}", this.complaintNo);
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "操作人Mid不能为空");
        }
    }

}
