package com.wt.complaint.manage.app.providerimpl;

import cn.hutool.core.convert.Convert;
import com.wt.complaint.manage.api.model.req.retail.GetBubbleCountReq;
import com.wt.complaint.manage.api.model.req.retail.RetailComplaintDetailReq;
import com.wt.complaint.manage.api.model.req.retail.RetailComplaintListSearchReq;
import com.wt.complaint.manage.api.model.req.retail.RetailHasFirstResponseRecordFlagReq;
import com.wt.complaint.manage.api.model.req.retail.StaticRetailCountReq;
import com.wt.complaint.manage.api.model.resp.RetailHasFirstResposeRecordFlagResp;
import com.wt.complaint.manage.api.model.resp.retail.BubbleCountResp;
import com.wt.complaint.manage.api.model.resp.retail.GetSelectBasicDataResp;
import com.wt.complaint.manage.api.model.resp.retail.RetailComplaintDetailResp;
import com.wt.complaint.manage.api.model.resp.retail.RetailComplaintListSearchResp;
import com.wt.complaint.manage.api.model.resp.retail.StaticTabCountResp;
import com.wt.complaint.manage.api.provider.RetailComplaintViewProvider;
import com.wt.complaint.manage.app.aspect.ExceptionHandle;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.GetSelectBasicDataSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailHasFirstResponseRecordFlagSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.BubbleCountSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.RetailComplaintDetaiSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.RetailComplaintListSearchSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.RetailHasFirstResposeRecordFlagSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.StaticTabCountSoOut;
import com.wt.complaint.manage.domain.api.service.interfaces.RetailComplaintViewService;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailComplaintDetailSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailComplaintListSearchSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.StaticRetailCountSoIn;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDoc;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;

/**
 * 零售投诉视图服务相关接口
 *
 * @author p-wangkai95
 * @version 1.0
 */
@Slf4j
@DubboService(timeout = 1000, group = "${dubbo.group}", version = "1.0")
@Validated
public class RetailComplaintViewProviderImpl implements RetailComplaintViewProvider {

    @Autowired
    private RetailComplaintViewService retailComplaintViewService;

    private static final String UPC_MIID = "$upc_miID";

    @Override
    @ApiDoc(value = "获取下拉框基础数据", description = "/mtop/proretailcar/retailComplaint/getSelectBasicData")
    @ExceptionHandle
    public Result<GetSelectBasicDataResp> getSelectBasicData() {
        String miID = RpcContext.getContext().getAttachment(UPC_MIID);
        log.info("RetailComplaintViewProviderImpl.getSelectBasicData miID:{}", miID);
        GetSelectBasicDataSoOut soOut =
                retailComplaintViewService.getSelectBasicData(miID);
        GetSelectBasicDataResp resp = Convert.convert(GetSelectBasicDataResp.class, soOut);
        log.info("RetailComplaintViewProviderImpl.getSelectBasicData resp:{}", RetailJsonUtil.toJson(resp));
        return Result.success(resp);
    }

    @Override
    @ApiDoc(value = "获取气泡数量", description = "/mtop/proretailcar/retailComplaint/getBubbleCount")
    @ExceptionHandle
    public Result<BubbleCountResp> getBubbleCount() {
        String miID = RpcContext.getContext().getAttachment(UPC_MIID);
        log.info("RetailComplaintViewProviderImpl.getBubbleCount miID:{}", miID);
        BubbleCountSoOut soOut =
                retailComplaintViewService.getBubbleCount(miID);
        BubbleCountResp resp = Convert.convert(BubbleCountResp.class, soOut);
        log.info("RetailComplaintViewProviderImpl.getBubbleCount resp:{}", RetailJsonUtil.toJson(resp));
        return Result.success(resp);
    }

    @Override
    @ApiDoc(value = "获取气泡数量V2", description = "/mtop/proretailcar/retailComplaint/getBubbleCountV2")
    @ExceptionHandle
    public Result<BubbleCountResp> getBubbleCountV2(GetBubbleCountReq req) {
        String miID = RpcContext.getContext().getAttachment(UPC_MIID);
        log.info("RetailComplaintViewProviderImpl.getBubbleCountV2 req:{}", RetailJsonUtil.toJson(req));
        BubbleCountSoOut soOut =
                retailComplaintViewService.getBubbleCountV2(miID, req.getOrgCode());
        BubbleCountResp resp = Convert.convert(BubbleCountResp.class, soOut);
        log.info("RetailComplaintViewProviderImpl.getBubbleCountV2 resp:{}", RetailJsonUtil.toJson(resp));
        return Result.success(resp);
    }

