package com.wt.complaint.manage.api.model.req.consult;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PadConsultListReq {
    @ApiDocClassDefine(value = "orgId", description = "门店id")
    private String orgId;
    @ApiDocClassDefine(value = "consultStatus", description = "咨询单状�?)
    private Integer consultStatus;

    @ApiDocClassDefine(value = "key", description = "查询关键�? 支持咨询单号、车牌号、vin")
    private String key;

    @ApiDocClassDefine(value = "onlyMe", description = "是否仅自己，1代表是，0代表�?)
    private int onlyMe;

    private Long mid;

    @Min(value = 1, message = "页码不能小于1")
    @ApiDocClassDefine(value = "pageNum", description = "页码, 默认�?")
    private Integer pageNum = 1;

    @Max(value = 500, message = "每页条数不能超过500")
    @Min(value = 1, message = "每页条数不能小于1")
    @ApiDocClassDefine(value = "pageSize", description = "每页大小, 默认�?0,最�?00,最�?")
    private Integer pageSize = 10;



}
