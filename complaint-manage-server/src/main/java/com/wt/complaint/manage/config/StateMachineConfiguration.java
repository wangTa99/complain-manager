package com.wt.complaint.manage.config;

import com.wt.complaint.manage.api.model.enums.UcOrderEventEnum;
import com.wt.complaint.manage.api.model.enums.ReportOrderStatusEnum;
import com.wt.complaint.manage.domain.statemachine.UcOrderContext;
import com.wt.complaint.manage.domain.statemachine.StateMachine;
import com.wt.complaint.manage.domain.statemachine.StateMachineBuilder;
import com.wt.complaint.manage.domain.statemachine.Transition;
import com.wt.complaint.manage.domain.statemachine.interfaceImpl.report.AddFollowRecordEventHandler;
import com.wt.complaint.manage.domain.statemachine.interfaceImpl.report.JudgeEventHandler;
import com.wt.complaint.manage.domain.statemachine.interfaceImpl.report.PickUpEventHandler;
import com.wt.complaint.manage.domain.statemachine.interfaceImpl.report.RemindEventHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 状态机配置�?
 */
@Configuration
public class StateMachineConfiguration {
    @Resource
    private PickUpEventHandler pickUpEventHandler;

    @Resource
    private RemindEventHandler remindEventHandler;

    @Resource
    private AddFollowRecordEventHandler addFollowRecordEventHandler;

    @Resource
    private JudgeEventHandler judgeEventHandler;


    /**
     * 构建状态机
     * 使用方式�?
     * 1. 创建 builder
     * 2. 往builder添加转换Transition，包括：
     *      - from       当前状�?
     *      - to         目标状�?
     *      - event      事件
     *      - action     状态扭转行�?
     *      - condition  条件（可选）
     * @return
     */
    @Bean(name = "reportOrderStateMachine")
    public StateMachine<ReportOrderStatusEnum, UcOrderEventEnum, UcOrderContext> stateMachine() {
        StateMachineBuilder<ReportOrderStatusEnum, UcOrderEventEnum, UcOrderContext> builder = new StateMachineBuilder<>();

        // 目标状态：待接�?
        builder.addTransition(
                Transition.<ReportOrderStatusEnum, UcOrderEventEnum, UcOrderContext>builder()
                        .from(ReportOrderStatusEnum.PENDING_ORDER)
                        .to(ReportOrderStatusEnum.PENDING_ORDER)
                        .event(UcOrderEventEnum.REMIND_ORDER)
                        .action(remindEventHandler.action())
                        .build());

        builder.addTransition(
                Transition.<ReportOrderStatusEnum, UcOrderEventEnum, UcOrderContext>builder()
                        .from(ReportOrderStatusEnum.PENDING_ORDER)
                        .to(ReportOrderStatusEnum.PENDING_ORDER)
                        .event(UcOrderEventEnum.ADD_FOLLOW_RECORD)
                        .action(addFollowRecordEventHandler.action())
                        .build());

        // 目标状态：待举报判�?
        builder.addTransition(
                Transition.<ReportOrderStatusEnum, UcOrderEventEnum, UcOrderContext>builder()
                        .from(ReportOrderStatusEnum.PENDING_ORDER)
                        .to(ReportOrderStatusEnum.PENDING_JUDGE)
                        .event(UcOrderEventEnum.PICKUP_ORDER)
                        .action(pickUpEventHandler.action())
                        .build());

        builder.addTransition(
                Transition.<ReportOrderStatusEnum, UcOrderEventEnum, UcOrderContext>builder()
                        .from(ReportOrderStatusEnum.PENDING_JUDGE)
                        .to(ReportOrderStatusEnum.PENDING_JUDGE)
                        .event(UcOrderEventEnum.REMIND_ORDER)
                        .action(remindEventHandler.action())
                        .build());

        builder.addTransition(
                Transition.<ReportOrderStatusEnum, UcOrderEventEnum, UcOrderContext>builder()
                        .from(ReportOrderStatusEnum.PENDING_JUDGE)
                        .to(ReportOrderStatusEnum.PENDING_JUDGE)
                        .event(UcOrderEventEnum.ADD_FOLLOW_RECORD)
                        .action(addFollowRecordEventHandler.action())
                        .build());

        // 目标状态：已完�?
        builder.addTransition(
                Transition.<ReportOrderStatusEnum, UcOrderEventEnum, UcOrderContext>builder()
                        .from(ReportOrderStatusEnum.PENDING_JUDGE)
                        .to(ReportOrderStatusEnum.FINISH)
                        .event(UcOrderEventEnum.JUDGE_ORDER)
                        .action(judgeEventHandler.action())
                        .build());

        return builder.buildStateMachine();
    }
}
