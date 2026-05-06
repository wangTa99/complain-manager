package com.wt.complaint.manage.api.model.req;

import com.wt.complaint.manage.api.model.enums.SourceEnum;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComplaintListSearchReq implements Serializable {

    private static final long serialVersionUID = -6832503495432598091L;

    /**
     * @see SourceEnum
     */
    @NotBlank(message = "source不能为空")
    @ApiDocClassDefine(value = "source",
            description = "请求来源, 零售通PAD�?投诉单列�?PAD_LIST,零售通PAD�?新建工单时关联客诉单列表:PAD_RELATE_LIST, 售后工作台：AFTER_SALE_WORKBENCH",
            required = true)
    private String source;

    /**
     * @see com.wt.complaint.manage.api.model.enums.PadTabEnum
     */
    @ApiDocClassDefine(value = "tab",
            description = "tab, 1-全部, 2-待接�? 3-处理�? 4-即将超时, 5-待结案评�? 6-仅查�? 8-待复�?)
    private Integer tab;

    @ApiDocClassDefine(value = "complaintNo", description = "客诉单号")
    private String complaintNo;

    @ApiDocClassDefine(value = "carNo", description = "车牌�?)
    private String carNo;

    @ApiDocClassDefine(value = "vin", description = "完整VIN�?)
    private String vin;

    @ApiDocClassDefine(value = "contactPhone", description = "联系人电�?)
    private String contactPhone;

    /**
     * ComplaintTypeEnum
     */
    @ApiDocClassDefine(value = "complaintType", description = "投诉类型, 1 产品投诉 2 服务投诉")
    private Integer complaintType;

    @ApiDocClassDefine(value = "statusList", description = "投诉单状态列�?)
    private List<Integer> statusList;

    @ApiDocClassDefine(value = "cityList", description = "城市列表")
    private List<Integer> cityList;

    @ApiDocClassDefine(value = "orgIdList", description = "门店id列表,支持多�?)
    private List<String> orgIdList;

    @ApiDocClassDefine(value = "zoneIdList", description = "大区id列表,支持多�?)
    private List<Integer> zoneIdList;

    @ApiDocClassDefine(value = "problemDesc", description = "问题详情")
    private String problemDesc;

    @ApiDocClassDefine(value = "reminderTimes", description = "催单次数")
    private Integer reminderTimes;

    @ApiDocClassDefine(value = "tagList",
            description = "考核标签列表, 英文字符�? 投诉率免考核(COMPLAINT_RATE_ASSESSMENT_FREE) 72H无法结案(FINISH_72H_ASSESSMENT_FREE) " +
                    "首响超时(FIRST_RESPONSE_TIMEOUT) 结案超时(FINISH_TIMEOUT) 门店有责(STORE_RESPONSIBLE)")
    private List<String> tagList;

    @ApiDocClassDefine(value = "riskLevelList", description = "风险等级列表, int 1~4")
    private List<Integer> riskLevelList;

    @ApiDocClassDefine(value = "createTimeStart", description = "创建时间起始")
    private String createTimeStart;

    @ApiDocClassDefine(value = "createTimeEnd", description = "创建时间结束")
    private String createTimeEnd;

    @ApiDocClassDefine(value = "finishTimeStart", description = "完成时间起始")
    private String finishTimeStart;

    @ApiDocClassDefine(value = "finishTimeEnd", description = "完成时间结束")
    private String finishTimeEnd;

    @ApiDocClassDefine(value = "firstResponseTimeStart", description = "首响时间起始")
    private String firstResponseTimeStart;

    @ApiDocClassDefine(value = "firstResponseTimeEnd", description = "首响时间结束")
    private String firstResponseTimeEnd;

    /**
     * 客诉三期废弃：门店是否有责不再作为筛选条件，请使用考核标签 tagList（如 STORE_RESPONSIBLE�?
     */
    @Deprecated
    @ApiDocClassDefine(value = "responsibility", description = "已废弃，请使�?tagList 门店有责(STORE_RESPONSIBLE)")
    private Integer responsibility;

    /**
     * @see com.wt.complaint.manage.api.model.enums.CreateSourceEnum
     */
    @ApiDocClassDefine(value = "createSource", description = "创建来源, 1-服务门店 2-线上客服")
    private Integer createSource;

    /**
     * pad端和售后工作台都支持
     */
    @ApiDocClassDefine(value = "mediaInvolved", description = "是否涉媒 0-�?1-�?)
    private Integer mediaInvolved;

    /**
     * pad端必�?
     */
    @ApiDocClassDefine(value = "orgId", description = "门店id, pad端必�?, required = true)
    private String orgId;

    /**
     * 仅用于pad�?
     */
    @ApiDocClassDefine(value = "searchKey", description = "搜索关键字，可以是投诉单�?vin�?�?车牌�?手机�?)
    private String searchKey;

    /**
     * 仅用于pad�?
     */
    @ApiDocClassDefine(value = "onlyShowMyCompositeOrder", description = "是否只显示我的综合订�?)
    private Boolean onlyShowMyCompositeOrder;

    /**
     * 仅用于pad�?
     */
    @ApiDocClassDefine(value = "operatorMid", description = "操作员ID")
    private Long operatorMid;

    @ApiDocClassDefine(value = "pageNum", description = "页码, 默认�?")
    private Integer pageNum = 1;

    @ApiDocClassDefine(value = "pageSize", description = "每页大小, 默认�?0,最�?00,最�?")
    @Max(value = 100, message = "每页条数不能超过100")
    @Min(value = 1, message = "每页条数不能小于1")
    private Integer pageSize = 10;
}
