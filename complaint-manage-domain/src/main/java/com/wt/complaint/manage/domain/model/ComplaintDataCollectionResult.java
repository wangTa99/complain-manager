package com.wt.complaint.manage.domain.model;

import com.wt.complaint.manage.domain.api.service.parameter.in.TemplateStructSoIn;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 投诉数据收集结果
 *
 * @author zhangzheyang
 * @date 2025/6/23
 */
@Data
@Builder
public class ComplaintDataCollectionResult {

    private Map<String, List<TemplateStructSoIn>> structMap;
    private List<Long> fileIdFromStruct;
    private List<Long> operatorMidList;
    private List<String> orgList;
    private List<String> complaintNoList;
}
