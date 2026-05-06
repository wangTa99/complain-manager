package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 统计TAB数量返回
 *
 * @author p-wangkai95
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StaticTabCountGoOut implements Serializable {

    private List<StaticTabCountGoOut.TabData> tabDataList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TabData implements Serializable {

        /**
         * tab 1-待接�? 2-处理�? 3-即将超时, 4-已结�?
         */
        private Integer tab;

        /**
         * 数量
         */
        private Integer count;
    }
}
