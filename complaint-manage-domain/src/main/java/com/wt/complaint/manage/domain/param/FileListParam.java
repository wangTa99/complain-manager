package com.wt.complaint.manage.domain.param;

import lombok.Data;

import java.util.List;

@Data
public class FileListParam {

    /**
     * 文件id列表
     */
    private List<Long> fileIdList;

    /**
     * 过期时长，单位分�?
     */
    private Integer expireTime;

    /**
     * 项目id，不填默认本项目
     */
    private Long projectId;

}
