package com.wt.complaint.manage.domain.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <a href="https://xiaomi.f.mioffice.cn/wiki/SdNIwAFhoiNuexkRGmyk10il4Nb">配置模板</a>
 * <a href="https://xiaomi.f.mioffice.cn/docx/doxk44XSL3sb4orDRJ73xYFpMMf">客诉管理消息中心相关汇�?/a>
 *
 * @author zhangzheyang
 * @date 2024/12/31
 */
@Getter
@AllArgsConstructor
public enum PushEnum {

    /**
     * 飞书和另�?个渠道不能一个场景id,这里要处�?分别�?300000333�?300000330
     */
    NEW_COMPLAINT_TO_DEAL(true, 300000333,
            "NEW_COMPLAINT_TO_DEAL", "新投诉单待处�?站内消息和mi push渠道,符合飞书发消息条�?,
            Arrays.asList(ComplaintChannelEnum.NR_BOX,
                    ComplaintChannelEnum.NR_MI_PUSH)),

    NEW_COMPLAINT_TO_DEAL_ONLY_MI_PUSH(true, 300000330,
            "NEW_COMPLAINT_TO_DEAL_ONLY_MI_PUSH", "新投诉单待处�?飞书消息发�?,
            Collections.singletonList(ComplaintChannelEnum.MI_OFFICE_EMAIL)),

    NEW_COMPLAINT_TO_DEAL_WITHOUT_MI_OFFICE(true, 300000333,
            "NEW_COMPLAINT_TO_DEAL", "新投诉单待处�?不符合飞书消息发送条�?,
            Arrays.asList(ComplaintChannelEnum.NR_BOX,
                    ComplaintChannelEnum.NR_MI_PUSH)),

    NEW_COMPLAINT_TO_VIEW(true, 300000334, "NEW_COMPLAINT_TO_VIEW", "新投诉单待查�?,
            Arrays.asList(ComplaintChannelEnum.NR_BOX,
                    ComplaintChannelEnum.NR_MI_PUSH)),

    FIRST_RESPONSE_TO_TIMEOUT(true, 300000335, "FIRST_RESPONSE_TO_TIMEOUT", "首响即将超时",
            Arrays.asList(ComplaintChannelEnum.NR_BOX,
                    ComplaintChannelEnum.NR_MI_PUSH)),

    FIRST_RESPONSE_TO_TIMEOUT_ONLY_MI_PUSH(true, 300000377,
            "FIRST_RESPONSE_TO_TIMEOUT_ONLY_MI_PUSH", "首响即将超时,飞书消息发�?,
            Collections.singletonList(ComplaintChannelEnum.MI_OFFICE_EMAIL)),

    FINISH_TO_TIMEOUT(true, 300000336, "FINISH_TO_TIMEOUT", "结案即将超时",
            Arrays.asList(ComplaintChannelEnum.NR_BOX,
                    ComplaintChannelEnum.NR_MI_PUSH)),

    FINISH_TO_TIMEOUT_ONLY_MI_PUSH(true, 300000378,
            "FINISH_TO_TIMEOUT_ONLY_MI_PUSH", "结案即将超时,飞书消息发�?,
            Collections.singletonList(ComplaintChannelEnum.MI_OFFICE_EMAIL)),

    REMIND(true, 300000337, "REMIND", "被催�?,
            Arrays.asList(ComplaintChannelEnum.NR_BOX, ComplaintChannelEnum.NR_MI_PUSH)),
    REMIND_ONLY_MI_PUSH(true, 300000379,
            "REMIND_ONLY_MI_PUSH", "投诉单被催单,飞书消息发�?,
            Collections.singletonList(ComplaintChannelEnum.MI_OFFICE_EMAIL)),
    REASSIGNMENT_STORE_REFUSE(true, 300000338, "REASSIGNMENT_STORE_REFUSE", "改派门店驳回",
            Arrays.asList(ComplaintChannelEnum.NR_BOX, ComplaintChannelEnum.NR_MI_PUSH)),
    APPLICATION_72H_CANNOT_BE_CLOSED_REFUSE(true, 300000340,
            "APPLICATION_72H_CANNOT_BE_CLOSED_REFUSE", "申请72H无法结案驳回",
            Arrays.asList(ComplaintChannelEnum.NR_BOX, ComplaintChannelEnum.NR_MI_PUSH)),
    APPLICATION_FOR_WAIVER_REFUSE(true, 300000339, "APPLICATION_FOR_WAIVER_REFUSE", "申请免责驳回",
            Arrays.asList(ComplaintChannelEnum.NR_BOX, ComplaintChannelEnum.NR_MI_PUSH)),
    REASSIGNMENT_STORE_AUDIT(true, 300000327, "REASSIGNMENT_STORE_AUDIT", "改派门店审批",
            Collections.singletonList(ComplaintChannelEnum.MI_OFFICE_EMAIL)),
    APPLICATION_FOR_WAIVER_AUDIT(true, 300000328, "APPLICATION_FOR_WAIVER_AUDIT", "门店免责审批",
            Collections.singletonList(ComplaintChannelEnum.MI_OFFICE_EMAIL)),
    APPLICATION_72H_CANNOT_BE_CLOSED_AUDIT(true, 300000329,
            "APPLICATION_72H_CANNOT_BE_CLOSED_REFUSE", "72H无法结案审批",
            Collections.singletonList(ComplaintChannelEnum.MI_OFFICE_EMAIL)),
    APPLICATION_FOR_CLOSURE_AUDIT(true, 300000331, "APPLICATION_FOR_CLOSURE_AUDIT", "门店投诉单申请结�?,
            Collections.singletonList(ComplaintChannelEnum.MI_OFFICE_EMAIL)),

