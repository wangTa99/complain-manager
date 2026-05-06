package com.wt.complaint.manage.api.model.req.retail;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统计TAB数量请求参数
 *
 * @author p-wangkai95
 * @version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StaticRetailCountReq implements Serializable {

    private static final long serialVersionUID = 5493376519802552365L;

    @ApiDocClassDefine(value = "type", description = "数据类型 0-大区 1-小区 2-门店")
    private Integer type;

    @ApiDocClassDefine(value = "value", description = "选项实际�?)
    private String value;

    @ApiDocClassDefine(value = "orgCode", description = "下钻门店")
    private String orgCode;

    @ApiDocClassDefine(value = "searchTerm", description = "搜索条件")
    private String searchTerm;
}
