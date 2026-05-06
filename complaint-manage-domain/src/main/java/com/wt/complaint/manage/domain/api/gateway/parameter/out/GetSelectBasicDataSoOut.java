package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 获取下拉框数据返�?
 *
 * @author p-wangkai95
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetSelectBasicDataSoOut {
    @ApiDocClassDefine(value = "type", description = "数据类型 0-大区 1-小区 2-门店")
    private Integer type;

    @ApiDocClassDefine(value = "selectDataList", description = "基础数据下拉�?)
    private List<GetSelectBasicDataSoOut.SelectData> selectDataList;

    /**
     * 基础数据
     *
     * @author p-wangkai95
     * @version 1.0
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SelectData implements Serializable {
        @ApiDocClassDefine(value = "value", description = "选项实际值（提交到后端）")
        private String value;

        @ApiDocClassDefine(value = "label", description = "选项显示文本（用户可见）")
        private String label;
    }
}
