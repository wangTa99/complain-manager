package com.wt.complaint.manage.api.model.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResp implements Serializable {

    List<FileInfo> fileList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileInfo implements Serializable {
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
}
