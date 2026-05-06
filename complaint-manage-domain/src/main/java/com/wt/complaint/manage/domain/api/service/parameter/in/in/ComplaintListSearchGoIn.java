package com.wt.complaint.manage.domain.api.service.parameter.in;

import com.wt.complaint.manage.api.model.enums.SourceEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarEmployeeInfoGoOut;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComplaintListSearchGoIn implements Serializable {

    private static final long serialVersionUID = -6832503495432598091L;

    private List<ConditionGroup> conditionGroups;
    /**
     * @see SourceEnum
     */
    private String source;
    /**
     * pad端各个pad
     */
    private Integer tab;
    /**
     * 客诉单号
     */
    private String complaintNo;
    /**
     * 车牌�?
     */
    private String carNo;
    /**
     * 完整VIN�?
     */
    private String vin;
    /**
     * 联系人电�?
     */
    private String contactPhone;

    /**
     * 联系人电话MD5
     */
    private String contactPhoneMd5;
    /**
     * 投诉类型, 1 产品投诉 2 服务投诉
     */
    private Integer complaintType;
    /**
     * 投诉单状态列�?
     */
    private List<Integer> statusList;
    /**
     * 城市列表
     */
    private List<String> cityList;

    /**
     * 大区id列表
     */
    private List<String> zoneIdList;

    /**
     * 问题详情
     */
    private String problemDesc;

    /**
     * 催单次数
     */
    private Integer reminderTimes;

    /**
     * 门店id列表,支持多�?
     */
    private List<String> orgIdList;
    /**
     * 考核标签列表,是英文字符串
     */
    private List<String> tagList;
    /**
     * 风险等级,int 1~4
     */
    private List<Integer> riskLevelList;
    /**
     * 创建时间起始
     */
    private String createTimeStart;
    /**
     * 创建时间结束
     */
    private String createTimeEnd;
    /**
     * 完成时间起始
     */
    private String finishTimeStart;
    /**
     * 完成时间结束
     */
    private String finishTimeEnd;
    /**
     * 首响时间起始
     */
    private String firstResponseTimeStart;
    /**
     * 首响时间结束
     */
    private String firstResponseTimeEnd;
    /**
     * 是否有责�? 无责 1 有责
     */
    private Integer responsibility;
    /**
     * 创建来源, 1-服务门店 2-线上客服
     * @see com.wt.complaint.manage.api.model.enums.CreateSourceEnum
     */
    private Integer createSource;
    /**
     * 是否涉媒 0-�?1-�?
     */
    private Integer mediaInvolved;
    /**
     * 门店id, pad端必�?
     */
    private String orgId;
    /**
     * 搜索关键字，可以是投诉单�?vin�?�?车牌�?手机�?
     */
    private String searchKey;
    /**
     * 是否只显示我的综合订�?
     */
    private Boolean onlyShowMyCompositeOrder;
    /**
     * 操作员ID
     */
    private Long operatorMid;
    /**
     * 页码
     */
    private Integer pageNum;
    /**
     * 页码和pageSize算出�?
     */
    private Integer start;
    /**
     * 每页大小
     */
    private Integer pageSize;
    /**
     * 搜索总数,不需要前端传�?
     */
    private Long total;
    /**
     * 当前登录人mid
     */
    private Long mid;
    /**
     * 当前登录人traceId
     */
    private String traceId;

    /**
     * 当前登录人email
     */
    private String email;
    /**
     * 角色列表
     */
    private List<String> roleList;
    /**
     * 当前角色
     */
    private String currRole;
    /**
     * 手机号后4�?
     */
    private String contactPhoneSuffix;
    /**
     * vin�?�?
     */
    private String vinSuffix;
    /**
     * 是否仅查�?
     */
    private Integer onlyView;

    /**
     * 是否已提交复盘，0-�?1-是（客诉三期 tab=8 待复盘时�?0�?
     */
    private Integer reviewed;

    /**
     * 测试标识, 0-非测试环�? 1-是测试环�?
     */
    private Integer testTag;

    /**
     * 售后工作台权�?
     */
    private AfterSaleWorkbenchPermissionGroup afterSaleWorkbenchPermissionGroup;
    /**
     * Pad关联投诉单条�?
     */
    private PadRelateListGroup padRelateListGroup;

    // 内部类表示一组条�?
    public static class ConditionGroup {

        public List<Integer> riskLevelList;

        public String createTimeStart;

        public String createTimeEnd;

        public List<Integer> statusList;
    }

    @NoArgsConstructor
    @Data
    public static class AfterSaleWorkbenchPermissionGroup {
        /**
         * 汽车小区岗位信息
         */
        List<CarEmployeeInfoGoOut.ZonePositionInfo> littleZonePositionsInfoList;
        /**
         * 0-所有全�?1-大区权限 2-小区权限
         */
        private Integer afterSaleWorkbenchPermissionType;
        /**
         * 汽车渠道岗位信息
         */
        private List<CarEmployeeInfoGoOut.ChannelPositionInfo> channelPositionInfoList;
        /**
         * 汽车大区岗位信息
         */
        private List<CarEmployeeInfoGoOut.ZonePositionInfo> bigZonePositionsInfoList;
    }

    @NoArgsConstructor
    @Data
    public static class PadRelateListGroup {
        /**
         * 创建时间开�?
         */
        public String createTimeStart;
        /**
         * 创建时间结束
         */
        public String createTimeEnd;
        /**
         * 结案标签
         */
        public List<String> tagList;
        /**
         * 进行中状�?
         */
        private List<Integer> inProgressStatus;
        /**
         * 结束状�?
         */
        private List<Integer> completeStatus;

    }
}
