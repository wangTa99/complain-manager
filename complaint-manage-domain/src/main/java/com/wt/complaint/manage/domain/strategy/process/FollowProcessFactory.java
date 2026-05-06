package com.wt.complaint.manage.domain.strategy.process;

import com.wt.complaint.manage.api.model.enums.UcOrderTypeEnum;
import com.wt.complaint.manage.domain.constant.StrategyConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import javax.annotation.Resource;

@Slf4j
@Service
public class FollowProcessFactory {
    @Resource
    private Map<String, FollowProcessStrategy> complaintFollowProcessStrategyMap;

    /**
     * 通过策略设计模式,实现不同类型单据的查�?
     */
    public FollowProcessStrategy getStrategy(Integer ucType) {
        // 投诉�?
        if (UcOrderTypeEnum.COMPLAINT_ORDER.getCode() == ucType) {
            return complaintFollowProcessStrategyMap.get(StrategyConstant.COMPLAINT_ORDER_FOLLOW_PROCESS);
            // 举报�?
        } else if (UcOrderTypeEnum.REPORT_ORDER.getCode() == ucType) {
            return complaintFollowProcessStrategyMap.get(StrategyConstant.REPORT_ORDER_FOLLOW_PROCESS);
            // 交付/零售单号
        } else if (UcOrderTypeEnum.DELIVER_COMPLAINT_ORDER.getCode() == ucType || UcOrderTypeEnum.RETAIL_COMPLAINT_ORDER.getCode() == ucType) {
            return complaintFollowProcessStrategyMap.get(StrategyConstant.DELIVER_RETAIL_ORDER_FOLLOW_PROCESS);
        } else if (UcOrderTypeEnum.CONSULT_ORDER.getCode() == ucType) {
            return complaintFollowProcessStrategyMap.get(StrategyConstant.CONSULT_ORDER_FOLLOW_PROCESS);
        }
        return null;
    }
}
