package com.wt.complaint.manage.api.model.req.approve;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

/**
 * 服务投诉判责请求
 *
 * @author generated
 */
@Data
public class JudgeResponsibilityReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiDocClassDefine(value = "id", description = "审批单ID", required = true)
    private Long id;

    @ApiDocClassDefine(value = "responsible", description = "门店是否有责: 0-无责, 1-有责", required = true)
    private Integer responsible;

    @ApiDocClassDefine(value = "responsibleJudgeDesc", description = "审批意见；判门店有责时必�?, required = false)
    private String responsibleJudgeDesc;
}
