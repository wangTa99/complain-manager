package com.wt.complaint.manage.api.model.resp;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 客诉类单据信�?
 * @author linjiehong
 * @date 2025/5/21 10:29
 */
@Data
public class UcOrderViewInfo implements Serializable {
    @ApiDocClassDefine(value = "serviceSceneList", description = "举报场景：用,分隔")
    private String serviceSceneList;

    @ApiDocClassDefine(value = "ucNo", description = "举报单号")
    private String ucNo;

    @ApiDocClassDefine(value = "orderStatus", description = "举报单状�?0-待接�?1-待举报判�?2-已完�?3-已撤销")
    private Integer orderStatus;

    @ApiDocClassDefine(value = "orderStatusName", description = "举报单状态名�?0-待接�?1-待举报判�?2-已完�?3-已撤销")
    private String orderStatusName;

    @ApiDocClassDefine(value = "createName", description = "创建人姓�?)
    private String createName;

    @ApiDocClassDefine(value = "orgId", description = "门店id")
    private String orgId;

    @ApiDocClassDefine(value = "orgName", description = "门店名称")
    private String orgName;

    @ApiDocClassDefine(value = "handleMid", description = "处理人mid")
    private Long handleMid;

    @ApiDocClassDefine(value = "handleName", description = "处理�?)
    private String handleName;

    @ApiDocClassDefine(value = "createTime", description = "创建时间")
    private String createTime;

    @ApiDocClassDefine(value = "finishTime", description = "完成时间")
    private String finishTime;

    @ApiDocClassDefine(value = "judgeType", description = "举报判定结果")
    private Integer judgeType;

    @ApiDocClassDefine(value = "userComplaintDetailInfos", description = "举报单详情信息列�?)
    private List<UserComplaintDetailInfo> userComplaintDetailInfos;
}
