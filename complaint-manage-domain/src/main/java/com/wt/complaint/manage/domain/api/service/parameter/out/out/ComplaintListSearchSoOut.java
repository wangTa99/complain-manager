package com.wt.complaint.manage.domain.api.service.parameter.out;

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
public class ComplaintListSearchSoOut implements Serializable {

    private static final long serialVersionUID = 3390901888605448879L;

    /**
     * 总条�?
     */
    private Integer total;

    /**
     * 数据列表
     */
    private List<ComplaintListSearchInfo> dataList;
}
