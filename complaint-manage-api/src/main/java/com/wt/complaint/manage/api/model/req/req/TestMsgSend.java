package com.wt.complaint.manage.api.model.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangzheyang
 * @date 2025/1/3
 */
@Data
public class TestMsgSend implements Serializable {

    private static final long serialVersionUID = 1532712291093855509L;

    private String pushConstant;

    private String complaintNo;

    private String targetOrgId;
}
