package com.wt.complaint.manage.api.model.req.retail;

import com.wt.complaint.manage.api.model.req.operate.RetailComplaintOrderCreateExpandDTO;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 创建交付/零售客诉单请求参�?
 *
 * @author p-wangkai95
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateRetailComplaintOrderReq implements Serializable {

    private static final long serialVersionUID = -1419895860443160013L;

    @ApiDocClassDefine(value = "workType", description = "作业类型:对应枚举WorkTypeEnum")
    @NotNull(message = "作业类型不能为空")
    private Integer workType;

    @ApiDocClassDefine(value = "soNo", description = "服务单号")
    @NotBlank(message = "服务单号不能为空")
    private String soNo;

    @ApiDocClassDefine(value = "superTicketNo", description = "超级工单�?)
    @NotBlank(message = "超级工单号不能为�?)
    private String superTicketNo;

    @ApiDocClassDefine(value = "idempotentId", description = "幂等ID")
    @NotBlank(message = "幂等ID不能为空")
    private String idempotentId;

    @ApiDocClassDefine(value = "contactName", description = "联系人密�?)
    @NotBlank(message = "联系人密文不能为�?)
    private String contactName;

    @ApiDocClassDefine(value = "contactTel", description = "联系人手机密�?)
    @NotBlank(message = "联系人手机密文不能为�?)
    private String contactTel;

    @ApiDocClassDefine(value = "contactTitle", description = "联系人尊�?)
    @NotNull(message = "联系人尊称不能为�?)
    private Integer contactTitle;

    @ApiDocClassDefine(value = "testTag", description = "测试标识, 0-非测试环�? 1-是测试环�?)
    @NotNull(message = "测试标识不能为空")
    private Integer testTag;

    @ApiDocClassDefine(value = "createMid", description = "创建人mid")
    @NotNull(message = "创建人mid不能为空")
    private Long createMid;

    @ApiDocClassDefine(value = "expand")
    @NotNull
    private RetailComplaintOrderCreateExpandDTO expand;
}
