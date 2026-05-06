package com.wt.complaint.manage.domain.manager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.wt.commons.utils.JacksonUtil;
import com.wt.complaint.manage.api.model.enums.AuditStatusEnum;
import com.wt.complaint.manage.api.model.enums.AuditTypeEnum;
import com.wt.complaint.manage.api.model.enums.ComplaintStatusEnum;
import com.wt.complaint.manage.api.model.enums.CreateSourceEnum;
import com.wt.complaint.manage.domain.api.enums.ComplaintTypeEnum;
import com.wt.complaint.manage.api.model.enums.ConsultStatusEnum;
import com.wt.complaint.manage.api.model.enums.ResponsibilityEnum;
import com.wt.complaint.manage.api.model.enums.ReviewedEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintAuditGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.ComplaintAuditListSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintAuditListSoOut;
import com.wt.complaint.manage.domain.constant.ComplaintActionConst;
import com.wt.complaint.manage.domain.constant.ComplaintInfoConstant;
import com.wt.complaint.manage.domain.constant.MrRoleConstant;
import com.wt.complaint.manage.domain.model.UserConsultOrderInfo;
import com.wt.proretail.newcommon.account.ProretailRoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@SuppressWarnings("all")
public class UserAuthManager {

    @Resource
    private ComplaintAuditGateway auditGateway;

    private static final Map<String, Map<Integer, List<UserAction>>> DETAIL_BASE_AUTH;

    private static final Map<String, Map<Integer, List<UserAction>>> CONSULT_BASE_AUTH;


    static {
        // 初始�?CONSULT_BASE_AUTH
        Map<String, Map<Integer, List<UserAction>>> detailBaseAuth = new HashMap<>();
        // 店长权限
        Map<Integer, List<UserAction>> carOrgManagerActions = new HashMap<>();
        carOrgManagerActions.put(ConsultStatusEnum.PENDING_ORDER.getCode(), Arrays.asList(
                // 派单
                UserAction.create(ComplaintActionConst.DISPATCH),
                // 申请改派门店
                UserAction.create(ComplaintActionConst.APPLY_REASSIGN_STORE, "applyOrgChange")
        ));
        carOrgManagerActions.put(ConsultStatusEnum.FIRST_RESPONSE_PENDING.getCode(), Collections.unmodifiableList(Arrays.asList(
                // 改派处理�?
                UserAction.create(ComplaintActionConst.REASSIGN_HANDLER),
                // 添加跟进记录
                UserAction.create(ComplaintActionConst.ADD_FOLLOW_UP_RECORDS),
                // 预约到店维保
                UserAction.create(ComplaintActionConst.APPOINTMENT_MR_ORDER)
        )));
        carOrgManagerActions.put(ConsultStatusEnum.FINISH_PENDING.getCode(), Collections.unmodifiableList(Arrays.asList(
                // 改派处理�?
                UserAction.create(ComplaintActionConst.REASSIGN_HANDLER),
                // 添加跟进记录
                UserAction.create(ComplaintActionConst.ADD_FOLLOW_UP_RECORDS),
                // 预约到店维保
                UserAction.create(ComplaintActionConst.APPOINTMENT_MR_ORDER),
                // 申请结案
                UserAction.create(ComplaintActionConst.APPLY_FINISH, "applyFinish")
        )));
        detailBaseAuth.put(MrRoleConstant.CAR_ORG_MANAGER, Collections.unmodifiableMap(carOrgManagerActions));

        // 服务代表
        Map<Integer, List<UserAction>> serviceRepresentativeActions = new HashMap<>();
        serviceRepresentativeActions.put(ConsultStatusEnum.PENDING_ORDER.getCode(), Arrays.asList(
                // 接单
                UserAction.create(ComplaintActionConst.PICK_UP),
                // 申请改派门店
                UserAction.create(ComplaintActionConst.APPLY_REASSIGN_STORE, "applyOrgChange")
        ));
        detailBaseAuth.put(MrRoleConstant.RECEIVER, serviceRepresentativeActions);


        // 服务顾问主管
        Map<Integer, List<UserAction>> serviceManagerRepresentativeActions = new HashMap<>();
        serviceManagerRepresentativeActions.put(ConsultStatusEnum.PENDING_ORDER.getCode(), Arrays.asList(
                // 接单
                UserAction.create(ComplaintActionConst.PICK_UP),
                // 派单
                UserAction.create(ComplaintActionConst.DISPATCH),
                // 申请改派门店
                UserAction.create(ComplaintActionConst.APPLY_REASSIGN_STORE, "applyOrgChange")
        ));
        serviceManagerRepresentativeActions.put(ConsultStatusEnum.FIRST_RESPONSE_PENDING.getCode(), Collections.unmodifiableList(Arrays.asList(
                // 改派处理�?
                UserAction.create(ComplaintActionConst.REASSIGN_HANDLER),
                // 添加跟进记录
                UserAction.create(ComplaintActionConst.ADD_FOLLOW_UP_RECORDS),
                // 预约到店维保
                UserAction.create(ComplaintActionConst.APPOINTMENT_MR_ORDER)
        )));
        serviceManagerRepresentativeActions.put(ConsultStatusEnum.FINISH_PENDING.getCode(), Collections.unmodifiableList(Arrays.asList(
                // 改派处理�?
                UserAction.create(ComplaintActionConst.REASSIGN_HANDLER),
                // 添加跟进记录
                UserAction.create(ComplaintActionConst.ADD_FOLLOW_UP_RECORDS),
                // 预约到店维保
                UserAction.create(ComplaintActionConst.APPOINTMENT_MR_ORDER),
                // 申请结案
                UserAction.create(ComplaintActionConst.APPLY_FINISH, "applyFinish")
        )));
        detailBaseAuth.put(MrRoleConstant.RECEIVER_MANAGER, serviceManagerRepresentativeActions);
        CONSULT_BASE_AUTH = Collections.unmodifiableMap(detailBaseAuth);
    }



