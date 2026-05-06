package com.wt.complaint.manage.domain.api.service.parameter.in;

import com.wt.complaint.manage.domain.constant.ComplaintInfoConstant;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.nr.common.utils.GsonUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintOrderCreateExpandSoIn {
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
     * @param fileCode
     * @return
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
