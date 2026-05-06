package com.wt.complaint.manage.domain.strategy.operate;

import com.wt.complaint.manage.api.model.enums.UcOrderTypeEnum;
import com.wt.complaint.manage.domain.constant.StrategyConstant;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Map;
import javax.annotation.Resource;

/**
 * @author linjiehong
 * @date 2025/5/21 16:37
 */
@Data
@Service
public class UserComplaintOperateFactory {
    @Resource
    private Map<String, UserComplaintOperateStrategy> userComplaintOperateStrategyMap;

    public UserComplaintOperateStrategy getStrategy(UcOrderTypeEnum type) {
        if (type == UcOrderTypeEnum.REPORT_ORDER) {
            return userComplaintOperateStrategyMap.get(StrategyConstant.REPORT_ORDER_OPERATE);
        } else if (type == UcOrderTypeEnum.COMPLAINT_ORDER) {
            return userComplaintOperateStrategyMap.get(StrategyConstant.COMPLAINT_ORDER_OPERATE);
        }

        return null;
    }
}
