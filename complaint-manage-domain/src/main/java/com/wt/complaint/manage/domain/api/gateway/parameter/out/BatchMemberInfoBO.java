package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhengziwei
 * @date 2023/6/14 2:07 下午
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchMemberInfoBO {

    private List<MemberInfoBo> list;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfoBo {
        private String mid;
        private String vid;
        // 1: vip; 2: svip;
        private Integer level;
        // VIP�?SVIP
        private String levelName;
    }
}
