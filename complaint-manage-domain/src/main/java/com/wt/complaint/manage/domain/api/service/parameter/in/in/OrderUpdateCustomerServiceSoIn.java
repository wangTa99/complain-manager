package com.wt.complaint.manage.domain.api.service.parameter.in;

import cn.hutool.core.collection.CollUtil;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class OrderUpdateCustomerServiceSoIn {
    private List<OrderUpdateCustomerServiceInfo> orderUpdateCustomerServiceInfos;

    public void checkServiceSoIn() {
        if (CollUtil.isEmpty(orderUpdateCustomerServiceInfos)) {
            log.error("更新服务信息为空");
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "更新信息为空");
        }
        orderUpdateCustomerServiceInfos.stream().forEach( e-> {
                if (StringUtils.isEmpty(e.getStNo()) || Objects.isNull(e.getCustomerServiceMid())) {
                    log.error("订单号或客服工号不能为空, getCustomerServiceMid:{}, stNo:{}", e.getCustomerServiceMid(), e.getStNo());
                    throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "更新信息为空");
                }
            }
        );
    }

    public List<String> getStNo() {
        List<String> collect = orderUpdateCustomerServiceInfos.stream().map(e -> e.getStNo()).collect(Collectors.toList());
        return collect;
    }

}