    static {
        // 初始�?DETAIL_BASE_AUTH
        Map<String, Map<Integer, List<UserAction>>> detailBaseAuth = new HashMap<>();
        // 店长权限
        Map<Integer, List<UserAction>> carOrgManagerActions = new HashMap<>();
        carOrgManagerActions.put(ComplaintStatusEnum.PENDING_ORDER.getCode(), Arrays.asList(
            // 接单
            UserAction.create(ComplaintActionConst.PICK_UP),
            // 派单
            UserAction.create(ComplaintActionConst.DISPATCH),
            // 申请改派门店
            UserAction.create(ComplaintActionConst.APPLY_REASSIGN_STORE, "applyOrgChange")
        ));
        carOrgManagerActions.put(ComplaintStatusEnum.FIRST_RESPONSE_PENDING.getCode(), Collections.unmodifiableList(Arrays.asList(
            // 改派处理�?
            UserAction.create(ComplaintActionConst.REASSIGN_HANDLER),
            // 添加跟进记录
            UserAction.create(ComplaintActionConst.ADD_FOLLOW_UP_RECORDS),
            // 预约到店维保
            UserAction.create(ComplaintActionConst.APPOINTMENT_MR_ORDER),
            // 积分发放
            UserAction.create(ComplaintActionConst.ISSUE_POINTS),
            // 申请免责
            UserAction.create(ComplaintActionConst.APPLY_EXEMPTION, "applyNoDuty")
        )));
        carOrgManagerActions.put(ComplaintStatusEnum.APPLY_FINISH_PENDING.getCode(), Collections.unmodifiableList(Arrays.asList(
            // 改派处理�?
            UserAction.create(ComplaintActionConst.REASSIGN_HANDLER),
            // 添加跟进记录
            UserAction.create(ComplaintActionConst.ADD_FOLLOW_UP_RECORDS),
            // 申请结案
            UserAction.create(ComplaintActionConst.APPLY_FINISH, "applyFinish"),
            // 申请72H无法结案
            UserAction.create(ComplaintActionConst.APPLY_72H_UNFINISHED, "apply72NoFinish"),
            // 预约到店维保
            UserAction.create(ComplaintActionConst.APPOINTMENT_MR_ORDER),
            // 积分发放
            UserAction.create(ComplaintActionConst.ISSUE_POINTS),
            // 申请免责
            UserAction.create(ComplaintActionConst.APPLY_EXEMPTION, "applyNoDuty")
        )));
        carOrgManagerActions.put(ComplaintStatusEnum.FINISH_EVALUATION_PENDING.getCode(), Collections.unmodifiableList(Arrays.asList(
            // 改派处理�?
            UserAction.create(ComplaintActionConst.REASSIGN_HANDLER),
            // 添加跟进记录
            UserAction.create(ComplaintActionConst.ADD_FOLLOW_UP_RECORDS),
            // 申请免责
            UserAction.create(ComplaintActionConst.APPLY_EXEMPTION, "applyNoDuty")
        )));
        carOrgManagerActions.put(ComplaintStatusEnum.FINISH_COMPLETE.getCode(), Collections.unmodifiableList(Arrays.asList(
            // 申请免责
            UserAction.create(ComplaintActionConst.APPLY_EXEMPTION, "applyNoDuty")
        )));
        detailBaseAuth.put(MrRoleConstant.CAR_ORG_MANAGER, Collections.unmodifiableMap(carOrgManagerActions));

        // 品牌派驻代表权限
        Map<Integer, List<UserAction>> brandDispatchActions = new HashMap<>();
        brandDispatchActions.put(ComplaintStatusEnum.PENDING_ORDER.getCode(), Arrays.asList(
            // 接单
            UserAction.create(ComplaintActionConst.PICK_UP),
            // 派单
            UserAction.create(ComplaintActionConst.DISPATCH),
            // 申请改派门店
            UserAction.create(ComplaintActionConst.APPLY_REASSIGN_STORE, "applyOrgChange")
        ));
        brandDispatchActions.put(ComplaintStatusEnum.FIRST_RESPONSE_PENDING.getCode(), Collections.unmodifiableList(Arrays.asList(
            // 改派处理�?
            UserAction.create(ComplaintActionConst.REASSIGN_HANDLER),
            // 添加跟进记录
            UserAction.create(ComplaintActionConst.ADD_FOLLOW_UP_RECORDS),
            // 预约到店维保
            UserAction.create(ComplaintActionConst.APPOINTMENT_MR_ORDER),
            // 积分发放
            UserAction.create(ComplaintActionConst.ISSUE_POINTS),
            // 申请免责
            UserAction.create(ComplaintActionConst.APPLY_EXEMPTION, "applyNoDuty")
        )));
        brandDispatchActions.put(ComplaintStatusEnum.APPLY_FINISH_PENDING.getCode(), Collections.unmodifiableList(Arrays.asList(
            // 改派处理�?
            UserAction.create(ComplaintActionConst.REASSIGN_HANDLER),
            // 添加跟进记录
            UserAction.create(ComplaintActionConst.ADD_FOLLOW_UP_RECORDS),
            // 申请结案
            UserAction.create(ComplaintActionConst.APPLY_FINISH, "applyFinish"),
            // 申请72H无法结案
            UserAction.create(ComplaintActionConst.APPLY_72H_UNFINISHED, "apply72NoFinish"),
            // 预约到店维保
            UserAction.create(ComplaintActionConst.APPOINTMENT_MR_ORDER),
            // 积分发放
            UserAction.create(ComplaintActionConst.ISSUE_POINTS),
            // 申请免责
            UserAction.create(ComplaintActionConst.APPLY_EXEMPTION, "applyNoDuty")
        )));
        brandDispatchActions.put(ComplaintStatusEnum.FINISH_EVALUATION_PENDING.getCode(), Collections.unmodifiableList(Arrays.asList(
            // 改派处理�?
            UserAction.create(ComplaintActionConst.REASSIGN_HANDLER),
            // 添加跟进记录
            UserAction.create(ComplaintActionConst.ADD_FOLLOW_UP_RECORDS),
            // 申请免责
            UserAction.create(ComplaintActionConst.APPLY_EXEMPTION, "applyNoDuty")
        )));
        brandDispatchActions.put(ComplaintStatusEnum.FINISH_COMPLETE.getCode(), Collections.unmodifiableList(Arrays.asList(
            // 申请免责
            UserAction.create(ComplaintActionConst.APPLY_EXEMPTION, "applyNoDuty")
        )));
        detailBaseAuth.put(MrRoleConstant.CAR_BRAND_REPRESENTATIVE, Collections.unmodifiableMap(carOrgManagerActions));

        // 服务代表
        Map<Integer, List<UserAction>> serviceRepresentativeActions = new HashMap<>();
        serviceRepresentativeActions.put(ComplaintStatusEnum.PENDING_ORDER.getCode(), Arrays.asList(
            // 接单
            UserAction.create(ComplaintActionConst.PICK_UP),
            // 申请改派门店
            UserAction.create(ComplaintActionConst.APPLY_REASSIGN_STORE, "applyOrgChange")
        ));
        serviceRepresentativeActions.put(ComplaintStatusEnum.FIRST_RESPONSE_PENDING.getCode(), Collections.unmodifiableList(Arrays.asList(
            // 添加跟进记录
            UserAction.create(ComplaintActionConst.ADD_FOLLOW_UP_RECORDS, "checkUserRoleAndLoginStatus"),
            // 预约到店维保
            UserAction.create(ComplaintActionConst.APPOINTMENT_MR_ORDER, "checkUserRoleAndLoginStatus"),
            // 申请免责
            UserAction.create(ComplaintActionConst.APPLY_EXEMPTION, "applyNoDuty")
        )));
        serviceRepresentativeActions.put(ComplaintStatusEnum.APPLY_FINISH_PENDING.getCode(), Collections.unmodifiableList(Arrays.asList(
            // 添加跟进记录
            UserAction.create(ComplaintActionConst.ADD_FOLLOW_UP_RECORDS, "checkUserRoleAndLoginStatus"),
            // 申请结案
            UserAction.create(ComplaintActionConst.APPLY_FINISH, "applyFinish"),
            // 申请72H无法结案
            UserAction.create(ComplaintActionConst.APPLY_72H_UNFINISHED, "apply72NoFinish"),
            // 预约到店维保
            UserAction.create(ComplaintActionConst.APPOINTMENT_MR_ORDER, "checkUserRoleAndLoginStatus"),
            // 申请免责
            UserAction.create(ComplaintActionConst.APPLY_EXEMPTION, "applyNoDuty")
        )));
        serviceRepresentativeActions.put(ComplaintStatusEnum.FINISH_EVALUATION_PENDING.getCode(), Collections.unmodifiableList(Arrays.asList(
            // 添加跟进记录
            UserAction.create(ComplaintActionConst.ADD_FOLLOW_UP_RECORDS, "checkUserRoleAndLoginStatus"),
            // 申请免责
            UserAction.create(ComplaintActionConst.APPLY_EXEMPTION, "applyNoDuty")
        )));
        serviceRepresentativeActions.put(ComplaintStatusEnum.FINISH_COMPLETE.getCode(), Collections.unmodifiableList(Arrays.asList(
            // 申请免责
            UserAction.create(ComplaintActionConst.APPLY_EXEMPTION, "applyNoDuty")
        )));
        detailBaseAuth.put(MrRoleConstant.RECEIVER, serviceRepresentativeActions);
        DETAIL_BASE_AUTH = Collections.unmodifiableMap(detailBaseAuth);
    }

