package com.wt.complaint.manage.api.model.req.deliver;

import com.wt.complaint.manage.api.model.Attachment;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 申免责请求体
 * @author huxiankang
 * @date 2025-06-24 10:12:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliverComplaintApplyExemptionReq implements Serializable {

    @ApiDocClassDefine(value = "drNo", description = "客诉单号", required = true)
    @NotBlank(message = "drNo不能为空")
    private String drNo;

    @ApiDocClassDefine(value = "exemptionReason", description = "申免责理�?, required = true)
    @NotBlank(message = "exemptionReason不能为空")
    private String exemptionReason;

    @ApiDocClassDefine(value = "attachmentList", description = "附件")
    private List<Attachment> applyExemptionAttachmentList;

    /**
     *  入参检�?
     */
    public void check() {
        if (!this.drNo.startsWith("DR")) {
            throw new IllegalArgumentException("非交付客诉单，请联系系统管理�?);
        }
        if (!CollectionUtils.isEmpty(this.applyExemptionAttachmentList)) {
            if (this.applyExemptionAttachmentList.size() > 10) {
                throw new IllegalArgumentException("无法上传超过10个附�?);
            }
            // 检查是否存在非法文�?
            if (!CollectionUtils.isEmpty(this.applyExemptionAttachmentList.stream()
                                                       .map(Attachment::getId)
                                                       .filter(Objects::isNull)
                                                       .collect(Collectors.toList()))) {
                throw new IllegalArgumentException("上传文件中包含非法附�?);
            }
        }
    }
}
