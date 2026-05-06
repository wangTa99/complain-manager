package com.wt.complaint.manage.domain.bo;

import lombok.Data;

@Data
public class FileInfoBO {

    /**
     * 文件id
     */
    private Long fileId;

    /**
     * 文件url
     */
    private String fileUrl;

    /**
     * 文件名称
     */
    private String fileName;
}
