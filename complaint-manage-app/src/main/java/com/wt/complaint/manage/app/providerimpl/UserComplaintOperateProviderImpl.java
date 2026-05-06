package com.wt.complaint.manage.app.providerimpl;

import com.wt.commons.utils.StringUtils;
import com.wt.complaint.manage.api.model.enums.UcOrderTypeEnum;
import com.wt.complaint.manage.api.model.req.FollowRecordReq;
import com.wt.complaint.manage.api.model.req.operate.CreateOrderReq;
import com.wt.complaint.manage.api.model.req.operate.JudgeOrderReq;
import com.wt.complaint.manage.api.model.req.operate.PickUpOrderReq;
import com.wt.complaint.manage.api.model.req.operate.RemindOrderReq;
import com.wt.complaint.manage.api.model.resp.operate.AddFollowRecordResp;
import com.wt.complaint.manage.api.model.resp.operate.CreateOrderResp;
import com.wt.complaint.manage.api.model.resp.operate.JudgeOrderResp;
import com.wt.complaint.manage.api.model.resp.operate.PickUpOrderResp;
import com.wt.complaint.manage.api.model.resp.operate.RemindOrderResp;
import com.wt.complaint.manage.api.provider.UserComplaintOperateProvider;
import com.wt.complaint.manage.app.aspect.ExceptionHandle;
import com.wt.complaint.manage.app.convert.ComplaintOperateConvert;
import com.wt.complaint.manage.app.convert.UserComplaintOperateConvert;
import com.wt.complaint.manage.domain.api.service.interfaces.ComplaintOperateService;
import com.wt.complaint.manage.domain.api.service.interfaces.RetailComplaintOperateService;
import com.wt.complaint.manage.domain.api.service.interfaces.UserComplaintOperateService;
import com.wt.complaint.manage.domain.api.service.interfaces.CustomeUserContext;
import com.wt.complaint.manage.domain.api.service.interfaces.UserConsultOperateService;
import com.wt.complaint.manage.domain.api.service.parameter.in.OrderAddFollowUpRecordSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.OrderPickUpSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.OrderRemindSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.opetate.CreateOrderSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.opetate.JudgeOrderSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailRemindOrderSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.OrderFollowUpRecordSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.OrderPickUpSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.OrderRemindSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.operate.CreateOrderSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.operate.JudgeOrderSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.RemindOrderSoOut;
import com.wt.complaint.manage.domain.model.UserInfo;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDoc;
import com.xiaomi.mone.docs.annotations.dubbo.ApiModule;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.rpc.RpcContext;
import sun.swing.StringUIClientPropertyKey;

import javax.annotation.Resource;

/**
 * @author linjiehong
 * @date 2025/5/19 11:14
 */
@Slf4j
@DubboService(timeout = 3000, group = "${dubbo.group}", version = "1.0")
@ApiModule(value = "客诉下作业单操作相关接口", apiInterface = UserComplaintOperateProviderImpl.class)
@SuppressWarnings("all")
public class UserComplaintOperateProviderImpl implements UserComplaintOperateProvider {
    @Resource
    private UserComplaintOperateService userComplaintOperateService;

    @Resource
    private ComplaintOperateService complaintOperateService;

    @Resource
    private RetailComplaintOperateService retailCarComplaintOperateService;

    @Resource
    private CustomeUserContext customeUserContext;

    @Resource
    private UserConsultOperateService userConsultOperateService;

    @Override
    @ExceptionHandle
    @ApiDoc(value = "新建举报�?, name = "新建举报�?, description = "dubbo接口,soc调用")
    public Result<CreateOrderResp> createOrder(CreateOrderReq req) {
        CreateOrderSoIn soIn = UserComplaintOperateConvert.INSTANCE.toSoIn(req);
        CreateOrderSoOut order = userComplaintOperateService.createOrder(soIn);
        CreateOrderResp resp = new CreateOrderResp();
        resp.setWorkNo(order.getUcNo());
        return Result.success(resp);
    }

    @Override
    @ExceptionHandle
    @ApiDoc(value = "举报单接�?, name = "举报单接�?, description = "售后工作台调用：/mtop/proretailcar/complaint/common/receiveOrder")
    public Result<PickUpOrderResp> pickUpOrder(PickUpOrderReq req) {
        OrderPickUpSoIn soIn = UserComplaintOperateConvert.INSTANCE.toSoIn(req);
        UserInfo userInfo = customeUserContext.fromRpcContextForAftersaleWorkbench();
        soIn.setPickUpMid(String.valueOf(userInfo.getMiID()));
        soIn.setLoginRole(userInfo.getRoleList());
        OrderPickUpSoOut orderPickUpSoOut = userComplaintOperateService.pickUpOrder(soIn);
        return Result.success(ComplaintOperateConvert.INSTANCE.toResp(orderPickUpSoOut));
    }

