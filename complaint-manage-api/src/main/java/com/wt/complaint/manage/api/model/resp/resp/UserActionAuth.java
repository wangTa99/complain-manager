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
public class UserActionAuth implements Serializable {
    @ApiDocClassDefine(value = "actionsList", description = "用户操作按钮列表 接单 pickUp，派�?dispatch，改派处理人 reassignHandler，添加跟进记�?addFollowUpRecords，预约到店维�?appointmentMROrder" +
        "申请免责 applyExemption，申�?2H无法结案 apply72HUnfinished，申请改派门�?applyReassignStore，申请结�?applyFinish")
    private List<String> actionsList;

    @ApiDocClassDefine(value = "actionsList", description = "用户操作按钮列表 接单 pickUp，派�?dispatch，改派处理人 reassignHandler，添加跟进记�?addFollowUpRecords，预约到店维�?appointmentMROrder" +
            "申请免责 applyExemption，申�?2H无法结案 apply72HUnfinished，申请改派门�?applyReassignStore，申请结�?applyFinish")
    private List<String> buttons;
}
