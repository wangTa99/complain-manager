package com.wt.complaint.manage.domain.serviceimpl;

import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintOrderRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintFollowProcessGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 提交复盘事务服务：仅包含跟进记录插入与客诉单更新，不包含 RPC 等非 DB 操作�?
 */
@Slf4j
@Service
public class ComplaintSubmitReviewTransactionService {

    @Resource
    private ComplaintOrderRepositoryGateway complaintOrderRepositoryGateway;

    @Resource
    private ComplaintFollowProcessRepositoryGateway complaintFollowProcessRepositoryGateway;

    /**
     * 事务内写入「提交复盘」跟进记录并更新客诉单已复盘标识�?
     */
    @Transactional(rollbackFor = Exception.class)
    public void doSubmitReviewInTransaction(ComplaintFollowProcessGoIn followProcessGoIn,
            ComplaintOrderInfoGoIn orderUpdateGoIn) {
        boolean saveProcess = complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(followProcessGoIn);
        if (!saveProcess) {
            log.error("submitReview 保存跟进记录失败，complaintNo:{}", followProcessGoIn.getComplaintNo());
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "提交复盘失败");
        }

        boolean updateOrder = complaintOrderRepositoryGateway.updateComplaintInfo(orderUpdateGoIn);
        if (!updateOrder) {
            log.error("submitReview 更新客诉单复盘状态失败，complaintNo:{}", orderUpdateGoIn.getComplaintNo());
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "提交复盘失败");
        }
    }
}
