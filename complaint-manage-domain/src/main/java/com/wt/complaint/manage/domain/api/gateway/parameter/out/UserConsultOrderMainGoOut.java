package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import com.wt.complaint.manage.domain.model.UserConsultOrderInfo;
import lombok.Data;

import java.util.List;

/**
 * 咨询单主表数据出�?
 */
@Data
public class UserConsultOrderMainGoOut {
    List<UserConsultOrderInfo> userConsultOrderInfoList;
}
