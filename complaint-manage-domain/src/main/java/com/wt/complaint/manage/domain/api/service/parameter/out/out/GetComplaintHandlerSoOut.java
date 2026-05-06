package com.wt.complaint.manage.domain.api.service.parameter.out;

import cn.hutool.core.collection.CollUtil;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetComplaintHandlerSoOut {
    List<GetComplaintHandlerInfo> handlerInfoList;


    public void fillHandlerInfoList(List<EmployeeInfoGoOut> employeeInfoGoOuts, List<ComplaintOrderInfoGoIn> orderList) {
        List<GetComplaintHandlerInfo> tempHandlerInfoList = new ArrayList<>();
        if (CollUtil.isEmpty(employeeInfoGoOuts)) {
            return;
        }
        // 根据mid对employeeInfoGoOuts去重，后者替代前�?
        employeeInfoGoOuts = employeeInfoGoOuts.stream().collect(Collectors.collectingAndThen(
            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EmployeeInfoGoOut::getMiId))), ArrayList::new));
        Map<Long, List<ComplaintOrderInfoGoIn>> orderMap = orderList.stream().collect(Collectors.groupingBy(e -> e.getOperatorMid()));
        this.handlerInfoList = tempHandlerInfoList;
        employeeInfoGoOuts.stream().forEach(e->{
            tempHandlerInfoList.add(GetComplaintHandlerInfo.builder()
                .mid(e.getMiId().toString())
                .name(e.getName())
                .notFinishedCnt(orderMap.getOrDefault(e.getMiId(), new LinkedList<>()).size())
                .build());
        });
    }
}
