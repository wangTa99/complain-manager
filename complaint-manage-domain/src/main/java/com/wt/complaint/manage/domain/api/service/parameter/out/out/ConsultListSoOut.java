package com.wt.complaint.manage.domain.api.service.parameter.out;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 咨询单列表查询出�?
 */
@Data
public class ConsultListSoOut implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long total;
    private Integer pageNum;
    private Integer pageSize;
    private List<ConsultListItemSoOut> dataList;
}
