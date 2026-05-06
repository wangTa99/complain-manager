package com.wt.complaint.manage.api.model.resp.retail;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 投诉单详情页返回
 *
 * @author p-wangkai95
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RetailComplaintDetailFrameResp implements Serializable {

    @ApiDocClassDefine(value = "retailUserActionAuth", description = "用户操作按钮")
    private RetailUserActionAuth retailUserActionAuth;
}
