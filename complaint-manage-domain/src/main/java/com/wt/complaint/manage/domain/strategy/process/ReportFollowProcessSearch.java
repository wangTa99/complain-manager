package com.wt.complaint.manage.domain.strategy.process;

import cn.hutool.core.util.ObjectUtil;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.UserComplaintOrderGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.FileRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.FileInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintFollowProcessGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintProcessSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintProcessListSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintOrderDetailSoOut;
import com.wt.complaint.manage.domain.constant.StrategyConstant;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Resource;

@Slf4j
@Service(StrategyConstant.REPORT_ORDER_FOLLOW_PROCESS)
public class ReportFollowProcessSearch extends AbstractSearch {

    @Resource
    private UserComplaintOrderGateway userComplaintOrderGateway;

    @Resource
    private ComplaintFollowProcessRepositoryGateway processRepositoryGateway;

    @Resource
    private MoneThreadPoolExecutor commonThreadPoolExecutor;

    @Resource
    private FileRemoteGateway fileRemoteGateway;

    @Override
    public ComplaintProcessListSoOut getFollowUpRecords(ComplaintProcessSoIn soIn) {
        ComplaintProcessListSoOut soOut = new ComplaintProcessListSoOut();
        UserComplaintOrderDetailSoOut userComplaintOrderDetailSoOut =
                userComplaintOrderGateway.selectDetailByUcNo(soIn.getUcNo());
        if (ObjectUtil.isNull(userComplaintOrderDetailSoOut)) {
            log.info("未找到举报单信息,ucNo={}", soIn.getUcNo());
            throw new BusinessException(ErrorCodeEnums.USER_COMPLAINT_ORDER_NOT_FOUND);
        }
        // 查询跟进记录信息
        CompletableFuture<List<ComplaintFollowProcessGoOut>> processLogFuture =
                getProcessLogFuture(userComplaintOrderDetailSoOut);
        List<ComplaintFollowProcessGoOut> followProcessGoOuts = processLogFuture.join();
        Map<Long, FileInfoGoOut> processRecordsAttachments = getProcessRecordsAttachments(followProcessGoOuts);
        soOut.fillProcessList(followProcessGoOuts, processRecordsAttachments);
        return soOut;
    }
}
