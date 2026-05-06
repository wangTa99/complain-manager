package com.wt.complaint.manage.domain.stateflow;

import cn.hutool.core.collection.CollUtil;
import com.wt.complaint.manage.api.model.enums.RetailComplaintOrderStatusEnum;
import com.wt.complaint.manage.api.model.enums.UcOrderTypeEnum;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserComplaintStatusEventFactory {

    private final Map<String, UserComplaintStatusEventHandler> statusEventHandlerMap = new HashMap<>();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @PostConstruct
    public void register() {
        Map<String, UserComplaintStatusEventHandler> beanMap = webApplicationContext.getBeansOfType(UserComplaintStatusEventHandler.class);
        if (!beanMap.isEmpty()) {
            beanMap.forEach((key, value) -> {
                String orderType = value.getUcOrderType().getDesc();
                List<Integer> sourceList = value.getSourceList();
                Integer target = value.getTarget();
                log.info("UC StatusEventFactory register --- type:{} ---- status transition from {} to {}", orderType, sourceList, target);
                calKeyList(orderType, sourceList, target).forEach(t -> statusEventHandlerMap.put(t, value));
            });
        }
    }

    public UserComplaintStatusEventHandler getStatusEventHandler(String orderType, Integer source, Integer target) {
        UserComplaintStatusEventHandler result =  statusEventHandlerMap.get(calKey(orderType, source, target));
        if (result == null) {
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "非法的状态机策略:" + RetailComplaintOrderStatusEnum.getByCode(source) + "�? + RetailComplaintOrderStatusEnum.getByCode(target));
        }
        return result;
    }

    private static List<String> calKeyList(String orderType, List<Integer> sourceList, Integer target) {
        if (CollUtil.isEmpty(sourceList)) {
            sourceList = new ArrayList<>();
            sourceList.add(null);
        }
        return sourceList.stream().map(t -> orderType + "-" + t + "-" + target).collect(Collectors.toList());
    }

    private static String calKey(String orderType, Integer source, Integer target) {
        return orderType + "-" + source + "-" + target;
    }

    @SuppressWarnings("squid:S2259")
    public static void main(String[] args) {
        System.out.println(calKeyList(UcOrderTypeEnum.COMPLAINT_ORDER.getDesc(), null, 10));
        System.out.println(calKey(UcOrderTypeEnum.COMPLAINT_ORDER.getDesc(), null, 10));
    }
}
