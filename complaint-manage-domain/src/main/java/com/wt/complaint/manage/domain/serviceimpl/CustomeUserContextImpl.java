package com.wt.complaint.manage.domain.serviceimpl;

import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarEmployeeRemoteGateway;
import com.wt.complaint.manage.domain.api.service.interfaces.CustomeUserContext;
import com.wt.complaint.manage.domain.constant.CommonConst;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.model.UserInfo;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class CustomeUserContextImpl implements CustomeUserContext {

    @Resource
    private CarEmployeeRemoteGateway carEmployeeRemoteGateway;

    /**
     * 从context获取用户信息
     * 注：仅售后工作台需要采用下面方�?通过邮箱查询登录用户mid。因为部分用户可能context没有mid（历史问题导致）
     * pad端和客服工作台，可以直接通过上下文获取用户mid。不需要额外一次RPC调用，用下面的fromRpcContext方法�?
     */
    @Override
    public UserInfo fromRpcContextForAftersaleWorkbench() {
        RpcContext rpcContext = RpcContext.getContext();
        if (rpcContext == null) {
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "用户信息为空");
        }
        UserInfo userInfo = new UserInfo();
        String email = rpcContext.getAttachment(CommonConst.RPC_CONTEXT_UPC_EMAIL);
        if (StringUtils.isNotBlank(email)) {
            Long miID = carEmployeeRemoteGateway.queryMidByEmail(email);
            if (miID == null) {
                log.error("miID为空, queryMidByEmail, email:{}", email);
                throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "miID不能为空");
            }
            userInfo.setMiID(miID);
        } else {
            log.error("rpc上下文缺少email, rpcContext:{}", GsonUtil.toJson(rpcContext));
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "用户email信息不存�?请联系技术人员排查原�?);
        }
        userInfo.setEmail(email);
        userInfo.setCurrRole(rpcContext.getAttachment(CommonConst.RPC_CONTEXT_CURR_ROLE));
        userInfo.setRoleList(rpcContext.getAttachment(CommonConst.RPC_CONTEXT_UPC_ROLES_LIST));
        userInfo.setUserName(rpcContext.getAttachment(CommonConst.RPC_CONTEXT_UPC_USERNAME));
        userInfo.setAccount(rpcContext.getAttachment(CommonConst.RPC_CONTEXT_UPC_ACCOUNT));
        userInfo.setTraceId(rpcContext.getAttachment(CommonConst.RPC_CONTEXT_TRACE_ID));
        log.info("fromRpcContextForAftersaleWorkbench, userInfo:{}", GsonUtil.toJson(userInfo));
        return userInfo;
    }

    @Override
    public UserInfo fromRpcContext() {
        RpcContext rpcContext = RpcContext.getContext();
        if (rpcContext == null) {
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "用户信息为空");
        }
        UserInfo userInfo = new UserInfo();
        String mid = rpcContext.getAttachment(CommonConst.RPC_CONTEXT_UPC_MID);
        if (StringUtils.isBlank(mid)) {
            log.warn("fromRpcContext $Cookie ={}", rpcContext.getAttachments());
            throw new BusinessException(GeneralCodes.NotAuthorized, "当前用户未登�? 请登�?");
        }
        userInfo.setMiID(Long.valueOf(mid));
        userInfo.setEmail(rpcContext.getAttachment(CommonConst.RPC_CONTEXT_UPC_EMAIL));
        userInfo.setCurrRole(rpcContext.getAttachment(CommonConst.RPC_CONTEXT_CURR_ROLE));
        userInfo.setRoleList(rpcContext.getAttachment(CommonConst.RPC_CONTEXT_UPC_ROLES_LIST));
        userInfo.setUserName(rpcContext.getAttachment(CommonConst.RPC_CONTEXT_UPC_USERNAME));
        userInfo.setAccount(rpcContext.getAttachment(CommonConst.RPC_CONTEXT_UPC_ACCOUNT));
        userInfo.setTraceId(rpcContext.getAttachment(CommonConst.RPC_CONTEXT_TRACE_ID));
        log.info("fromRpcContext, userInfo:{}", GsonUtil.toJson(userInfo));
        return userInfo;
    }

}
