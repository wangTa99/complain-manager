package com.wt.complaint.manage.domain.statemachine;

import com.wt.complaint.manage.domain.statemachine.interfaces.Action;
import com.wt.complaint.manage.domain.statemachine.interfaces.Condition;
import lombok.Builder;

@Builder
public class Transition<S, E, C> {
    /**
     * 当前状�?
     */
    S from;

    /**
     * 目标状�?
     */
    S to;

    /**
     * 事件
     */
    E event;

    /**
     * 条件判断
     */
    Condition<C> condition;

    /**
     * 状态扭�?
     */
    Action<S, E, C> action;
}
