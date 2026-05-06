package com.wt.complaint.manage.api.model.req.retail;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 投诉单列表搜�?
 *
 * @author p-wangkai95
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RetailComplaintListSearchReq implements Serializable {

    private static final long serialVersionUID = 522350008918092736L;

    @ApiDocClassDefine(value = "tab",
            description = "tab 1-待接�? 2-处理�? 3-即将超时, 4-已结�?, required = true)
    private Integer tab;

    @ApiDocClassDefine(value = "type", description = "数据类型 0-大区 1-小区 2-门店")
    private Integer type;

    @ApiDocClassDefine(value = "value", description = "选项实际�?)
    private String value;

    @ApiDocClassDefine(value = "orgCode", description = "下钻门店")
    private String orgCode;

    @ApiDocClassDefine(value = "searchTerm", description = "搜索条件 手机�?投诉单号")
    private String searchTerm;

    @ApiDocClassDefine(value = "pageNum", description = "页码, 默认�?", required = true)
    private Integer pageNum = 1;

    @ApiDocClassDefine(value = "pageSize", description = "每页大小, 默认�?0,最�?00,最�?", required = true)
    @Max(value = 100, message = "每页条数不能超过100")
    @Min(value = 1, message = "每页条数不能小于1")
    private Integer pageSize = 10;
}
