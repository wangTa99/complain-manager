package com.wt.complaint.manage.app.aspect;

import cn.hutool.core.io.file.FileNameUtil;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.ErrorCode;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolationException;
import java.util.Objects;

/**
 * @author linjiehong
 * @date 2025/5/21 20:56
 */
@Aspect
@Slf4j
@Component
@SuppressWarnings("checkstyle:MagicNumber")
@Order(20)
public class ExceptionHandleAspect {
    /**
     * 定义切点
     */
    @Pointcut("@annotation(com.wt.complaint.manage.app.aspect.ExceptionHandle)")
    public void exceptionHandle() {
        // do nothing
    }

    @Around("exceptionHandle()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().toString().split("@")[0];
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        Object[] args = joinPoint.getArgs();
        String simpleName = FileNameUtil.extName(className);
        try {
            log.info("===== {}.{} req: {}", simpleName, methodName, GsonUtil.toJson(args));
            Object proceed = joinPoint.proceed();
            log.info("call success ===== {}.{} resp: {}", simpleName, methodName, GsonUtil.toJson(proceed));
            return proceed;
        } catch (BusinessException | ConstraintViolationException | IllegalArgumentException e) {
            log.warn("业务异常: {}", e.getMessage(), e);
            ErrorCode errorCode = null;
            if (e instanceof BusinessException) {
                errorCode = ((BusinessException) e).getErrorCode();
            }
            Result<Object> result = Result.fail(Objects.nonNull(errorCode) ? errorCode : ErrorCodeEnums.BUS_ERROR.getErrorCode(), e.getMessage());
            log.warn("call fail ===== {}.{} resp: {}", simpleName, methodName, GsonUtil.toJson(result));
            return result;
        } catch (Exception e) {
            log.error("未知异常: {}", e.getMessage(), e);
            Result<Object> result = Result.fail(GeneralCodes.InternalError, e.getMessage());
            log.info("call error ===== {}.{} resp: {}", simpleName, methodName, GsonUtil.toJson(result));
            return result;
        }

    }
}
