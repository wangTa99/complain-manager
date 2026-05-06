package com.wt.complaint.manage.domain.model;


import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.rpc.RpcContext;

import java.io.Serializable;

/**
 * rpc的用户信�?
 */
@Data
@Slf4j
public class UserInfo implements Serializable {

    private Long miID;

    private String currRole;

    private String roleList;

    private String userName;

    private String account;

    private String email;

    private String traceId;

    //通过rpccontext获取数据构造对�?
    public static UserInfo fromRpcContext() {
        RpcContext rpcContext = RpcContext.getContext();
        if (rpcContext == null) {
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "用户信息为空");
        }
        UserInfo userInfo = new UserInfo();
        if (StringUtils.isBlank(rpcContext.getAttachment("$upc_miID"))) {
            log.error("rpc上下文缺少miID");
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "miID不能为空");
        }
        userInfo.setMiID(Long.valueOf(rpcContext.getAttachment("$upc_miID")));
        userInfo.setEmail(rpcContext.getAttachment("$upc_email"));
        userInfo.setCurrRole(rpcContext.getAttachment("$curr_role"));
        userInfo.setRoleList(rpcContext.getAttachment("$upc_roles_list"));
        userInfo.setUserName(rpcContext.getAttachment("$upc_userName"));
        userInfo.setAccount(rpcContext.getAttachment("$upc_account"));
        userInfo.setTraceId(RpcContext.getContext().getAttachment("_trace_id_"));
        log.info("rpc上下文信�?{}", userInfo);
        return userInfo;
    }
}

