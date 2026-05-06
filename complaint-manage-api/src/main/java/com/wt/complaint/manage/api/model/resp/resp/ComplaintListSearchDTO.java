package com.wt.complaint.manage.api.model.resp;

import com.wt.car.common.privacy.annotation.MaskAndEncrypted;
import com.wt.car.common.privacy.enums.MaskTypeEnum;
import com.wt.car.common.privacy.vo.BaseVO;
import com.wt.complaint.manage.api.model.enums.ComplaintStatusEnum;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComplaintListSearchDTO extends BaseVO {

    private static final long serialVersionUID = 8048291777112043027L;

    @ApiDocClassDefine(value = "complaintNo", description = "客诉单号")
    private String complaintNo;

    @ApiDocClassDefine(value = "soNo", description = "服务单号")
    private String soNo;

    @ApiDocClassDefine(value = "superTicketNo", description = "超级工单�?)
    private String superTicketNo;

    @ApiDocClassDefine(value = "carNo", description = "车牌�?)
    private String carNo;

    @MaskAndEncrypted(maskType = MaskTypeEnum.VIN)
    @ApiDocClassDefine(value = "vin", description = "vin�?)
    private String vin;

    @MaskAndEncrypted(maskType = MaskTypeEnum.NAME)
    @ApiDocClassDefine(value = "contactName", description = "联系人姓�?)
    private String contactName;

    @MaskAndEncrypted(maskType = MaskTypeEnum.PHONE)
    @ApiDocClassDefine(value = "contactPhone", description = "联系人电�?)
    private String contactPhone;

    @ApiDocClassDefine(value = "complaintType", description = "投诉类型 1 产品投诉 2 服务投诉")
    private Integer complaintType;

    @ApiDocClassDefine(value = "complaintTypeName", description = "投诉类型名称:产品投诉,服务投诉")
    private String complaintTypeName;

    /**
     * @see ComplaintStatusEnum
     */
    @ApiDocClassDefine(value = "status", description = "投诉单状�?")
    private Integer status;

    @ApiDocClassDefine(value = "statusName", description = "投诉单状态名�?)
    private String statusName;

    @ApiDocClassDefine(value = "createTime", description = "创建时间")
    private String createTime;

    @ApiDocClassDefine(value = "firstResponseTime", description = "首响时间")
    private String firstResponseTime;

    @ApiDocClassDefine(value = "finishTime", description = "结案时间")
    private String finishTime;

    @ApiDocClassDefine(value = "responsibility", description = "是否有责�? 无责 1 有责")
    private Integer responsibility;

    @ApiDocClassDefine(value = "responsibilityName", description = "是否有责名称")
    private String responsibilityName;

    @ApiDocClassDefine(value = "riskLevel", description = "风险等级,int 1~4")
    private Integer riskLevel;

    @ApiDocClassDefine(value = "riskLevelName", description = "风险等级名称")
    private String riskLevelName;

    @ApiDocClassDefine(value = "operatorMid", description = "处理人mid")
    private Long operatorMid;

    @ApiDocClassDefine(value = "operatorName", description = "处理人姓�?)
    private String operatorName;

    @ApiDocClassDefine(value = "跟进客服mid", description = "跟进客服mid")
    private Long customerServiceMid;

    @ApiDocClassDefine(value = "跟进客服姓名", description = "跟进客服姓名")
    private String customerServiceName;

    @ApiDocClassDefine(value = "orgId", description = "门店ID")
    private String orgId;

    @ApiDocClassDefine(value = "orgName", description = "门店名称")
    private String orgName;

    @ApiDocClassDefine(value = "tagList", description = "标签列表,包括英文和中文描�?)
    private List<TagDTO> tagList;

    @ApiDocClassDefine(value = "reminderTimes", description = "催单次数")
    private Integer reminderTimes;

    @ApiDocClassDefine(value = "problemCategory", description = "问题分类")
    private String problemCategory;

    @ApiDocClassDefine(value = "problemDesc", description = "问题描述")
    private String problemDesc;

    @ApiDocClassDefine(value = "onlyView", description = "投诉单是否门店仅查阅")
    private Integer onlyView;

    @ApiDocClassDefine(value = "cityId", description = "城市id")
    private String cityId;

    @ApiDocClassDefine(value = "zoneId", description = "区域id")
    private String zoneId;

    @ApiDocClassDefine(value = "upgradeTime", description = "升级投诉时间")
    private String upgradeTime;

    @ApiDocClassDefine(value = "createSource", description = "创建来源�?-服务门店 2-线上客服")
    private Integer createSource;

    @ApiDocClassDefine(value = "createSourceDesc", description = "创建来源描述")
    private String createSourceDesc;

    @ApiDocClassDefine(value = "reviewed", description = "是否已提交复盘：0-�?1-�?)
    private Integer reviewed;

    @ApiDocClassDefine(value = "exemptionApplyTimes", description = "免责申请次数，默�?")
    private Integer exemptionApplyTimes;

}
