package com.wt.complaint.manage.domain.api.gateway.parameter.in.rpc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 零售客诉创建流程请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RetailComplaintCreateBPMGoIn {
    private String key;
    private String name;
    private String requestId;
    private String creator;
    private Map<String, Object> extra;
    private String html;
    private String content;
    private String businessId;
}
