package com.wt.complaint.manage.app.providerimpl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import com.wt.complaint.manage.api.model.req.FileReq;
import com.wt.complaint.manage.api.model.resp.FileResp;
import com.wt.complaint.manage.api.provider.FileProvider;
import com.wt.complaint.manage.app.aspect.ExceptionHandle;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.FileRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.FileInfoGoOut;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@DubboService(timeout = 3000, group = "${dubbo.group}", version = "1.0")
@Validated
public class FileProviderImpl implements FileProvider {

    @Resource
    FileRemoteGateway fileRemoteGateway;

    @Override
    @ExceptionHandle
    public Result<FileResp> getFileInfo(FileReq req) {
        List<FileInfoGoOut> fileList = fileRemoteGateway.getFileList(req.getFileIds(), 3600 * 24);
        log.info("FileProvider#getFileInfo, resp:{}", GsonUtil.toJson(fileList));
        return Result.success(FileResp.builder()
                                      .fileList(Convert.convert(new TypeReference<List<FileResp.FileInfo>>() {
                                      }, fileList))
                                      .build());
    }
}
