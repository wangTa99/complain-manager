package com.wt.complaint.manage.api.model.req.operate;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

/**
 * 建单请求参数
 * @author linjiehong
 * @date 2025/5/19 10:43
 */
@Data
public class CreateOrderReq implements Serializable {
    @ApiDocClassDefine(value = "vid", description = "车vid")
    private String vid;

    @ApiDocClassDefine(value = "workType", description = "作业类型")
    private Integer workType;

    @ApiDocClassDefine(value = "soNo", description = "服务单号")
    private String soNo;

    @ApiDocClassDefine(value = "superTicketNo", description = "超级工单�?)
    private String superTicketNo;

    @ApiDocClassDefine(value = "idempotentId", description = "幂等ID")
    private String idempotentId;

    @ApiDocClassDefine(value = "orgId", description = "门店id")
    private String orgId;

    @ApiDocClassDefine(value = "contactName", description = "联系人密�?)
    private String contactName;

    @ApiDocClassDefine(value = "contactTel", description = "联系人手机密�?)
    private String contactTel;

    @ApiDocClassDefine(value = "contactTitle", description = "联系人尊�?)
    private Integer contactTitle;

    @ApiDocClassDefine(value = "testTag", description = "测试标识, 0-非测试环�? 1-是测试环�?)
    private Integer testTag;

    @ApiDocClassDefine(value = "createMid", description = "创建人mid")
    private Long createMid;

    @ApiDocClassDefine(value = "expand", description = "扩展信息")
    private ComplaintOrderCreateExpandDTO expand;
}
