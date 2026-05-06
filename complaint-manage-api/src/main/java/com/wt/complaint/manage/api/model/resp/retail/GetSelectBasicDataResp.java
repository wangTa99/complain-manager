package com.wt.complaint.manage.api.model.resp.retail;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 获取基础数据下拉框返�?
 *
 * @author p-wangkai95
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetSelectBasicDataResp implements Serializable {

    private static final long serialVersionUID = 14101859097845091L;

    @ApiDocClassDefine(value = "type", description = "数据类型 0-大区 1-小区 2-门店")
    private Integer type;

    @ApiDocClassDefine(value = "selectDataList", description = "基础数据下拉�?)
    private List<SelectData> selectDataList;

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

        private static final long serialVersionUID = 5608733365123640849L;

        @ApiDocClassDefine(value = "value", description = "选项实际�?)
        private String value;

        @ApiDocClassDefine(value = "label", description = "选项显示文本")
        private String label;
    }
}

