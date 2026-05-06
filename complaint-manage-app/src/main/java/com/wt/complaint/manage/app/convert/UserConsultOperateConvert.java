package com.wt.complaint.manage.app.convert;

import com.wt.complaint.manage.api.model.Attachment;
import com.wt.complaint.manage.api.model.req.FollowRecordReq;
import com.wt.complaint.manage.api.model.req.consult.*;
import com.wt.complaint.manage.api.model.req.operate.*;
import com.wt.complaint.manage.api.model.resp.operate.*;
import com.wt.complaint.manage.domain.api.service.parameter.in.*;
import com.wt.complaint.manage.domain.api.service.parameter.out.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.factory.Mappers.getMapper;

import java.util.List;

/**
 * 咨询单操作转换器
 */
@Mapper
public interface UserConsultOperateConvert {
    UserConsultOperateConvert INSTANCE = getMapper(UserConsultOperateConvert.class);

    @Mapping(target = "expandSoIn", source = "expand")
    CreateConsultOrderSoIn toCreateConsultSoIn(CreateConsultReq source);

    ConsultCreateExpandSoIn toExpandSoIn(ConsultCreateExpandDTO source);

    TemplateStructSoIn toStructSoIn(TemplateStructInfo source);

    List<TemplateStructSoIn> toStructSoIn(List<TemplateStructInfo> source);

    @Mapping(target = "attachmentList", source = "attachments")
    TemplateFieldSoIn toFieldSoIn(TemplateField source);

    List<TemplateFieldSoIn> toFieldSoIn(List<TemplateField> source);

    FieldValueSoIn toFieldValueSoIn(FieldValue source);

    List<FieldValueSoIn> toFieldValueSoIn(List<FieldValue> source);

    @Mapping(target = "expandSoIn", source = "expand")
    OrderEditConsultSoIn toEditConsultSoIn(EditConsultReq source);

    ConsultOrderPickUpSoIn toPickUpSoIn(PickUpOrderReq source);

    OrderAddFollowUpRecordSoIn toFollowRecordSoIn(FollowRecordReq source);

    @Mapping(target = "result", source = "result")
    EditComplaintResp toEditConsultResp(OrderEditConsultSoOut source);

    PickUpOrderResp toPickUpResp(ConsultOrderPickUpSoOut source);

    AddFollowRecordResp toFollowRecordResp(OrderFollowUpRecordSoOut source);

    ChangeOrgResp toOrgChangeResp(ConsultOrgChangeApplySoOut source);

    ConsultReassignSoIn toReassignSoIn(ConsultReassignReq source);

    ConsultOrgChangeApplySoIn toOrgChangeApplySoIn(ConsultOrgChangeApplyReq source);

    ConsultFinishSoIn toFinishSoIn(ConsultFinishReq source);

    @Mapping(target = "operatorMid", source = "handlerMid")
    ConsultUpdateHandlerSoIn toUpdateHandlerSoIn(UpdateHandlerReq source);

    AttachmentSoIn toAttachmentSoIn(Attachment source);

    List<AttachmentSoIn> toAttachmentSoIn(List<Attachment> source);
}
