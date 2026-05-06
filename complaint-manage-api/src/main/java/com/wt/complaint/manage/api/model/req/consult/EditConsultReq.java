package com.wt.complaint.manage.api.model.req.consult;

import com.wt.complaint.manage.api.model.req.operate.ConsultCreateExpandDTO;
import com.wt.complaint.manage.api.model.req.operate.IssueTypeContent;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EditConsultReq implements Serializable {

    private static final long serialVersionUID = 1919699885886279612L;

    @ApiDocClassDefine(value = "consultNo", description = "咨询单号", required = true)
    private String consultNo;


    @ApiDocClassDefine(value = "operatorMid", description = "跟进人mid")
    private Long operatorMid;

    @ApiDocClassDefine(value = "operatorPositionId", description = "跟进人岗位id")
    private Integer operatorPositionId;

    @ApiDocClassDefine(value = "expand", description = "扩展信息")
    private ConsultCreateExpandDTO expand;
}
