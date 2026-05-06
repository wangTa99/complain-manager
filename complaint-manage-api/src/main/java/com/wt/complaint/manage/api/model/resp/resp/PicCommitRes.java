package com.wt.complaint.manage.api.model.resp;

import lombok.Data;

import java.util.Map;

@Data
public class PicCommitRes {

    /**
     * 文件id
     */
    private Map<Long, String> ids;

    /**
     * 项目id
     */
    private Long projectId;
}
