package com.wt.complaint.manage.domain.api.service.parameter.out;

import com.wt.complaint.manage.api.model.resp.UcOrderViewInfo;
import lombok.Data;

import java.util.List;

/**
 * @author linjiehong
 * @date 2025/5/26 19:33
 */
@Data
public class UcOrderBatchInfoSoOut {
    private List<UserComplaintDetailSoOut> ucOrderViewInfoList;
}
