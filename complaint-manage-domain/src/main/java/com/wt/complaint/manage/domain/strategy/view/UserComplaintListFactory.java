package com.wt.complaint.manage.domain.strategy.view;

import com.wt.complaint.manage.api.model.enums.UcOrderTypeEnum;
import com.wt.complaint.manage.domain.constant.StrategyConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import javax.annotation.Resource;

@Slf4j
@Service
public class UserComplaintListFactory {
    @Resource
    private Map<String, UserComplaintListStrategy> userComplaintListStrategyMap;

    /**
     * 通过策略设计模式,实现不同类型单据的查�?
     */
    public UserComplaintListStrategy getStrategy(Integer ucType) {
        // 举报�?
        if (UcOrderTypeEnum.REPORT_ORDER.getCode() == ucType) {
            return userComplaintListStrategyMap.get(StrategyConstant.REPORT_ORDER_LIST_SEARCH);
        }
        return null;
    }
}