    @Override
    @ExceptionHandle
    @ApiDoc(value = "添加跟进记录", name = "添加跟进记录", description = "售后工作台调用：/mtop/proretailcar/complaint/common/addFollowRecord")
    public Result<AddFollowRecordResp> addFollowRecord(FollowRecordReq req) {
        // 获取当前登录人信�?
        UserInfo userInfo = customeUserContext.fromRpcContextForAftersaleWorkbench();
        OrderAddFollowUpRecordSoIn soIn = UserComplaintOperateConvert.INSTANCE.toSoIn(req);
        soIn.setFollowUpMid(String.valueOf(userInfo.getMiID()));
        soIn.setLoginRole(userInfo.getCurrRole());
        log.info("ComplaintOperateProviderImpl.remindOrder soIn:{}", GsonUtil.toJson(soIn));
        // 获取单号
        String order = req.getUcNo() == null ? req.getComplaintNo() : req.getUcNo();
        UcOrderTypeEnum ucOrderTypeEnum = UcOrderTypeEnum.getByUcNo(order);
        OrderFollowUpRecordSoOut followUpRecordSoOut;
        // 投诉单兼�?
        if (ucOrderTypeEnum == UcOrderTypeEnum.COMPLAINT_ORDER) {
            soIn.setComplaintNo(req.getUcNo());
            followUpRecordSoOut = complaintOperateService.addFollowUpRecords(soIn);
        } else {
            followUpRecordSoOut = userComplaintOperateService.addFollowUpRecords(soIn);
        }
        return Result.success(UserComplaintOperateConvert.INSTANCE.toResp(followUpRecordSoOut));
    }

    @Override
    @ExceptionHandle
    @ApiDoc(value = "催单", name = "催单", description = "客服工作台调用：/mtop/car_cs/complaint/common/remindOrder")
    public Result<RemindOrderResp> remindOrder(RemindOrderReq req) {
        // 获取当前登录人信�?
        OrderRemindSoIn soIn = UserComplaintOperateConvert.INSTANCE.toSoIn(req);
        String miID = RpcContext.getContext().getAttachment("$upc_miID");
        soIn.setReminderMid(miID);
        log.info("ComplaintOperateProviderImpl.remindOrder soIn:{}", GsonUtil.toJson(soIn));
        // 获取单号
        String order = req.getUcNo() == null ? req.getComplaintNo() : req.getUcNo();
        //兼容咨询�?
        if(StringUtils.isNotBlank(req.getConsultNo())){
            order = req.getConsultNo();
        }
        UcOrderTypeEnum ucOrderTypeEnum = UcOrderTypeEnum.getByUcNo(order);
        OrderRemindSoOut orderRemindSoOut;
        // 投诉单兼�?
        if (ucOrderTypeEnum == UcOrderTypeEnum.COMPLAINT_ORDER) {
            soIn.setComplaintNo(order);
            orderRemindSoOut = complaintOperateService.remindOrder(soIn);
            // 兼容交付或投诉单
        } else if (ucOrderTypeEnum == UcOrderTypeEnum.DELIVER_COMPLAINT_ORDER ||
                ucOrderTypeEnum == UcOrderTypeEnum.RETAIL_COMPLAINT_ORDER) {
            RemindOrderSoOut remindOrderSoOut =
                    retailCarComplaintOperateService.remindOrder(RetailRemindOrderSoIn.builder()
                            .drNo(soIn.getUcNo())
                            .reminderMid(soIn.getReminderMid())
                            .reminderName(soIn.getReminderName())
                            .orderRemindInfo(soIn.getOrderRemindInfo()).build());
            return Result.success(RemindOrderResp.builder().result(remindOrderSoOut.getResult()).build());
        } //兼容咨询�?
        else if (ucOrderTypeEnum == UcOrderTypeEnum.CONSULT_ORDER) {
        RemindOrderSoOut remindOrderSoOut =
                userConsultOperateService.remindOrder(RetailRemindOrderSoIn.builder()
                        .drNo(soIn.getConsultNo())
                        .reminderMid(soIn.getReminderMid())
                        .reminderName(soIn.getReminderName())
                        .orderRemindInfo(soIn.getOrderRemindInfo()).build());
        return Result.success(RemindOrderResp.builder().result(remindOrderSoOut.getResult()).build());
    }else {
            orderRemindSoOut = userComplaintOperateService.remindOrder(soIn);
        }
        return Result.success(UserComplaintOperateConvert.INSTANCE.toResp(orderRemindSoOut));
    }

    @Override
    @ExceptionHandle
    @ApiDoc(value = "判定", name = "判定", description = "售后工作台调用：/mtop/proretailcar/complaint/common/judgeOrder")
    public Result<JudgeOrderResp> judgeOrder(JudgeOrderReq req) {
        JudgeOrderSoIn soIn = UserComplaintOperateConvert.INSTANCE.toSoIn(req);
        UserInfo userInfo = customeUserContext.fromRpcContextForAftersaleWorkbench();
        soIn.setUserMid(String.valueOf(userInfo.getMiID()));
        soIn.setLoginRole(userInfo.getRoleList());
        JudgeOrderSoOut judgeOrderSoOut = userComplaintOperateService.judgeOrder(soIn);
        return Result.success(UserComplaintOperateConvert.INSTANCE.toResp(judgeOrderSoOut));
    }
}