    PRODUCT_RISK_CLOSURE_APPLICATION_AUDIT(true, 300000978, "PRODUCT_RISK_CLOSURE_APPLICATION_AUDIT", "产品风险-申请结案",
            Collections.singletonList(ComplaintChannelEnum.MI_OFFICE_EMAIL)),
    NEW_REPORT_TO_DEAL_ONLY_MI_PUSH(true, 300000720,
            "NEW_REPORT_TO_DEAL_ONLY_MI_PUSH", "新举报单待处�?飞书消息发�?,
            Collections.singletonList(ComplaintChannelEnum.MI_OFFICE_EMAIL)),
    NEW_REPORT_REMIND_ONLY_MI_PUSH(true, 300000721,
            "REMIND_ONLY_MI_PUSH", "投诉单被催单,飞书消息发�?,
            Collections.singletonList(ComplaintChannelEnum.MI_OFFICE_EMAIL)),
    NOTIFY_CUSTOMER_SERVICET_MI_PUSH(true, 300000900, "NOTIFY_CUSTOMER_SERVICET", "举报处理单已完成",
            Arrays.asList(ComplaintChannelEnum.NR_BOX,
                    ComplaintChannelEnum.MI_OFFICE_EMAIL)),

    DELIVER_NEW_COMPLAINT(true, 300001005, "DELIVER_NEW_COMPLAINT", "交付新投诉单待处�?,
            Arrays.asList(ComplaintChannelEnum.MI_OFFICE_EMAIL)),
    DELIVER_FIRST_RESPONSE_TO_TIMEOUT(true, 300001004, "DELIVER_FIRST_RESPONSE_TO_TIMEOUT", "交付首响即将超时",
            Arrays.asList(ComplaintChannelEnum.MI_OFFICE_EMAIL)),
    DELIVER_FINISH_TO_TIMEOUT(true, 300000906, "DELIVER_FINISH_TO_TIMEOUT", "交付结案即将超时",
            Arrays.asList(ComplaintChannelEnum.MI_OFFICE_EMAIL)),
    DELIVER_REMIND(true, 300001006, "DELIVER_REMIND", "交付被催�?,
            Arrays.asList(ComplaintChannelEnum.MI_OFFICE_EMAIL)),
    DELIVER_REASSIGNMENT(true, 300001007, "DELIVER_REASSIGNMENT", "交付改派",
            Arrays.asList(ComplaintChannelEnum.MI_OFFICE_EMAIL)),

    DELIVER_FINISH_TO_CUSTOMER_SERVICE(true, 300001009, "DELIVER_FINISH_TO_CUSTOMER_SERVICE",
            "交付客诉单关闭通知客服",
            Arrays.asList(ComplaintChannelEnum.NR_BOX, ComplaintChannelEnum.MI_OFFICE_EMAIL)),

    RETAIL_FINISH_TO_CUSTOMER_SERVICE(true, 300000907, "RETAIL_FINISH_TO_CUSTOMER_SERVICE",
            "零售客诉单关闭通知客服",
            Arrays.asList(ComplaintChannelEnum.NR_BOX, ComplaintChannelEnum.MI_OFFICE_EMAIL)),

