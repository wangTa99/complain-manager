package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import com.wt.proretail.newcommon.param.BaseParamModelGoOut;
import lombok.Data;

import java.util.List;

/**
 * 点赞网关层返回�?
 *
 * @author wangshanjun
 * @date 2021/6/17
 */
@Data
public class DemoGoOut extends BaseParamModelGoOut {
    
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
    
}
