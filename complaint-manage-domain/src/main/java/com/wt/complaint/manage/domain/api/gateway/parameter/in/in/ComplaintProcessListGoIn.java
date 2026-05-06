package com.wt.complaint.manage.domain.api.gateway.parameter.in;

import cn.hutool.core.collection.CollUtil;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class ComplaintProcessListGoIn {

    private Boolean useMaster = false;

    /**
     * 客诉单号
     */
    private String complaintNo;

    /**
     * 客诉记录类型
     */
    private List<String> processTypeList;

    /**
     *  bpmId
     */
    private String processInstanceId;

    public void checkGoIn() {
        if (Objects.isNull(complaintNo) && Objects.isNull(processInstanceId)) {
            log.warn("ComplaintProcessListGoIn.checkGoIn: complaintNo or processInstanceId is null");
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR.getErrorCode(), "客诉单号不可为空");
        }
        if (CollUtil.isEmpty(processTypeList)) {
            log.warn("ComplaintProcessListGoIn.checkGoIn: processTypeList is empty, processTypeList={}", processTypeList);
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR.getErrorCode(), "processTypeList不可为空");
        }
    }
}
