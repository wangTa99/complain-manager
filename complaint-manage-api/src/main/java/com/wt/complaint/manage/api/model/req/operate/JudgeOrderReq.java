package com.wt.complaint.manage.api.model.req.operate;

import com.wt.complaint.manage.api.model.Attachment;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 举报判定请求参数
 * @author linjiehong
 * @date 2025/5/19 13:26
 */
@Data
public class JudgeOrderReq implements Serializable {
    @ApiDocClassDefine(value = "ucNo", description = "客诉类作业单�?)
    private String ucNo;

    @ApiDocClassDefine(value = "judgeType", description = "判断结果")
    private Integer judgeType;

    @ApiDocClassDefine(value = "judgeReason", description = "判断原因")
    private String judgeReason;

    @ApiDocClassDefine(value = "attachmentList", description = "附件信息")
    private List<Attachment> attachmentList;

}
