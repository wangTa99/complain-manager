package com.wt.complaint.manage.app.providerimpl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.wt.commons.utils.JacksonUtil;
import com.wt.complaint.manage.api.model.enums.TestTagEnum;
import com.wt.complaint.manage.api.model.req.*;
import com.wt.complaint.manage.api.model.req.view.UcOrderInfoBatchReq;
import com.wt.complaint.manage.api.model.req.view.UcOrderLightInfoBatchReq;
import com.wt.complaint.manage.api.model.resp.*;
import com.wt.complaint.manage.api.model.resp.view.UcOrderInfoBatchResp;
import com.wt.complaint.manage.api.model.resp.view.UcOrderLightInfoBatchResp;
import com.wt.complaint.manage.api.provider.UserComplaintViewProvider;
import com.wt.complaint.manage.app.aspect.ExceptionHandle;
import com.wt.complaint.manage.app.convert.UserComplaintViewConvert;
import com.wt.complaint.manage.domain.api.service.interfaces.UserComplaintViewService;
import com.wt.complaint.manage.domain.api.service.interfaces.CustomeUserContext;
import com.wt.complaint.manage.domain.api.service.parameter.in.UcOrderBatchInfoSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.UcOrderBatchLightInfoSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.UserComplaintDetailFrameGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.UserComplaintDetailGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.UserComplaintListSearchGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.UcOrderBatchInfoSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UcOrderBatchLightInfoSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintDetailFrameSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintDetailSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintListSearchSoOut;
import com.wt.complaint.manage.domain.constant.MrRoleConstant;
import com.wt.complaint.manage.domain.model.UserInfo;
import com.wt.complaint.manage.infrastructure.converter.ComplaintOrderConverter;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDoc;
import com.xiaomi.mone.docs.annotations.dubbo.ApiModule;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 客诉类单据视图提供�?
 * @author linjiehong
 */
@Slf4j
@DubboService(timeout = 3000, group = "${dubbo.group}", version = "1.0")
@ApiModule(value = "举报单展示相关接�?, apiInterface = UserComplaintViewProvider.class)
@Validated
public class UserComplaintViewProviderImpl implements UserComplaintViewProvider {

    @Autowired
    private CustomeUserContext customeUserContext;

    @Autowired
    private UserComplaintViewService userComplaintViewService;

    @Override
    @ExceptionHandle
    @ApiDoc(value = "获取单据列表信息", name = "获取单据列表信息", description =
            "获取单据列表信息，售后工作台路径�?mtop/proretailcar" +
                    "/userComplaint/view/searchUserComplaintList")
    public Result<UserComplaintListSearchResp> searchUserComplaintList(UserComplaintListSearchReq request) {
        UserInfo userInfo = customeUserContext.fromRpcContextForAftersaleWorkbench();
        log.info("searchUserComplaintList,userInfo:{}", RetailJsonUtil.toJson(userInfo));
        UserComplaintListSearchGoIn goIn = Convert.convert(UserComplaintListSearchGoIn.class, request);
        List<String> roleList = JacksonUtil.parseArray(userInfo.getRoleList(), String.class);
        goIn.setRoleList(roleList);
        goIn.setMid(userInfo.getMiID());
        goIn.setCurrRole(userInfo.getCurrRole());
        // 获取角色信息
        String userInfoRoleList = userInfo.getRoleList();
        // 仅产研角色可以看MOCK门店数据
        if (StrUtil.isNotEmpty(userInfoRoleList)
                && !userInfoRoleList.contains(MrRoleConstant.PROGRAMMER)) {
            goIn.setTestTag(TestTagEnum.NON_TEST.getCode());
        }
        log.info("searchUserComplaintList goIn={}", JSONUtil.toJsonStr(goIn));
        UserComplaintListSearchSoOut goOut = userComplaintViewService.searchUserComplaintList(goIn);
        UserComplaintListSearchResp resp = Convert.convert(UserComplaintListSearchResp.class, goOut);
        return Result.success(resp);
    }

    @Override
    @ExceptionHandle
    @ApiDoc(value = "获取单据详情框架信息", name = "获取单据详情框架信息", description =
            "获取单据详情框架信息，售后工作台路径�?mtop/proretailcar" +
                    "/userComplaint/view/getUserComplaintFrame")
    public Result<UserComplaintDetailFrameResp> getUserComplaintFrame(UserComplaintDetailFrameReq req) {
        UserComplaintDetailFrameGoIn goIn = Convert.convert(UserComplaintDetailFrameGoIn.class, req);
        UserComplaintDetailFrameSoOut soOut = userComplaintViewService.getUserComplaintFrame(goIn);
        UserComplaintDetailFrameResp resp = ComplaintOrderConverter.INSTANCE.convertToResp(soOut);
        return Result.success(resp);
    }

    @Override
    @ExceptionHandle
    @ApiDoc(value = "单据信息tab接口", name = "单据信息tab接口", description =
            "单据信息tab接口，售后工作台路径�?mtop/proretailcar" +
                    "/userComplaint/view/getUserComplaintDetail")
    public Result<UserComplaintDetailResp> getUserComplaintDetail(UserComplaintDetailReq req) {
        UserComplaintDetailGoIn goIn = Convert.convert(UserComplaintDetailGoIn.class, req);
        UserComplaintDetailSoOut soOut = userComplaintViewService.getUserComplaintDetail(goIn);
        UserComplaintDetailResp resp = Convert.convert(UserComplaintDetailResp.class, soOut);
        return Result.success(resp);
    }

    @Override
    @ExceptionHandle
    @ApiDoc(value = "单据信息查询", name = "单据信息查询", description = "soc调用：获取举报单判定结果")
    public Result<UcOrderInfoBatchResp> getUcOrderInfo(UcOrderInfoBatchReq req) {
        UcOrderBatchInfoSoIn soIn = Convert.convert(UcOrderBatchInfoSoIn.class, req);
        UcOrderBatchInfoSoOut userOrderInfo = userComplaintViewService.getUcOrderInfo(soIn);
        return Result.success(UserComplaintViewConvert.INSTANCE.toUcOrderInfoBatchResp(userOrderInfo));
    }

    @Override
    @ExceptionHandle
    @ApiDoc(value = "单据信息查询（轻量级�?, name = "单据信息查询（轻量级�?, description = "soc调用：获取举报单判定结果")
    public Result<UcOrderLightInfoBatchResp> getUcOrderLightInfo(UcOrderLightInfoBatchReq req) {
        UcOrderBatchLightInfoSoIn soIn = Convert.convert(UcOrderBatchLightInfoSoIn.class, req);
        UcOrderBatchLightInfoSoOut userOrderLightInfo = userComplaintViewService.getUcOrderLightInfo(soIn);
        return Result.success(Convert.convert(UcOrderLightInfoBatchResp.class, userOrderLightInfo));
    }
}
