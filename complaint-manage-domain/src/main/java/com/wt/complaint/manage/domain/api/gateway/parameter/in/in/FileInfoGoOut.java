package com.wt.complaint.manage.domain.api.gateway.parameter.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileInfoGoOut {
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
