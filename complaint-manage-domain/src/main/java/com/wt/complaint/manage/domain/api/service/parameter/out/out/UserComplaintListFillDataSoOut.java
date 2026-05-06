package com.wt.complaint.manage.domain.api.service.parameter.out;

import com.wt.complaint.manage.domain.api.gateway.parameter.out.GetDynamicInfoResponseGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserComplaintListFillDataSoOut implements Serializable {

    private List<StoreInfoGoOut> storeInfoGoOutList;

    private Map<Long, String> midNameMap;

    private GetDynamicInfoResponseGoOut getDynamicInfoResponseGoOut;
}
