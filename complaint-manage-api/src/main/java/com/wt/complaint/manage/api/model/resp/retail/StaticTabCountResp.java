package com.wt.complaint.manage.api.model.resp.retail;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
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
public class StaticTabCountResp implements Serializable {

    private static final long serialVersionUID = -7773025745399114604L;

    @ApiDocClassDefine(value = "tabDataList",
            description = "tab数据列表")
    private List<TabData> tabDataList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static class TabData implements Serializable {

        private static final long serialVersionUID = 2769371687791995905L;

        @ApiDocClassDefine(value = "tab",
                description = "tab 1-待接�? 2-处理�? 3-即将超时, 4-已结�?)
        private Integer tab;

        @ApiDocClassDefine(value = "count",
                description = "数量")
        private Integer count;
    }

}
