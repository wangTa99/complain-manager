package com.wt.complaint.manage.domain.statemachine;

import com.wt.complaint.manage.domain.api.service.parameter.in.AttachmentSoIn;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UcOrderContext {
    /** 客诉类单�?*/
    private String ucNo;

    /** 处理人mid */
    private String operateMid;

    /** 处理人名�?*/
    private String operateName;

    /** 处理内容 **/
    private String operateContent;

    /** 处理附件 **/
    private List<AttachmentSoIn> attachmentList;

    /** 操作结果 **/
    private int operateType;
}
