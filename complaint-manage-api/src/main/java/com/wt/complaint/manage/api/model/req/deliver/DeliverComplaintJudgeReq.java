package com.wt.complaint.manage.api.model.req.deliver;

import com.wt.complaint.manage.api.model.enums.ResponsibleEnum;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Arrays;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
/**
 * 判责请求�?
 *
 * @author huxiankang
 * @date 2025/6/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliverComplaintJudgeReq implements Serializable {

    @ApiDocClassDefine(value = "drNo", description = "客诉单号", required = true)
    @NotBlank(message = "drNo不能为空")
    private String drNo;

    @ApiDocClassDefine(value = "responsible", description = "判责, 1-有责 2-无责", required = true)
    @NotNull(message = "responsible不能为空")
    private Integer responsible;

    @ApiDocClassDefine(value = "responsibleJudgeDesc", description = "判责说明", required = true)
    private String responsibleJudgeDesc;

    /**
     *  入参检�?
     */
    public void check() {
        if (!this.drNo.startsWith("DR")) {
            throw new IllegalArgumentException("非交付客诉单，请联系系统管理�?);
        }
        if (!Arrays.asList(ResponsibleEnum.RESPONSIBLE.getCode(), ResponsibleEnum.NOT_RESPONSIBLE.getCode()).contains(this.responsible)) {
            throw new IllegalArgumentException("交付客诉单判责结论不合法, 请联系管理员");
        }
        if (ResponsibleEnum.RESPONSIBLE.getCode().equals(this.responsible) && StringUtils.isEmpty(this.responsibleJudgeDesc)) {
            throw new IllegalArgumentException("判为有责�? 判责说明不能为空");
        }
    }
}
