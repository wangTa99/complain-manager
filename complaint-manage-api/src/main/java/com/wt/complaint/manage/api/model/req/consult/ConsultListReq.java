package com.wt.complaint.manage.api.model.req.consult;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsultListReq implements Serializable {

//    咨询单号:精确搜索
//    咨询类型:单�?透传客服系统的类�?
//    VIN:精确搜索
//    处理结果:单选、无需门店处理、已处理
//    单据状�?单�?待接单、待首响、待结案、已完成
//    紧急程�?一般、紧�?
//    门店搜索:
//    支持门店ID精确搜索或门店名称的模糊搜索
//            单选即�?
//    创建时间
//    结案时间



    @ApiDocClassDefine(value = "org", description = "门店搜索")
    private String orgId;

    @ApiDocClassDefine(value = "consultNo", description = "咨询单号")
    private String consultNo;

    @ApiDocClassDefine(value = "consultType", description = "咨询单类�?)
    private Byte consultType;

    @ApiDocClassDefine(value = "vin", description = "完整的vin")
    private String vin;

    @ApiDocClassDefine(value = "handleResult", description = "处理结果")
    private Integer handleResult;

    @ApiDocClassDefine(value = "consultStatus", description = "咨询单状�?)
    private Integer consultStatus;

    @ApiDocClassDefine(value = "urgencyLevel", description = "紧急程�?)
    private Integer urgencyLevel;

    @ApiDocClassDefine(value = "createTimeStart", description = "创建时间起始")
    private String createTimeStart;

    @ApiDocClassDefine(value = "createTimeEnd", description = "创建时间结束")
    private String createTimeEnd;

    @ApiDocClassDefine(value = "finishTimeStart", description = "结案时间起始")
    private String finishTimeStart;

    @ApiDocClassDefine(value = "finishTimeEnd", description = "结案时间结束")
    private String finishTimeEnd;


    @Min(value = 1, message = "页码不能小于1")
    @ApiDocClassDefine(value = "pageNum", description = "页码, 默认�?")
    private Integer pageNum = 1;

    @Max(value = 500, message = "每页条数不能超过500")
    @Min(value = 1, message = "每页条数不能小于1")
    @ApiDocClassDefine(value = "pageSize", description = "每页大小, 默认�?0,最�?00,最�?")
    private Integer pageSize = 10;
}