    MEDIA_INVOLVED_STORE_AUDIT(true, 300000977, "MEDIA_INVOLVED_STORE_AUDIT", "涉媒投诉门店相关人员推�?,
            Arrays.asList(ComplaintChannelEnum.VOICE, ComplaintChannelEnum.NR_BOX, ComplaintChannelEnum.NR_MI_PUSH)),

    MEDIA_INVOLVED_ZONE_AUDIT(true, 300000974, "MEDIA_INVOLVED_ZONE_AUDIT", "涉媒投诉区域相关人员推送（飞书机器人）",
            Arrays.asList(ComplaintChannelEnum.MI_OFFICE_EMAIL)),

    PRODUCT_RISK_UPGRADE_STORE_AUDIT(true, 300001062, "PRODUCT_RISK_UPGRADE_STORE_AUDIT", "产品风险升级投诉门店相关人员推�?,
            Arrays.asList(ComplaintChannelEnum.NR_BOX, ComplaintChannelEnum.NR_MI_PUSH)),

    PRODUCT_RISK_UPGRADE_ZONE_AUDIT(true, 300001060, "PRODUCT_RISK_UPGRADE_ZONE_AUDIT", "产品风险升级投诉区域相关人员推送（飞书机器人）",
            Arrays.asList(ComplaintChannelEnum.MI_OFFICE_EMAIL)),

    UN_FINISHED_TO_TIMEOUT(true, 300000975,
            "UN_FINISHED_TO_TIMEOUT", "�?5天未结案",
            Collections.singletonList(ComplaintChannelEnum.MI_OFFICE_EMAIL)),

    JUDGE_RESPONSIBILITY_AUDIT(true, 300001064,
            "JUDGE_RESPONSIBILITY_AUDIT", "投诉待判责审批任�?飞书消息发�?,
            Collections.singletonList(ComplaintChannelEnum.MI_OFFICE_EMAIL)),

    STORE_RESPONSIBLE_AUDIT(true, 300001062,
            "STORE_RESPONSIBLE_AUDIT", "门店有责,零售通push+站内信消息发�?,
            Arrays.asList(ComplaintChannelEnum.NR_BOX, ComplaintChannelEnum.NR_MI_PUSH)),

    STORE_RESPONSIBLE_AUDIT_FEI_SHU(true, 300001065, "STORE_RESPONSIBLE_AUDIT_FEI_SHU", "门店有责,飞书消息发�?,
                                    Arrays.asList(ComplaintChannelEnum.MI_OFFICE_EMAIL)),

    STORE_REPORT_CLOSURE(true, 300001062,
            "STORE_REPORT_CLOSURE", "门店报备投诉单结案完�?零售通push+站内信消息发�?,
            Arrays.asList(ComplaintChannelEnum.NR_BOX, ComplaintChannelEnum.NR_MI_PUSH)),

    STORE_REPORT_CLOSURE_FEI_SHU(true, 300001066, "STORE_REPORT_CLOSURE_FEI_SHU", "门店报备投诉单结案完�?飞书消息发�?,
                                     Arrays.asList(ComplaintChannelEnum.MI_OFFICE_EMAIL)),

    SUBMIT_REVIEW_CLOSURE(true, 300001067,
            "SUBMIT_REVIEW_CLOSURE", "服务投诉复盘完成�?飞书消息发�?,
            Collections.singletonList(ComplaintChannelEnum.MI_OFFICE_EMAIL)),

    NEW_CONSULT_TO_DEAL_ONLY(true, 300000980,
            "NEW_CONSULT_TO_DEAL_ONLY", "新咨询单待处�?,
            Collections.singletonList(ComplaintChannelEnum.NR_BOX)),

    NEW_CONSULT_REMIND_ONLY(true, 300000979,
            "NEW_CONSULT_REMIND_ONLY", "咨询单催�?,
            Collections.singletonList(ComplaintChannelEnum.NR_BOX)),

    CONSULT_REASSIGN_ONLY(true, 300000981,
            "CONSULT_REASSIGN_ONLY", "咨询单改�?,
            Collections.singletonList(ComplaintChannelEnum.NR_BOX)),

    ;
    /**
     * 开�?
     */
    private final boolean open;
    /**
     * 场景ID
     */
    private final Integer sceneId;
    /**
     * 场景
     */
    private final String sceneType;
    /**
     * 描述
     */
    private final String desc;
    /**
     * 通知方式,参�?com.xiaomi.nr.messagehub.sdk.dto.enums.ChannelEnum
     */
    private final List<ComplaintChannelEnum> complaintChannelEnumList;

}
