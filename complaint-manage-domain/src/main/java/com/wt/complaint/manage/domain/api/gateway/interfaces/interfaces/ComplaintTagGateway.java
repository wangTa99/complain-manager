package com.wt.complaint.manage.domain.api.gateway.interfaces;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintTagListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintTagGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintTagSoIn;

import java.util.List;

/**
 * @author zhangzheyang
 * @date 2024/12/25
 */
public interface ComplaintTagGateway {

    Boolean insertTag(ComplaintTagSoIn req);

    Boolean batchInsertTag(List<ComplaintTagSoIn> soInList);

    List<ComplaintTagGoOut> getComplaintTagByComplaintNo(ComplaintTagListGoIn goIn);

    /**
     * 软删除标�?
     * @param complaintNo 投诉单号
     * @param tagType 标签类型
     * @return 是否成功
     */
    Boolean deleteTag(String complaintNo, String tagType);
}
