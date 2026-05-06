package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import com.wt.complaint.manage.domain.model.UserComplaintExpandInfo;
import lombok.Data;

import java.util.List;

/**
 * @author linjiehong
 * @date 2025/5/23 17:04
 */
@Data
public class UserComplaintOrderExpandGoOut {
    List<UserComplaintExpandInfo> userComplaintExpandInfoList;
}