    /**
     * 对外接口
     */
    public List<String> getDetailActionAuth(String role, ComplaintOrderInfoGoIn goIn, Long mid) {
        // 列表�?获取列表页权限树
        UserActionAuthContext context = new UserActionAuthContext();
        context.setOrgId(goIn.getOrgId());
        context.setComplaintNo(goIn.getComplaintNo());
        context.setHandlerMid(goIn.getOperatorMid());
        context.setResponsibility(goIn.getResponsibility());
        context.setExemptionApplyTimes(goIn.getExemptionApplyTimes());
        context.setRole(role);
        context.setLoginMid(mid);
        context.setComplaintType(goIn.getComplaintType());
        Map<Integer, List<UserAction>> statusActionList = DETAIL_BASE_AUTH.getOrDefault(role, new HashMap<>());
        List<UserAction> actions = statusActionList.getOrDefault(goIn.getStatus(), new ArrayList<>());
        List<String> userActions = actions.stream().filter(x -> businessCheck(x, context)).map(UserAction::getActionKey)
            .collect(Collectors.toList());
        return userActions;
    }

    public List<String> getDetailActionAuth(String role, UserConsultOrderInfo goIn, Long mid) {
        // 列表�?获取列表页权限树
/*        UserActionAuthContext context = new UserActionAuthContext();
        context.setOrgId(goIn.getOrgId());
        context.setHandlerMid(goIn.getOperatorMid());
        context.setRole(role);
        context.setLoginMid(mid);*/
        Map<Integer, List<UserAction>> statusActionList = CONSULT_BASE_AUTH.getOrDefault(role, new HashMap<>());
        List<UserAction> actions = statusActionList.getOrDefault(goIn.getOrderStatus(), new ArrayList<>());
        return actions.stream().map(UserAction::getActionKey)
                .collect(Collectors.toList());
    }

