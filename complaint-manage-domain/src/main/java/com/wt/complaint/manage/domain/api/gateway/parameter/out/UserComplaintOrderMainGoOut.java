package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import com.wt.complaint.manage.domain.model.UserComplaintOrderInfo;
import lombok.Data;

import java.util.List;

/**
 * @author linjiehong
 * @date 2025/5/22 20:30
 */
@Data
public class UserComplaintOrderMainGoOut {
    List<UserComplaintOrderInfo> userComplaintOrderInfoList;
}
