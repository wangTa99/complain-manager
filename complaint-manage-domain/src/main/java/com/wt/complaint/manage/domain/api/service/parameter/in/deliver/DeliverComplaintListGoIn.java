package com.wt.complaint.manage.domain.api.service.parameter.in.deliver;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
/**
 * 列表入参
 * @author huxiankang
 * @date 2025-06-24 14:15:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliverComplaintListGoIn extends DeliverComplaintDataPermissionGoIn{

    private Boolean useMaster = false;

    @ApiDocClassDefine(value = "orgIds", description = "门店ids")
    private List<String> orgIds;

    @ApiDocClassDefine(value = "testTag", description = "测试数据", required = true)
    private Integer testTag;

    @ApiDocClassDefine(value = "drNo", description = "投诉单号")
    private String drNo;

    @ApiDocClassDefine(value = "tradeOrderId", description = "订单�?)
    private String tradeOrderId;

    @ApiDocClassDefine(value = "contactName", description = "联系�?)
    private String contactName;

    @ApiDocClassDefine(value = "contactNameMd5", description = "联系�?)
    private String contactNameMd5;

    @ApiDocClassDefine(value = "contactPhone", description = "手机�?)
    private String contactPhone;

    @ApiDocClassDefine(value = "contactPhone", description = "手机�?)
    private String contactPhoneMd5;

    @ApiDocClassDefine(value = "riskLevel", description = "风险等级，多选：L1、L2、L3、L4")
    private List<String> riskLevel;

    @ApiDocClassDefine(value = "lastCategoryId", description = "末级问题类目id，多�?)
    private List<Integer> lastCategoryId;

    @ApiDocClassDefine(value = "lastComplaintSceneId", description = "末级投诉场景id，多�?)
    private List<Integer> lastComplaintSceneId;

    @ApiDocClassDefine(value = "orderStatus", description = "投诉状态，多选： 10-待首�?20-跟进�?50-已结�?)
    private List<Integer> orderStatus;

    @ApiDocClassDefine(value = "responsible", description = "判责状态，多选：1-有责 2-无责 3-待判�?)
    private List<Integer> responsible;

    @ApiDocClassDefine(value = "operatorPositionId", description = "跟进岗位")
    private List<Integer> operatorPositionId;

    @ApiDocClassDefine(value = "operatorMid", description = "跟进人mid")
    private List<Long> operatorMid;

    @ApiDocClassDefine(value = "reminderTimes", description = "用户催单，多选：0-未催单�?-催单1次�?-催单2次以�?)
    private List<Integer> reminderTimes;

    @ApiDocClassDefine(value = "firstResponseTag", description = "首响超时，单选： 0-未首响超�? 1-已首响超�?)
    private Integer firstResponseTag;

    @ApiDocClassDefine(value = "finishTag", description = "结案超时，单选：0-未结案超�? 1-已结案超�?)
    private Integer finishTag;

    @ApiDocClassDefine(value = "createTimeStart", description = "投诉日期�? 格式：yyyy-MM-dd HH:mm:ss")
    private String createTimeStart;

    @ApiDocClassDefine(value = "createTimeEnd", description = "投诉日期�? 格式：yyyy-MM-dd HH:mm:ss")
    private String createTimeEnd;

    @ApiDocClassDefine(value = "realFirstResponseTimeStart", description = "首响日期�? 格式：yyyy-MM-dd HH:mm:ss")
    private String realFirstResponseTimeStart;

    @ApiDocClassDefine(value = "realFirstResponseTimeEnd", description = "首响日期�? 格式：yyyy-MM-dd HH:mm:ss")
    private String realFirstResponseTimeEnd;

    @ApiDocClassDefine(value = "realFinishTimeStart", description = "结案日期�? 格式：yyyy-MM-dd HH:mm:ss")
    private String realFinishTimeStart;

    @ApiDocClassDefine(value = "realFinishTimeEnd", description = "结案日期�? 格式：yyyy-MM-dd HH:mm:ss")
    private String realFinishTimeEnd;

    @ApiDocClassDefine(value = "onlyUnfinished", description = "仅看未完结投诉单")
    private Boolean onlyUnfinished = false;

    @ApiDocClassDefine(value = "offset", description = "偏移�?)
    private Integer offset;

    @ApiDocClassDefine(value = "pageSize", description = "每页大小, 默认�?0,最�?00,最�?")
    private Integer pageSize;

    private String traceId;
}
