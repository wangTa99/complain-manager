package com.wt.complaint.manage.domain.api.service.interfaces;

import com.wt.complaint.manage.domain.model.UserInfo;

public interface CustomeUserContext {
    UserInfo fromRpcContextForAftersaleWorkbench();

    UserInfo fromRpcContext();
}
