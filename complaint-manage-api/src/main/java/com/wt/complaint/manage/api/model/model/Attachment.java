package com.wt.complaint.manage.api.model;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Attachment implements Serializable {
    @ApiDocClassDefine(value = "文件ID", description = "文件ID")
    private Long id;

    @ApiDocClassDefine(value = "文件名称", description = "文件名称")
    private String fileName;

    @ApiDocClassDefine(value = "文件URL", description = "文件URL")
    private String url;

    @ApiDocClassDefine(value = "文件类型", description = "文件类型 1 图片 2 视频 3 其他 4  声音文件�? pdf ")
    private Integer type;

    @ApiDocClassDefine(value = "兼容文件ID", description = "兼容文件ID")
    private Long fileId;

}
