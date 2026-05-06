package com.wt.complaint.manage.api.model.resp.retail;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 响应文件信息
 * 文件ID、文件名、文件url地址、文件类型等�?
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttachmentSoResp {

    @ApiDocClassDefine(value = "id", description = "文件ID")
    private Long id;

    @ApiDocClassDefine(value = "fileName", description = "文件�?)
    private String fileName;

    @ApiDocClassDefine(value = "url", description = "文件URL")
    private String url;

    @ApiDocClassDefine(value = "type", description = "文件类型 1 图片 2 视频 3 其他 4  声音文件�? pdf")
    private Integer type;
}
