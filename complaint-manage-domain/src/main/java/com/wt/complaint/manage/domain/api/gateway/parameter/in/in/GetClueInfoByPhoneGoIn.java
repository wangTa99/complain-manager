package com.wt.complaint.manage.domain.api.gateway.parameter.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 依据手机号查询线索信息的请求参数封装
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetClueInfoByPhoneGoIn implements Serializable {

    /**
     * 手机�?
     */
    private String phone;
}
