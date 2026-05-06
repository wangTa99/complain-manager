package com.wt.complaint.manage.domain.strategy.process;

import cn.hutool.core.collection.CollUtil;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.FileRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.AttachmentGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.FileInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.RecordInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintFollowProcessGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintOrderDetailSoOut;
import com.wt.complaint.manage.domain.constant.StrategyConstant;
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
@Service(StrategyConstant.REPORT_ORDER_FOLLOW_PROCESS)
public abstract class AbstractSearch implements FollowProcessStrategy {

    @Resource
    private ComplaintFollowProcessRepositoryGateway processRepositoryGateway;

    @Resource
    private MoneThreadPoolExecutor commonThreadPoolExecutor;

    @Resource
    private FileRemoteGateway fileRemoteGateway;

    public CompletableFuture<List<ComplaintFollowProcessGoOut>> getProcessLogFuture(
            UserComplaintOrderDetailSoOut userComplaintOrderDetailSoOut) {
        return CompletableFuture.supplyAsync(
                () -> processRepositoryGateway.getProcessListByNo(userComplaintOrderDetailSoOut.getUcNo()),
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