    /**
     * 特殊业务逻辑检�?businessCheck
     */
    private boolean businessCheck(UserAction action, UserActionAuthContext context) {
        log.info("UserAuthManager|businessCheck context:{},action:{}", JacksonUtil.toStr(context), JacksonUtil.toStr(action));
        try {
            if (StringUtils.isNotEmpty(action.getFunc())) {
                log.info("invoke func:{}", action.getFunc());
                Method func = UserAuthManager.class.getMethod(action.getFunc(), context.getClass());
                return (Boolean) func.invoke(this, context);
            }
            return true;
        } catch (Exception ex) {
            log.error("UserAuthManager|err,action:{},error:", JacksonUtil.toStr(action), ex);
        }
        return false;
    }


    /**
     * 检查是否有进行中的免责申请�?
     *
     * @param context
     * @param auditType
     * @return
     */
    private Boolean hasOngoingAudit(UserActionAuthContext context, AuditTypeEnum auditType) {
        ComplaintAuditListSoIn auditListParam = ComplaintAuditListSoIn.builder()
            .complaintNo(context.getComplaintNo())
            .auditStatusList(AuditStatusEnum.getNoApplyCodes())
            .auditTypeList(Arrays.asList(auditType.getCode()))
            .pageNum(1)
            .pageSize(100)
            .build();
        ComplaintAuditListSoOut auditInfos = auditGateway.searchComplaintAuditList(auditListParam);
        return Objects.nonNull(auditInfos) && auditInfos.getTotal() > 0;
    }

