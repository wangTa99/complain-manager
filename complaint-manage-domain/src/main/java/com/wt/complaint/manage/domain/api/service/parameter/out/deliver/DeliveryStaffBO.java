package com.wt.complaint.manage.domain.api.service.parameter.out.deliver;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 交付人员信息
 *
 * @author huxiankang
 * @date 2025/10/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryStaffBO {
    @ApiDocClassDefine(value = "orderId", description = "订单id")
    private String orderId;

    @ApiDocClassDefine(value = "staffAMiId", description = "交付邀约专员miId")
    private Long staffAMiId;

    @ApiDocClassDefine(value = "positionAUserName", description = "交付邀约专�?)
    private String positionAUserName;

    @ApiDocClassDefine(value = "staffBmiId", description = "交付接待专员miId")
    private Long staffBMiId;

    @ApiDocClassDefine(value = "positionBUserName", description = "交付接待专员")
    private String positionBUserName;
}
