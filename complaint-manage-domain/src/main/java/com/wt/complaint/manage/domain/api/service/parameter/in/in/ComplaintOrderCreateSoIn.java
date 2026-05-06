package com.wt.complaint.manage.domain.api.service.parameter.in;

import cn.hutool.core.collection.CollUtil;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 投诉订单创建输入�?
 */
@Data
public class ComplaintOrderCreateSoIn {
    /**
     * 车辆vid
     */
    private String vid;

    /**
     * 车辆vin
     */
    private String vin;

    /**
     * 作业类型
     */
    private Integer workType;

    /**
     * 服务单号
     */
    private String soNo;

    /**
     * 超级工单�?
     */
    private String superTicketNo;

    /**
     * 客诉单号
     */
    private String complaintNo;

    /**
     * 车型
     */
    private String carType;

    /**
     * 幂等ID
     */
    private String idempotentId;

    /**
     * 联系人密�?
     */
    private String contactName;

    /**
     * 联系人手机密�?
     */
    private String contactTel;

    /**
     * 联系人尊�?
     */
    private Integer contactTitle;

    /**
     * 城市id
     */
    private String cityId;

    /**
     * 分公司Id
     */
    private String areaId;

    /**
     * 测试标识, 0-非测试环�? 1-是测试环�?
     */
    private Integer testTag;

    /**
     * 创建人mid
     */
    private Long createMid;

    /**
     * 创建来源, 1-服务门店, 2-在线客服
     */
    private Integer createSource;

    /**
     * 扩展信息
     */
    private ComplaintOrderCreateExpandSoIn expandSoIn;


    public void checkCreateSoIn() {
        if (StringUtils.isEmpty(vid)) {
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "车辆vid不能为空");
        }
        if (Objects.isNull(this.expandSoIn)) {
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "扩展信息不能为空");
        }
        if (StringUtils.isEmpty(this.expandSoIn.getCustomerServiceMid())) {
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "跟进客服不能为空");
        }
        if (CollUtil.isEmpty(this.expandSoIn.getComplaintInfo())) {
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "客诉信息不能为空");
        }
        if (StringUtils.isEmpty(this.idempotentId)) {
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "幂等id不能为空");
        }
    }
}
