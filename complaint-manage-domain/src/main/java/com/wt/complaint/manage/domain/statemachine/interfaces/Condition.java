package com.wt.complaint.manage.domain.statemachine.interfaces;

public interface Condition<C> {
    boolean isSatisfiedBy(C context);
}
