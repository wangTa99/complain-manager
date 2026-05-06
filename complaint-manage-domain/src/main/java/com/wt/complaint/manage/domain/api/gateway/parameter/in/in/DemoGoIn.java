package com.wt.complaint.manage.domain.api.gateway.parameter.in;

import com.wt.proretail.newcommon.param.BaseParamModelGoIn;
import lombok.Data;

/**
 * 点赞网关层入�?
 *
 * @author wangshanjun
 * @date 2021/6/17
 */
@Data
public class DemoGoIn extends BaseParamModelGoIn {
    
    /**
     * 文章ID
     */
    private Long businessId;
    
    /**
     * 用户米聊�?
     */
    private String mino;
    
    /**
     * 文章类型
     */
    private Integer businessType;
    
}
