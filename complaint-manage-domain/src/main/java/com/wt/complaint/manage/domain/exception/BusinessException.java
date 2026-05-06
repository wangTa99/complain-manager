package com.wt.complaint.manage.domain.exception;

import com.xiaomi.youpin.infra.rpc.errors.ErrorCode;

/**
 * @author linjiehong
 * @date 2024/10/21 20:30
 */
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public BusinessException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message, Object... args) {
        super(String.format(message, args));
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCodeEnums errorCodeEnums, String message, Object... args) {
        super(String.format(message, args));
        this.errorCode = errorCodeEnums.getErrorCode();
    }

    public BusinessException(ErrorCodeEnums errorCodeEnums, String message) {
        super(message);
        this.errorCode = errorCodeEnums.getErrorCode();
    }

    public BusinessException(ErrorCodeEnums errorCodeEnums) {
        super(errorCodeEnums.getName());
        this.errorCode = errorCodeEnums.getErrorCode();
    }
}
