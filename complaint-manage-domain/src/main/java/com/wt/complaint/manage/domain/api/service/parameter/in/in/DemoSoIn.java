package com.wt.complaint.manage.domain.api.service.parameter.in;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.DemoGoIn;
import com.wt.proretail.newcommon.param.BaseParamModelSoIn;
import lombok.Data;

/**
 * 点赞业务层入�?
 *
 * @author wangshanjun
 * @date 2021/6/17
 */
@Data
public class DemoSoIn extends BaseParamModelSoIn {
    
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
    
    public DemoGoIn convert2service() {
        DemoGoIn request = new DemoGoIn();
        request.setBusinessId(this.businessId);
        request.setBusinessType(this.businessType);
        request.setMino(this.mino);
        return request;
    }
    
}
