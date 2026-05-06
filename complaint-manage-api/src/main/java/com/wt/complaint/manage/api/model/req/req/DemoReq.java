package com.wt.complaint.manage.api.model.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 政策点赞请求参数
 *
 * @author wangshanjun
 * @date 2021/6/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemoReq implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 政策ID
     */
    private Long id;
    
    @NotNull(message = "区划过滤类型不可为空")
    private String aaa;
}
