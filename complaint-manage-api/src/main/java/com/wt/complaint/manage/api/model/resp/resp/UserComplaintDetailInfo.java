package com.wt.complaint.manage.api.model.resp;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 用户投诉详情信息
 * @author linjiehong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserComplaintDetailInfo implements Serializable {
    @ApiDocClassDefine(value = "groupName", description = "分组名称")
    private String groupName;

    @ApiDocClassDefine(value = "groupOrder", description = "分组排序")
    private Integer groupOrder;

    @ApiDocClassDefine(value = "fields", description = "字段列表")
    private List<DetailField> fields;
}