    /**
     * 检查用户角色和登录状�?
     *
     * @param context
     * @return
     */
    public Boolean checkUserRoleAndLoginStatus(UserActionAuthContext context) {
        if (ProretailRoleEnum.CAR_SERVICE_REPRESENTATIVE.getKey().equals(context.getRole())) {
            return Objects.nonNull(context.getLoginMid()) && Objects.nonNull(context.getHandlerMid()) && context.getHandlerMid().equals(context.getLoginMid());
        }
        return true;
    }

    /**
     * 免责申请按钮判断
     * 若客诉单门店无责：则不展示门店免责按�?
     * 若门店有责：若已有进行中的免责申请单，则不展示门店免责按�?
     *
     * 已废弃，参考V2版本，使用applyNoDutyV2方法
     */
    @Deprecated
    public Boolean applyNoDuty(UserActionAuthContext context) {
        Integer responsibility = context.getResponsibility();
        if (Objects.equals(responsibility, ResponsibilityEnum.NO.getCode())) {
            return false;
        }
        if (!checkUserRoleAndLoginStatus(context)) {
            return false;
        }
        return !hasOngoingAudit(context, AuditTypeEnum.APPLICATION_FOR_WAIVER);
    }

    /**
     * 改派门店按钮控制
     * 1. 服务门店不允许改�?
     * 2. 如果存在进行中的审批单，不允许改�?
     *
     * @param context
     * @return
     */
    public Boolean applyOrgChange(UserActionAuthContext context) {
        if (CreateSourceEnum.STORE.getCode().equals(context.getCreateSource())) {
            return false;
        }
        return !hasOngoingAudit(context, AuditTypeEnum.REASSIGNMENT_STORES);
    }

