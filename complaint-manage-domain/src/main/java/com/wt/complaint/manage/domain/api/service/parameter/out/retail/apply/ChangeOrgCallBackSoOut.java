package com.wt.complaint.manage.domain.api.service.parameter.out.retail.apply;

import com.xiaomi.newretail.bpm.api.model.callback.StatusChangeAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeOrgCallBackSoOut {
    private StatusChangeAction action;
    private List<String> messages;
    private String tableColumnJSON;
    private String tableDataJSON;
    private String businessId;
    private Integer code;
}
