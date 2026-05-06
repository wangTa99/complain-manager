package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarStoreListGoIn {
    private List<String> orgIdList;

    private String[] filter;

    private List<Long> groupIdList;

    public Boolean validParam() {
        if (CollectionUtils.isEmpty(orgIdList) && CollectionUtils.isEmpty(groupIdList)) {
            return false;
        }
        return true;
    }
}
