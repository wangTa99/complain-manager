package com.wt.complaint.manage.api.model.resp.consult;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsultListDTO implements Serializable {

    @ApiDocClassDefine(value = "consultNo", description = "咨询单号")
    private String consultNo;

    @ApiDocClassDefine(value = "createTime", description = "创建时间")
    private String createTime;

    @ApiDocClassDefine(value = "consultType", description = "咨询类型（如：配件报价）")
    private String consultType;

    @ApiDocClassDefine(value = "vid", description = "车辆VID�?)
    private String vid;

    @ApiDocClassDefine(value = "vin", description = "车辆VIN�?)
    private String vin;

    @ApiDocClassDefine(value = "carVersion", description = "车型版本（如：SU7 PRO�?)
    private String carVersion;

    @ApiDocClassDefine(value = "expectedCallbackTime", description = "期望回电时间")
    private String expectedCallbackTime;

    @ApiDocClassDefine(value = "mrNo", description = "关联维保单号")
    private String mrNo;

    @ApiDocClassDefine(value = "stNo", description = "关联维保工单�?)
    private String stNo;

    @ApiDocClassDefine(value = "creator", description = "创建�?)
    private String creator;

    @ApiDocClassDefine(value = "urgentFlag", description = "是否紧急（1-紧急，0-非紧急）")
    private Integer urgentFlag;

    @ApiDocClassDefine(value = "priority", description = "优先�?)
    private Integer priority;


    @ApiDocClassDefine(value = "remindFlag", description = "是否催单�?-已催�?-未催�?)
    private Integer remindFlag;

    @ApiDocClassDefine(value = "carNo", description = "车牌�?)
    private String carNo;

    @ApiDocClassDefine(value = "handleResult", description = "处理结果�?-无需门店处理�?-已处理）")
    private Integer handleResult;

    @ApiDocClassDefine(value = "handleResultDesc", description = "处理结果描述")
    private String handleResultDesc;

    @ApiDocClassDefine(value = "orgId", description = "门店编码（仅webList返回�?)
    private String orgId;

    @ApiDocClassDefine(value = "orgName", description = "门店名称（仅webList返回�?)
    private String orgName;

    @ApiDocClassDefine(value = "consultStatus", description = "咨询单状�?)
    private Integer consultStatus;
}