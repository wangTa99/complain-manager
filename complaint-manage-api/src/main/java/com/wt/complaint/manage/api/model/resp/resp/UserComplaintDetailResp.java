package com.wt.complaint.manage.api.model.resp;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserComplaintDetailResp implements Serializable {

    @ApiDocClassDefine(value = "serviceScene", description = "举报场景：用,分隔")
    private String serviceScene;

    @ApiDocClassDefine(value = "ucNo", description = "举报单号")
    private String ucNo;

    @ApiDocClassDefine(value = "soNo", description = "服务单号")
    private String soNo;

    @ApiDocClassDefine(value = "superTicketNo", description = "超级工单�?)
    private String superTicketNo;

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

    @ApiDocClassDefine(value = "handleName", description = "处理�?)
    private String handleName;

    @ApiDocClassDefine(value = "createTime", description = "创建时间")
    private String createTime;

    @ApiDocClassDefine(value = "finishTime", description = "完成时间")
    private String finishTime;

    @ApiDocClassDefine(value = "userComplaintDetailInfos", description = "举报单详情信息列�?)
    private List<UserComplaintDetailInfo> userComplaintDetailInfos;
}
