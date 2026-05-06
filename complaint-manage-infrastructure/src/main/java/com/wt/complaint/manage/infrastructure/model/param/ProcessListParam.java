package com.wt.complaint.manage.infrastructure.model.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProcessListParam implements Serializable {

    private Boolean useMaster = false;

    /**
     * 客诉单号
     */
    private String complaintNo;

    /**
     * 处理类型
     */
    private List<String> processTypeList;

    /**
     *  Bpm 流程 ID
     */
    private String processInstanceId;
}