    /**
     * 72小时无责按钮控制
     *
     * @param context
     * @return
     */
    public Boolean apply72NoFinish(UserActionAuthContext context) {
        if (!checkUserRoleAndLoginStatus(context)) {
            return false;
        }
        return !hasOngoingAudit(context, AuditTypeEnum.APPLICATION_72H_CANNOT_BE_CLOSED);
    }

    /**
     * 申请结案按钮控制
     *
     * @param context
     * @return
     */
    public Boolean applyFinish(UserActionAuthContext context) {
        if (!checkUserRoleAndLoginStatus(context)) {
            return false;
        }
        return !hasOngoingAudit(context, AuditTypeEnum.APPLICATION_FOR_CLOSURE);
    }

    /**
     * 角色大区是否和单据大区一�?
     */
    public boolean ifSameBigZone(UserActionAuthContext context) {
        if (ObjectUtil.isNull(context.getBigZoneId()) || CollUtil.isEmpty(context.getRoleAreaId())) {
            log.error("UserAuthManager|ifSameBigZone err,context:{}", JacksonUtil.toStr(context));
            return false;
        }
        return context.getRoleAreaId().contains(context.getBigZoneId());
    }

    /**
     * 角色城市是否和单据城市一�?
     */
    public boolean ifSameCity(UserActionAuthContext context) {
        if (ObjectUtil.isNull(context.getLittleZoneId()) || CollUtil.isEmpty(context.getRoleAreaId())) {
            log.info("UserAuthManager|ifSameCity err,context:{}", JacksonUtil.toStr(context));
            return false;
        }
        return context.getRoleAreaId().contains(context.getLittleZoneId());
    }

    /** --------------- 天工使用的函�?-------------- **/


    /**
     * 复盘按钮展示规则（需同时满足）：
     * 1. 创建来源为「线上客服�?
     * 2. 投诉分类为「服务投诉�?
     * 3. 未提交过复盘
     * 4. 投诉单状态不等于「改派门店待审核�?
     */
    public Boolean applySubmitReview(UserActionAuthContext context) {
        return CreateSourceEnum.ONLINE_CS.getCode().equals(context.getCreateSource())
                && ComplaintTypeEnum.SERVICE_COMPLAINT.getCode().equals(context.getComplaintType())
                && !ReviewedEnum.YES.getCode().equals(context.getReviewed())
                && !ComplaintStatusEnum.ORG_REASSIGN_PENDING.getCode().equals(context.getStatus());
    }

    /**
     * 有责并且没有免责申请
     * -----------------------
     * 免责申请按钮判断
     * 若客诉单门店无责：则不展示门店免责按�?
     * 若门店有责：若已有进行中的免责申请单，则不展示门店免责按�?
     * 如果申请�?次免责，按钮不能展示
     */
    public Boolean applyNoDutyV2(UserActionAuthContext context) {
        Integer responsibility = context.getResponsibility();
        if (Objects.equals(responsibility, ResponsibilityEnum.NO.getCode())) {
            return false;
        }
        if (context.getExemptionApplyTimes() != null
                && context.getExemptionApplyTimes() >= ComplaintInfoConstant.RESPONSIBILITY_EXEMPTION_MAX_APPLY_TIMES) {
            return false;
        }
        return !hasOngoingAudit(context, AuditTypeEnum.APPLICATION_FOR_WAIVER);
    }

    /**
     * 没有申请无法结案
     * -----------------------
     * 72小时无责按钮控制
     */
    public Boolean apply72NoFinishV2(UserActionAuthContext context) {
        return !hasOngoingAudit(context, AuditTypeEnum.APPLICATION_72H_CANNOT_BE_CLOSED);
    }

    /**
     * 没有申请结案
     * -----------------------
     * 申请结案按钮控制
     */
    public Boolean applyFinishV2(UserActionAuthContext context) {
        return !hasOngoingAudit(context, AuditTypeEnum.APPLICATION_FOR_CLOSURE);
    }

}
