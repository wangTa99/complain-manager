package com.wt.complaint.manage.domain.statemachine;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;


@Slf4j
public class StateMachine<S, E, C> {
    private Map<E, List<Transition<S, E, C>>> transitionMap;

    StateMachine(Map<E, List<Transition<S, E, C>>> transitionMap) {
        this.transitionMap = transitionMap;
    }

    /**
     * 是否能进行此操作
     * @param s 当前状�?
     * @param e 事件
     * @return true or false
     */
    public boolean verify(S s, E e) {
        List<Transition<S, E, C>> transitionList = transitionMap.get(e);
        if (transitionList == null || transitionList.isEmpty()) {
            return false;
        }
        for (Transition<S, E, C> transition : transitionList) {
            if (transition.from.equals(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 执行状态扭�?
     * @param s 当前状�?
     * @param e 事件
     * @param c 上下�?
     * @return 状态扭转后的状�?
     */
    public S executeAction(S s, E e, C c) {
        if (!transitionMap.containsKey(e)) {
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "ErrorMsgs.STATE_NOT_FOUND");
        }
        if (!verify(s, e)) {
            log.info("State:{}, Event:{}, Context:{}", RetailJsonUtil.toJson(s), RetailJsonUtil.toJson(e), RetailJsonUtil.toJson(c));
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "ErrorMsgs.STATE_CHANGE_NOT_ALLOW");
        }
        List<Transition<S, E, C>> stateNodeList = transitionMap.get(e);
        for (Transition<S, E, C> stateNode : stateNodeList) {
            if (stateNode.from.equals(s)) {
                if (stateNode.condition != null && !stateNode.condition.isSatisfiedBy(c)) {
                    return null;
                }
                stateNode.action.execute(s, stateNode.to, e, c);
                return stateNode.to;
            }
        }
        return null;
    }

}

