package com.wt.complaint.manage.domain.statemachine.interfaces;

public interface Action<S, E, C> {
    boolean execute(S s, S to, E e, C c);
}
