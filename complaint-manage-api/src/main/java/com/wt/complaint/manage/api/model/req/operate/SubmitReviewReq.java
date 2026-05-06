package com.wt.complaint.manage.api.model.req.operate;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

/**
 * 提交复盘请求（客诉三期）
 * 路径�?mtop/proretailcarpad/complaint/operate/submitReview
 */
@Data
public class SubmitReviewReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiDocClassDefine(value = "complaintNo", description = "投诉单号")
    private String complaintNo;

    @ApiDocClassDefine(value = "reviewMaterial", description = "复盘材料-飞书云文档链�?)
    private String reviewMaterial;
}
