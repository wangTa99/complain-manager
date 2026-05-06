package com.wt.complaint.manage.domain.statemachine;

import com.wt.complaint.manage.domain.statemachine.interfaces.Action;
import com.wt.complaint.manage.domain.statemachine.interfaces.Condition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateMachineBuilder<S, E, C> {
    private List<Transition<S, E, C>> transitions;

    /**
     * 添加状态转�?
     *
     * @param transition
     */
    public void addTransition(Transition<S, E, C> transition) {
        if (transitions == null) {
            transitions = new ArrayList<>();
            transitions.add(transition);
        } else {
            transitions.add(transition);
        }
    }

    /**
     * 构建状态机
     */
    public StateMachine<S, E, C> buildStateMachine() {
        if (transitions == null) {
            return null;
        }

        // 根据事件类型分组
        Map<E, List<Transition<S, E, C>>> stateMap = transitions.stream()
                .collect(HashMap::new, (m, t) -> m.computeIfAbsent(t.event, k -> new ArrayList<>()).add(t), HashMap::putAll);
        return new StateMachine<>(stateMap);
    }
}
