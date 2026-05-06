package com.wt.complaint.manage.api.model.resp;

import com.wt.car.common.privacy.annotation.MaskAndEncrypted;
import com.wt.car.common.privacy.enums.MaskTypeEnum;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 客诉类单据列表查询参�?
 * @author linjiehong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserComplaintListSearchDTO implements Serializable {
    @ApiDocClassDefine(value = "ucNo", description = "举报单号")
    private String ucNo;

    @ApiDocClassDefine(value = "soNo", description = "服务单号")
    private String soNo;

    @ApiDocClassDefine(value = "superTicketNo", description = "超级工单�?)
    private String superTicketNo;

    @ApiDocClassDefine(value = "carNo", description = "车牌�?)
    private String carNo;

    @MaskAndEncrypted(maskType = MaskTypeEnum.VIN, encrypted = false)
    @ApiDocClassDefine(value = "vin", description = "vin�?)
    private String vin;

    @MaskAndEncrypted(maskType = MaskTypeEnum.NAME, encrypted = false)
    @ApiDocClassDefine(value = "contactName", description = "联系人姓�?)
    private String contactName;

    @MaskAndEncrypted(maskType = MaskTypeEnum.PHONE, encrypted = false)
    @ApiDocClassDefine(value = "contactPhone", description = "联系人电�?)
    private String contactPhone;

    @ApiDocClassDefine(value = "serviceScene", description = "举报场景：用,分隔")
    private String serviceScene;

    @ApiDocClassDefine(value = "orderStatus", description = "举报单状�?1-待接�?2-待举报判�?3-已完�?)
    private Integer orderStatus;

    @ApiDocClassDefine(value = "orderStatusName", description = "举报单状态名�?)
    private String orderStatusName;

    @ApiDocClassDefine(value = "orgId", description = "门店ID")
    private String orgId;

    @ApiDocClassDefine(value = "orgName", description = "门店名称")
    private String orgName;

    @ApiDocClassDefine(value = "operatorMid", description = "处理人mid")
    private Long operatorMid;

    @ApiDocClassDefine(value = "operatorName", description = "处理人姓�?)
    private String operatorName;

    @ApiDocClassDefine(value = "createTime", description = "创建时间")
    private String createTime;

    @ApiDocClassDefine(value = "finishTime", description = "完成时间")
    private String finishTime;

    @ApiDocClassDefine(value = "reminderTimes", description = "催单次数")
    private Integer reminderTimes;

}
