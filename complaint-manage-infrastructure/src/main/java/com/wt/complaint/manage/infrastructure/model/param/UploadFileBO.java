package com.wt.complaint.manage.infrastructure.model.param;

import lombok.Data;

@Data
public class UploadFileBO {
    private String code;

    private String msg;

    private Content data;

    @Data
    public static class Content {

        private Integer id;

        private String uri;

        private String tmpUri;
    }
}
