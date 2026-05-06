package com.wt.complaint.manage.domain.param;

import lombok.Data;

import java.util.List;

@Data
public class PicCommitParam {

    /**
     * 文件id集合
     */
    private List<Long> ids;

    /**
     * 项目id
     */
    private Long projectId;
}
