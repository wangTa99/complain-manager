package com.wt.complaint.manage.api.model.req.retail;

import com.wt.complaint.manage.api.model.Attachment;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotBlank;

/**
 * 查询跟进记录
 *
 * @author p-wangkai95
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RetailFollowRecordReq implements Serializable {

    private static final long serialVersionUID = -1991899787151781807L;

    @ApiDocClassDefine(value = "drNo", required = true, description = "客诉单号")
    @NotBlank(message = "客诉单号不能为空")
    private String drNo;

    @ApiDocClassDefine(value = "followInfo", required = true, description = "跟进详情")
    @NotBlank(message = "跟进详情不能为空")
    private String followInfo;

    @ApiDocClassDefine(value = "attachmentList", description = "附件信息")
    private List<Attachment> attachmentList;
}
