package com.wt.complaint.manage.domain.constant;

import com.xiaomi.youpin.infra.rpc.errors.ErrorCode;
import com.xiaomi.youpin.infra.rpc.errors.Scopes;

/**
 * @author huwei
 * @date 2021-06-22
 */
public final class ErrorCodesAndMsgs {
    
    /**
     * 注意 这里的Scopes，需要使用自己项目的~！！！！！！！！！！！！！！！！！！
     */
    public static final ErrorCode CODE_OUTER_FAIL = ErrorCode.createOnce(Scopes.SCOPE_PRORETAIL_GENERAL, 1);
    
    public static final ErrorCode CODE_OUTER_DATA_NOT_FIND = ErrorCode.createOnce(Scopes.SCOPE_PRORETAIL_GENERAL, 2);
    
    public static final ErrorCode CODE_ILLEGAL_PARAMETER = ErrorCode.createOnce(Scopes.SCOPE_PRORETAIL_GENERAL, 3);
    
    public static final String MSG_OUTER_POLICY_DATA_NOT_FIND = "未查询到政策信息";
    
    public static final String MSG_ILLEGAL_PARAMETER_POLICY_ID = "政策id不能为空";
    
    
    private ErrorCodesAndMsgs() {
    
    }
    
}
