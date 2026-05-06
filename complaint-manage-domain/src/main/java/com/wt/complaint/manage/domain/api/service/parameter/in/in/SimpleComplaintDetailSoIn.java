package com.wt.complaint.manage.domain.api.service.parameter.in;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleComplaintDetailSoIn implements Serializable {

    private static final long serialVersionUID = 8543377916665180826L;

    /**
     * 客诉单号
     */
    private String complaintNo;

    /**
     * 登录人mid
     */
    private String midStr;
}
