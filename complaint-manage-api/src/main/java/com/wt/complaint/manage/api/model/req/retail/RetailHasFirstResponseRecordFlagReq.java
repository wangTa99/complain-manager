package com.wt.complaint.manage.api.model.req.retail;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 零售查询是否有首响记录标识请�?
 *
 * @author p-wangkai95
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RetailHasFirstResponseRecordFlagReq implements Serializable {

    private static final long serialVersionUID = -585493188670217985L;
    
    @ApiDocClassDefine(value = "drNo", description = "客诉单号", required = true)
    @NotBlank(message = "drNo不能为空")
    private String drNo;
}