    @Override
    @ApiDoc(value = "统计TAB数量", description = "/mtop/proretailcar/retailComplaint/staticTabCount")
    @ExceptionHandle
    public Result<StaticTabCountResp> staticTabCount(StaticRetailCountReq req) {
        log.info("RetailComplaintViewProviderImpl.staticTabCount req:{}", RetailJsonUtil.toJson(req));
        StaticRetailCountSoIn soIn = Convert.convert(StaticRetailCountSoIn.class, req);
        soIn.setMid(RpcContext.getContext().getAttachment(UPC_MIID));
        StaticTabCountSoOut soOut = retailComplaintViewService.staticTabCount(soIn);
        StaticTabCountResp resp = Convert.convert(StaticTabCountResp.class, soOut);
        log.info("RetailComplaintViewProviderImpl.staticTabCount resp:{}", RetailJsonUtil.toJson(resp));
        return Result.success(resp);
    }

    @Override
    @ApiDoc(value = "投诉工单列表查询", description = "/mtop/proretailcar/retailComplaint/searchRetailComplaintList")
    @ExceptionHandle
    public Result<RetailComplaintListSearchResp> searchRetailComplaintList(
            RetailComplaintListSearchReq req) {
        log.info("RetailComplaintViewProviderImpl.searchRetailComplaintList req:{}", RetailJsonUtil.toJson(req));
        RetailComplaintListSearchSoIn soIn = Convert.convert(RetailComplaintListSearchSoIn.class, req);
        soIn.setMid(RpcContext.getContext().getAttachment(UPC_MIID));
        RetailComplaintListSearchSoOut soOut = retailComplaintViewService.searchRetailComplaintList(soIn);
        RetailComplaintListSearchResp resp = Convert.convert(RetailComplaintListSearchResp.class, soOut);
        log.info("RetailComplaintViewProviderImpl.searchRetailComplaintList resp:{}", RetailJsonUtil.toJson(resp));
        return Result.success(resp);
    }

    @Override
    @ApiDoc(value = "获取投诉详情", description = "/mtop/proretailcar/retailComplaint/getRetailComplaintDetail")
    @ExceptionHandle
    public Result<RetailComplaintDetailResp> getRetailComplaintDetail(RetailComplaintDetailReq req) {
        log.info("RetailComplaintViewProviderImpl.getRetailComplaintDetail req:{}", RetailJsonUtil.toJson(req));
        RetailComplaintDetailSoIn soIn =
                Convert.convert(RetailComplaintDetailSoIn.class,
                        req);
        soIn.setMid(RpcContext.getContext().getAttachment(UPC_MIID));
        RetailComplaintDetaiSoOut soOut =
                retailComplaintViewService.getRetailComplaintDetail(soIn);
        RetailComplaintDetailResp resp = Convert.convert(RetailComplaintDetailResp.class, soOut);
        log.info("RetailComplaintViewProviderImpl.getRetailComplaintDetail resp:{}", RetailJsonUtil.toJson(resp));
        return Result.success(resp);
    }

    @Override
    @ApiDoc(value = "获取投诉单是否有首响记录标识", description = "/mtop/proretailcar/retailComplaint/getRetailHasFirstResposeRecordFlag")
    @ExceptionHandle
    public Result<RetailHasFirstResposeRecordFlagResp> getRetailHasFirstResposeRecordFlag(
            RetailHasFirstResponseRecordFlagReq req) {
        log.info("RetailComplaintViewProviderImpl.getRetailHasFirstResposeRecordFlag req:{}", RetailJsonUtil.toJson(req));
        RetailHasFirstResposeRecordFlagSoOut retailHasFirstResposeRecordFlag =
                retailComplaintViewService.getRetailHasFirstResposeRecordFlag(
                        Convert.convert(RetailHasFirstResponseRecordFlagSoIn.class, req));
        RetailHasFirstResposeRecordFlagResp resp = Convert.convert(RetailHasFirstResposeRecordFlagResp.class, retailHasFirstResposeRecordFlag);
        log.info("RetailComplaintViewProviderImpl.getRetailHasFirstResposeRecordFlag resp:{}",
                RetailJsonUtil.toJson(resp));
        return Result.success(resp);
    }
}
