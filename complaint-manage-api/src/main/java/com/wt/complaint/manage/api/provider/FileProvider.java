package com.wt.complaint.manage.api.provider;

import com.wt.complaint.manage.api.model.req.FileReq;
import com.wt.complaint.manage.api.model.resp.FileResp;
import com.xiaomi.youpin.infra.rpc.Result;

public interface FileProvider {

    Result<FileResp> getFileInfo(FileReq req);

}
