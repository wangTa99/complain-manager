package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import com.xiaomi.mone.dubbo.docs.annotations.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarUserAggGoOut {
    /**
     * 系统用户数据
     */
    private Long sysUserMid;
    private String sysUserName;
    private String sysUserPhone;

    /**
     * 用户车辆信息
     */
    private String carVin;
    private String carVid;
    private String carNo;
}
