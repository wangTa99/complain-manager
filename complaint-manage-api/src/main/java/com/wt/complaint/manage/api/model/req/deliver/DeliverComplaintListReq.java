package com.wt.complaint.manage.api.model.req.deliver;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * 列表查询请求�?
 *
 * @author huxiankang
 * @date 2025/6/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliverComplaintListReq implements Serializable {

    @ApiDocClassDefine(value = "orgIds", description = "门店ids, �?,'拼接字符�?)
    private String orgIds;

    @ApiDocClassDefine(value = "drNo", description = "投诉单号")
    private String drNo;

    @ApiDocClassDefine(value = "tradeOrderId", description = "订单�?)
    private String tradeOrderId;

    @ApiDocClassDefine(value = "contactName", description = "联系�?)
    private String contactName;

    @ApiDocClassDefine(value = "contactPhone", description = "手机�?)
    private String contactPhone;

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

    @ApiDocClassDefine(value = "firstResponseTag", description = "首响超时，单选： 0-未超�? 1-已超�?)
    private Integer firstResponseTag;

    @ApiDocClassDefine(value = "finishTag", description = "结案超时，单选：0-未超�? 1-已超�?)
    private Integer finishTag;

    @ApiDocClassDefine(value = "createTime", description = "投诉日期, ['2025-06-20','2025-06-21']")
    private List<String> createTime;

    @ApiDocClassDefine(value = "realFirstResponseTime", description = "首响日期, ['2025-06-20','2025-06-21']")
    private List<String> realFirstResponseTime;

    @ApiDocClassDefine(value = "realFinishTime", description = "结案日期, ['2025-06-20','2025-06-21']")
    private List<String> realFinishTime;

    @ApiDocClassDefine(value = "onlyUnfinished", description = "仅看未完结投诉单")
    private Boolean onlyUnfinished = false;

    @Min(value = 1, message = "页码不能小于1")
    @ApiDocClassDefine(value = "pageNum", description = "页码, 默认�?")
    private Integer pageNum = 1;

    @Max(value = 500, message = "每页条数不能超过500")
    @Min(value = 1, message = "每页条数不能小于1")
    @ApiDocClassDefine(value = "pageSize", description = "每页大小, 默认�?0,最�?00,最�?")
    private Integer pageSize = 10;
}
