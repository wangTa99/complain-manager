package com.wt.complaint.manage.domain.model;

import com.wt.complaint.manage.domain.value.ComplaintListHeader;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageQueryComplaintListModel {

    /**
     * 客诉列表导出数据
     */
    private List<ComplaintListHeader> complaintListHeaderList;
}
