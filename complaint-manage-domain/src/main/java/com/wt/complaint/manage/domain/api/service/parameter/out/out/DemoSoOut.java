package com.wt.complaint.manage.domain.api.service.parameter.out;

import com.wt.complaint.manage.domain.api.gateway.parameter.out.DemoGoOut;
import com.wt.proretail.newcommon.param.BaseParamModelSoOut;
import lombok.Data;

import java.util.List;

/**
 * 点赞业务层返回�?
 *
 * @author wangshanjun
 * @date 2021/6/17
 */
@Data
public class DemoSoOut extends BaseParamModelSoOut {
    
    /**
     * 点赞状�?
     */
    private Integer likeStatus;
    
    /**
     * 点赞数量
     */
    private Integer likeNum;
    
    /**
     * 点赞人群
     */
    private List<String> likeCrowd;
    
    public DemoSoOut convert2service(DemoGoOut demoGoOut) {
        DemoSoOut demoSoOut = new DemoSoOut();
        demoSoOut.setLikeCrowd(demoGoOut.getLikeCrowd());
        demoSoOut.setLikeNum(demoGoOut.getLikeNum());
        demoSoOut.setLikeStatus(demoGoOut.getLikeStatus());
        return demoSoOut;
    }
    
    
    
}
