package com.wt.complaint.manage.app.convert;

import com.wt.complaint.manage.api.model.Attachment;
import com.wt.complaint.manage.api.model.req.FollowRecordReq;
import com.wt.complaint.manage.api.model.req.operate.ComplaintOrderCreateExpandDTO;
import com.wt.complaint.manage.api.model.req.operate.CreateOrderReq;
import com.wt.complaint.manage.api.model.req.operate.FieldValue;
import com.wt.complaint.manage.api.model.req.operate.JudgeOrderReq;
import com.wt.complaint.manage.api.model.req.operate.PickUpOrderReq;
import com.wt.complaint.manage.api.model.req.operate.RemindOrderReq;
import com.wt.complaint.manage.api.model.req.operate.TemplateField;
import com.wt.complaint.manage.api.model.req.operate.TemplateStructInfo;
import com.wt.complaint.manage.api.model.resp.operate.AddFollowRecordResp;
import com.wt.complaint.manage.api.model.resp.operate.JudgeOrderResp;
import com.wt.complaint.manage.api.model.resp.operate.RemindOrderResp;
import com.wt.complaint.manage.domain.api.service.parameter.in.AttachmentSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintOrderCreateExpandSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.FieldValueSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.OrderAddFollowUpRecordSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.OrderPickUpSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.OrderRemindSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.TemplateFieldSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.TemplateStructSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.opetate.CreateOrderSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.opetate.JudgeOrderSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.OrderFollowUpRecordSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.OrderRemindSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.operate.JudgeOrderSoOut;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import static org.mapstruct.factory.Mappers.getMapper;

import java.util.List;

/**
 * @author linjiehong
 * @date 2025/5/22 17:14
 */
@Mapper
public interface UserComplaintOperateConvert {
    UserComplaintOperateConvert INSTANCE = getMapper(UserComplaintOperateConvert.class);

    @Mapping(target = "expandSoIn", source = "expand")
    CreateOrderSoIn toSoIn(CreateOrderReq source);

    ComplaintOrderCreateExpandSoIn toExpandSoIn(ComplaintOrderCreateExpandDTO source);

    TemplateStructSoIn toStructSoIn(TemplateStructInfo source);

    List<TemplateStructSoIn> toStructSoIn(List<TemplateStructInfo> source);

    @Mapping(target = "attachmentList", source = "attachments")
    TemplateFieldSoIn toFieldSoIn(TemplateField source);

    List<TemplateFieldSoIn> toFieldSoIn(List<TemplateField> source);

    FieldValueSoIn toFieldValueSoIn(FieldValue source);

    List<FieldValueSoIn> toFieldValueSoIn(List<FieldValue> source);

    AttachmentSoIn toAttachmentSoIn(Attachment source);

    List<AttachmentSoIn> toAttachmentSoIn(List<Attachment> source);

    OrderPickUpSoIn toSoIn(PickUpOrderReq source);

    OrderRemindSoIn toSoIn(RemindOrderReq source);

    OrderAddFollowUpRecordSoIn toSoIn(FollowRecordReq source);

    @Mapping(target = "judgeContent", source = "judgeReason")
    JudgeOrderSoIn toSoIn(JudgeOrderReq source);

    @Mapping(target = "result", source = "remindResult")
    RemindOrderResp toResp(OrderRemindSoOut source);

    JudgeOrderResp toResp(JudgeOrderSoOut source);

    AddFollowRecordResp toResp(OrderFollowUpRecordSoOut source);
}
