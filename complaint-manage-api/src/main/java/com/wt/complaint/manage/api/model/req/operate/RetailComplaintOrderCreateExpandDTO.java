package com.wt.complaint.manage.api.model.req.operate;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 交付/零售客诉单创建扩展信�?
 *
 * @author p-wangkai95
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RetailComplaintOrderCreateExpandDTO implements Serializable {

    private static final long serialVersionUID = 2168340117302398904L;

    @ApiDocClassDefine(value = "customerServiceMid", description = "客服MID")
    @NotBlank(message = "客服MID不能为空")
    private String customerServiceMid;

    @ApiDocClassDefine(value = "carNo", description = "车牌�?)
    private String carNo;

    @ApiDocClassDefine(value = "relateOrderNo", description = "关联单号")
    private String relateOrderNo;

    @ApiDocClassDefine(value = "complaintInfo", description = "投诉信息详情")
    @NotEmpty(message = "投诉信息详情不能为空")
    private List<TemplateStructInfo> complaintInfo;
}
