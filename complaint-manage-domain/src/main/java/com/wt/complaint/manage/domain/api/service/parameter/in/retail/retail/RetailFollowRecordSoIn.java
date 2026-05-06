package com.wt.complaint.manage.domain.api.service.parameter.in.retail;

import com.wt.complaint.manage.domain.api.service.parameter.in.AttachmentSoIn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RetailFollowRecordSoIn {
    /**
     * 客诉单号
     */
    private String drNo;

    /**
     * 跟进人mid
     */
    private String followUpMid;

    /**
     * 跟进人姓�?
     */
    private String followUpName;

    /**
     * 跟进详情
     */
    private String followInfo;

    /**
     * 附件信息
     */
    private List<AttachmentSoIn> attachmentList;
}
