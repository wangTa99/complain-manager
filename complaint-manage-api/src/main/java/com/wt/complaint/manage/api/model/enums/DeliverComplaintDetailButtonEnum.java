package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审批单类型枚�?
 */
@AllArgsConstructor
@Getter
public enum DeliverComplaintDetailButtonEnum {

      REASSIGN("reassign", "改派"),
      START_PROCESS("start_process", "开始处�?),
      FINISH("finish", "结案"),
      APPLY_EXEMPTION("apply_exemption", "申请免责"),
      WITH_RESPONSIBILITY("with_responsibility", "有责"),
      WITHOUT_RESPONSIBILITY("without_responsibility", "无责"),

      ;

    private final String code;
    private final String desc;


}
