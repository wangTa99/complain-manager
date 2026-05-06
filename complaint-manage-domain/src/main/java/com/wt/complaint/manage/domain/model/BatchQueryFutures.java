package com.wt.complaint.manage.domain.model;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.FileInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintTagGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author zhangzheyang
 * @date 2025/6/23
 */
@Data
@Builder
public class BatchQueryFutures {

    private CompletableFuture<List<EmployeeInfoGoOut>> employInfoFuture;
    private CompletableFuture<List<StoreInfoGoOut>> batchStoreInfoFuture;
    private CompletableFuture<List<FileInfoGoOut>> fileFuture;
    private CompletableFuture<List<ComplaintTagGoOut>> complaintTagFuture;
}
