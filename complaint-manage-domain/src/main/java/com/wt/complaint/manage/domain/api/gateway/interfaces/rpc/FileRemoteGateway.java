package com.wt.complaint.manage.domain.api.gateway.interfaces.rpc;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.FileInfoGoOut;
import com.wt.complaint.manage.domain.bo.FileInfoBO;
import com.wt.complaint.manage.api.model.resp.PicCommitRes;
import com.wt.complaint.manage.domain.param.PicCommitParam;

import java.io.File;
import java.util.List;

public interface FileRemoteGateway {
    FileInfoGoOut uploadFile(File file);

    void fileCommit(List<Long> fileIdList);

    List<FileInfoGoOut> getFileList(List<Long> fileIdList, Integer expireTime);

    FileInfoBO uploadPublicFile(String filePath);

    PicCommitRes fileCommit(PicCommitParam param);

}
