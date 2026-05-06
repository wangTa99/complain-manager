package com.wt.complaint.manage.domain.bo;

import com.wt.complaint.manage.api.model.Attachment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmContentBo {

    // bpm 审批�?
    private List<BpmBlock> blocks;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BpmBlock {
        // bpm 展示条目
        private List<BpmEntity> entities;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BpmEntity {

        // 条目 key, 保持唯一
        private String key;

        // 展示�?
        private String showName;

        // 展示�?
        private String showValue;

        // 渲染属�?
        private String property;

        // 附件列表
        private List<Attachment> attachmentList;
    }
}
