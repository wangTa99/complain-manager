package com.wt.complaint.manage.api.model.resp.retail;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * 用户操作权限
 *
 * @author p-wangkai95
 * @version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RetailUserActionAuth implements Serializable {
    @ApiDocClassDefine(value = "actionsList", description = "用户操作按钮列表 改派门店 reassignStore，添加跟进记�?addFollowUpRecords，申请结�?applyFinish")
    private Set<String> actionsList;
}
