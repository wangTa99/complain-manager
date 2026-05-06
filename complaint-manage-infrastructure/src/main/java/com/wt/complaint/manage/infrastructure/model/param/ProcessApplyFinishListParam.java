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
public class ProcessApplyFinishListParam implements Serializable {
    /**
     * 客诉单号
     */
    private List<String> complaintNoList;

}
