package com.wt.complaint.manage.domain.strategy.process;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.RetailComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.FileRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.AttachmentGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.FileInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.RecordInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.RetailComplaintDetailGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintFollowProcessGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.RetailComplaintDetaiGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintProcessSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintProcessListSoOut;
import com.wt.complaint.manage.domain.constant.StrategyConstant;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.annotation.Resource;

@Slf4j
@Service(StrategyConstant.DELIVER_RETAIL_ORDER_FOLLOW_PROCESS)
public class DeliverRetaiFollowProcessSearch implements FollowProcessStrategy {

    @Resource
    private RetailComplaintGateway retailComplaintGateway;

    @Resource
    private ComplaintFollowProcessRepositoryGateway processRepositoryGateway;

    @Resource
    private MoneThreadPoolExecutor commonThreadPoolExecutor;

    @Resource
    private FileRemoteGateway fileRemoteGateway;

    public ComplaintProcessListSoOut getFollowUpRecords(ComplaintProcessSoIn soIn) {
        ComplaintProcessListSoOut soOut = new ComplaintProcessListSoOut();
        RetailComplaintDetaiGoOut retailComplaintDetaiGoOut = retailComplaintGateway.getRetailComplaintDetail(
                RetailComplaintDetailGoIn.builder().drNo(soIn.getUcNo()).build());
        if (ObjectUtil.isNull(retailComplaintDetaiGoOut)) {
            log.info("未找到投诉单单信�?ucNo={}", soIn.getUcNo());
            throw new BusinessException(ErrorCodeEnums.USER_COMPLAINT_ORDER_NOT_FOUND);
        }
        // 查询跟进记录信息
        CompletableFuture<List<ComplaintFollowProcessGoOut>> processLogFuture =
                getProcessLogFuture(retailComplaintDetaiGoOut);
        List<ComplaintFollowProcessGoOut> followProcessGoOuts = processLogFuture.join();
        Map<Long, FileInfoGoOut> processRecordsAttachments = getProcessRecordsAttachments(followProcessGoOuts);
        soOut.fillProcessList(followProcessGoOuts, processRecordsAttachments);
        return soOut;
    }

    public CompletableFuture<List<ComplaintFollowProcessGoOut>> getProcessLogFuture(
            RetailComplaintDetaiGoOut retailComplaintDetaiGoOut) {
        return CompletableFuture.supplyAsync(
                () -> processRepositoryGateway.getProcessListByNo(retailComplaintDetaiGoOut.getDrNo()),
                commonThreadPoolExecutor);
    }

    public Map<Long, FileInfoGoOut> getProcessRecordsAttachments(
            List<ComplaintFollowProcessGoOut> followProcessGoOuts) {
        Map<Long, FileInfoGoOut> resultMap = new HashMap<>();
        if (CollUtil.isEmpty(followProcessGoOuts)) {
            return resultMap;
        }
        List<Long> fileIdUrl = new ArrayList<>();
        for (ComplaintFollowProcessGoOut followProcessGoOut : followProcessGoOuts) {
            if (StringUtils.isNotEmpty(followProcessGoOut.getProcessContent())) {
                RecordInfoGoIn recordInfoGoIn =
                        GsonUtil.fromJson(followProcessGoOut.getProcessContent(), RecordInfoGoIn.class);
                if (CollUtil.isNotEmpty(recordInfoGoIn.getAttachments())) {
                    List<Long> tempFileIdUrl =
                            recordInfoGoIn.getAttachments().stream().map(AttachmentGoIn::getId).collect(
                                    Collectors.toList());
                    fileIdUrl.addAll(tempFileIdUrl);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(fileIdUrl)) {
            List<FileInfoGoOut> fileList = fileRemoteGateway.getFileList(fileIdUrl, null);
            resultMap = fileList.stream().collect(Collectors.toMap(FileInfoGoOut::getFileId, e -> e));
        }
        return resultMap;

    }
}
