package com.wt.complaint.manage.api.model.resp;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 用户操作权限
 * @author linjiehong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UseComplaintActionAuth implements Serializable {
    @ApiDocClassDefine(value = "actionsList", description = "用户操作按钮列表 接单 pickUp，添加跟进记�?addFollowUpRecords，举报判�?reportJudgment")
    private List<String> actionsList;
}
