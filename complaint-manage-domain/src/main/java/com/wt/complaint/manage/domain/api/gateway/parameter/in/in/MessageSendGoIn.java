package com.wt.complaint.manage.domain.api.gateway.parameter.in;

import com.wt.complaint.manage.domain.api.enums.PushEnum;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class MessageSendGoIn implements Serializable {

    private static final long serialVersionUID = -2098538129129320415L;
    private List<Long> midList;
    private PushEnum pushEnum;
    private String complaintNo;
    private String requestId;
    private Map<String, String> payload;
}
