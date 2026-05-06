package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttachmentGoOut {
    /**
     * 文件ID
     */
    private Long id;

    /**
     * 文件�?
     */
    private String fileName;

    /**
     * 文件URL
     */
    private String url;

    /**
     * 文件类型 1 图片 2 视频 3 其他 4  声音文件�? pdf
     */
    private Integer type;
}
