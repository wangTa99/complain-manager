package com.wt.complaint.manage.domain.api.gateway.parameter.in;

import com.wt.proretail.newcommon.param.BaseParamModelGoIn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComplaintOrderIn extends BaseParamModelGoIn {



    // 车辆编号
    private String carNo;

    // 车辆类型
    private String carType;

    // 车辆识别�?
    private String vin;

    // 状态：1进度更新 2待跟�?
    private int status;

    // 消息
    private String message;

    // 投诉单编�?
    private String complaintNo;

    // 组织ID
    private String orgId;

    // 组织名称
    private String orgName;

    // 页面URL
    private String pageUrl;

    //推送渠道列�?
    private List<Integer> pushChannelList;


    //特殊指定与工单相关的mid
    private Map<String, List<Long>> midList;



    //消息接受者角色列�?
    private List<String> roleList;


    //角色类型

    private Integer roleType;



}
