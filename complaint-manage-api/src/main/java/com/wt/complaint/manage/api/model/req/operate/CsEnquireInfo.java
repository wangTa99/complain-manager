package com.wt.complaint.manage.api.model.req.operate;

import com.wt.complaint.manage.api.model.Attachment;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class CsEnquireInfo implements Serializable {

    private static final long serialVersionUID = 1420760602947603855L;

    /**
     * 优先级：�?2，一�?4，高-8，紧�?16,@TicketPriorityEnum
     */
    private Integer priority;

    /**
     * 咨询类型,
     * todo-djf 详见枚举
     */
    private Integer enquireType;

    /**
     * 问题描述
     */
    private String remark;

    /**
     * 期望联系时间�?0位毫秒时间戳
     */
    private Long expectedTouchTime;

    /**
     * 附件信息
     */
    private List<Attachment> attachments;

    /**
     * 门店id
     */
    private String orgId;

    /**
     * 维保超级工单�?
     */
    private String mrSuperTicketNo;

}
