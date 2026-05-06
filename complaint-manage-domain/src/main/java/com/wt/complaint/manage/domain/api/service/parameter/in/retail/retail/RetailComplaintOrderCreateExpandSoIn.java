package com.wt.complaint.manage.domain.api.service.parameter.in.retail;

import com.wt.complaint.manage.domain.api.service.parameter.in.TemplateFieldSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.TemplateStructSoIn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 零售客诉单创建扩展入�?
 * 封装客诉单的扩展信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetailComplaintOrderCreateExpandSoIn implements Serializable {
    /**
     * 跟进客服mid
     */
    private String customerServiceMid;

    /**
     * 车牌�?
     */
    private String carNo;

    /**
     * 客诉信息详情
     */
    private List<TemplateStructSoIn> complaintInfo;

    /**
     * 服务场景
     * com.wt.complaint.manage.api.model.enums.serviceSceneEnum
     */
    private List<String> serviceScene;

    /**
     * 门店id
     */
    private String orgId;

    /**
     * 关联单号
     */
    private String relateOrderNo;

    /**
     * 获取模板指定字段�?
     * @param fileCode 字段编码
     * @return 匹配字段的valueCode�?
     */
    public Object getFieldsValue(String fileCode) {
        Object result = null;
        for (TemplateStructSoIn templateStructSoIn : this.complaintInfo) {
            for (TemplateFieldSoIn field : templateStructSoIn.getFields()) {
                if (!field.getFieldCode().equals(fileCode)) {
                    continue;
                }
                return field.getValueCode();
            }
        }
        return result;
    }
}
