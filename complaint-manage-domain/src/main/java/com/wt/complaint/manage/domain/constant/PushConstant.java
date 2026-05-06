package com.wt.complaint.manage.domain.constant;

/**
 * 需要与 PushEnum 中的枚举保持一�?
 *
 * @author zhangzheyang
 * @date 2024/12/31
 */
public class PushConstant {

    public static final String NEW_COMPLAINT_TO_DEAL = "NEW_COMPLAINT_TO_DEAL";

    public static final String NEW_COMPLAINT_TO_VIEW = "NEW_COMPLAINT_TO_VIEW";

    public static final String FIRST_RESPONSE_TO_TIMEOUT = "FIRST_RESPONSE_TO_TIMEOUT";

    public static final String FINISH_TO_TIMEOUT = "FINISH_TO_TIMEOUT";

    public static final String REMIND = "REMIND";

    public static final String REASSIGNMENT_STORE_REFUSE = "REASSIGNMENT_STORE_REFUSE";

    public static final String APPLICATION_72H_CANNOT_BE_CLOSED_REFUSE = "APPLICATION_72H_CANNOT_BE_CLOSED_REFUSE";

    public static final String APPLICATION_FOR_WAIVER_REFUSE = "APPLICATION_FOR_WAIVER_REFUSE";

    public static final String REASSIGNMENT_STORE_AUDIT = "REASSIGNMENT_STORE_AUDIT";

    public static final String APPLICATION_FOR_WAIVER_AUDIT = "APPLICATION_FOR_WAIVER_AUDIT";

    public static final String APPLICATION_72H_CANNOT_BE_CLOSED_AUDIT = "APPLICATION_72H_CANNOT_BE_CLOSED_AUDIT";

    public static final String APPLICATION_FOR_CLOSURE_AUDIT = "APPLICATION_FOR_CLOSURE_AUDIT";

    public static final String PRODUCT_RISK_CLOSURE_APPLICATION_AUDIT = "PRODUCT_RISK_CLOSURE_APPLICATION_AUDIT";

    public static final String NEW_REPORT_TO_DEAL = "NEW_REPORT_TO_DEAL";

    public static final String REPORT_REMIND = "REPORT_REMIND";

    public static final String NOTIFY_CUSTOMER_SERVICE = "NOTIFY_CUSTOMER_SERVICE";

    public static final String DELIVER_NEW_COMPLAINT = "DELIVER_NEW_COMPLAINT";

    public static final String DELIVER_FIRST_RESPONSE_TO_TIMEOUT = "DELIVER_FIRST_RESPONSE_TO_TIMEOUT";

    public static final String DELIVER_FINISH_TO_TIMEOUT = "DELIVER_FINISH_TO_TIMEOUT";

    public static final String DELIVER_REMIND = "DELIVER_REMIND";

    public static final String DELIVER_REASSIGNMENT = "DELIVER_REASSIGNMENT";

    public static final String DELIVER_FINISH_TO_CUSTOMER_SERVICE = "DELIVER_FINISH_TO_CUSTOMER_SERVICE";

    public static final String RETAIL_FINISH_TO_CUSTOMER_SERVICE = "RETAIL_FINISH_TO_CUSTOMER_SERVICE";

    /**
     * 改派扩展字段key
     * 操作改派�?
     */
    public static final String PRE_OPERATOR = "preOperator";

    /**
     * 改派扩展字段key
     * 改派原因
     */
    public static final String REASON = "reason";

    /**
     * 改派扩展字段key
     * 改派人姓�?
     */
    public static final String REASSIGN_OPERATOR = "reassignOperator";

    /**
     * 跟进人姓�?
     */
    public static final String OPERATOR_NAME = "operatorName";

    /**
     * 客诉单id
     */
    public static final String COMPLAINT_ORDER_ID = "complaintOrderId";

    /**
     * 涉媒投诉仅门店推送id
     */
    public static final String MEDIA_INVOLVED_AUDIT = "MEDIA_INVOLVED_AUDIT";

    /**
     * 升级投诉仅门店推送id
     */
    public static final String PRODUCT_RISK_UPGRADE_AUDIT = "PRODUCT_RISK_UPGRADE_AUDIT";

    /**
     * �?5天未结案
     */
    public static final String UN_FINISHED_TO_TIMEOUT = "UN_FINISHED_TO_TIMEOUT";

    /**
     * 投诉待判责审批任�?
     */
    public static final String JUDGE_RESPONSIBILITY_AUDIT = "JUDGE_RESPONSIBILITY_AUDIT";

    /**
     * 服务投诉判责结果为门店有�?
     */
    public static final String STORE_RESPONSIBLE_AUDIT = "STORE_RESPONSIBLE_AUDIT";

    /**
     * 门店报备投诉单结案完�?
     */
    public static final String STORE_REPORT_CLOSURE = "STORE_REPORT_CLOSURE";

    /**
     * 提交服务投诉复盘完成
     */
    public static final String SUBMIT_REVIEW_CLOSURE = "SUBMIT_REVIEW_CLOSURE";

    /**
     * 服务满意度管理岗位ID（申请免责三审、服务投诉判责）
     * 跟进记录中该岗位审核人展示为：中台判责小�?

     */
    public static final String POSITION_SERVICE_SATISFACTION_MANAGEMENT = "174";

    /**
     * 判责人默认展示名称（服务满意度管理岗位在跟进记录中的展示名）
     */
    public static final String DISPLAY_NAME_CENTER_JUDGE_GROUP = "中台判责小组";

    /**
     * 咨询单创�?
     */
    public static final String NEW_CONSULT_TO_DEAL = "NEW_CONSULT_TO_DEAL";

    /**
     * 咨询单催�?
     */
    public static final String CONSULT_REMIND = "CONSULT_REMIND";

    public static final String CONSULT_REASSIGN = "CONSULT_REASSIGN";

}
