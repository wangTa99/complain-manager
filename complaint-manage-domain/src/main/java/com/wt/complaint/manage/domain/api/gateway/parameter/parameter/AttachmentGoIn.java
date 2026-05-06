package com.wt.complaint.manage.domain.api.gateway.parameter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttachmentGoIn implements Serializable {
    /**
     * 文件ID
     */
    private Long id;

    /**
     * 文件URL
     */
    private String url;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件类型 1 图片 2 视频 3 其他 4  声音文件�? pdf
     */
    private Integer type;

    /**
    * 文件ID
     */
    private Long fileId;

}