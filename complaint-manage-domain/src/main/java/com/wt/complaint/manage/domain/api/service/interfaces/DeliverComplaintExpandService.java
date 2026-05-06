package com.wt.complaint.manage.domain.api.service.interfaces;

import com.wt.complaint.manage.domain.bo.DeliverComplaintExpandBO;

import java.util.List;

public interface DeliverComplaintExpandService {

    /**
     * 批量更新非空字段
     * @param list 扩展信息列表
     * @return 更新成功的记录数
     */
    int updateBatchSelective(List<DeliverComplaintExpandBO> list);
}
