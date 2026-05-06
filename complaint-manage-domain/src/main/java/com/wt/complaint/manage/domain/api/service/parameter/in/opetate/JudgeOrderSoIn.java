package com.wt.complaint.manage.domain.api.service.parameter.in.opetate;

import com.wt.complaint.manage.domain.api.service.parameter.in.AttachmentSoIn;
import lombok.Data;

import java.util.List;

/**
 * @author linjiehong
 * @date 2025/5/26 14:29
 */
@Data
public class JudgeOrderSoIn {
    /**
     * 客诉类单�?
     */
    private String ucNo;

    /**
     * 判定结果
     */
    private Integer judgeType;

    /**
     * 判定结果描述
     */
    private String judgeContent;

    /**
     * 用户mid
     */
    private String userMid;

    /**
     * 登录用户角色
     */
    private String loginRole;

    /**
     * 附件列表
     */
    private List<AttachmentSoIn> attachmentList;
}
