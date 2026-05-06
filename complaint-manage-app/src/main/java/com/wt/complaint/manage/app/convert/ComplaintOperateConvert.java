package com.wt.complaint.manage.app.convert;

import com.wt.complaint.manage.api.model.Attachment;
import com.wt.complaint.manage.api.model.req.AddKindPointsDistributionRecordReq;
import com.wt.complaint.manage.api.model.req.FollowRecordReq;
import com.wt.complaint.manage.api.model.req.FollowRecordReqV2;
import com.wt.complaint.manage.api.model.req.operate.*;
import com.wt.complaint.manage.api.model.resp.operate.*;
import com.wt.complaint.manage.domain.api.service.parameter.in.*;
import com.wt.complaint.manage.domain.api.service.parameter.out.*;
import com.wt.complaint.manage.domain.api.service.parameter.out.operate.SubmitReviewSoOut;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ComplaintOperateConvert {
    ComplaintOperateConvert INSTANCE = Mappers.getMapper(ComplaintOperateConvert.class);

    @Mapping(target = "expandSoIn", source = "expand")
    @Mapping(target = "createSource", source = "source")
    ComplaintOrderCreateSoIn toSoIn(CreateComplaintOrderReq source);

    OrderAddDistributionRecordSoIn toSoIn(AddKindPointsDistributionRecordReq source);

    @Mapping(target = "complaintInfo", source = "complaintInfo")
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

    OrderUpdateHandlerSoIn toSoIn(UpdateHandlerReq source);

    OrderAddFollowUpRecordSoIn toSoIn(FollowRecordReq source);

    OrderAddFollowUpRecordSoInV2 toSoIn(FollowRecordReqV2 source);

    OrderRemindSoIn toSoIn(RemindOrderReq source);

    @Mapping(target = "orderUpdateCustomerServiceInfos", source = "customerServiceReqList")
    OrderUpdateCustomerServiceSoIn toSoIn(UpdateCustomerServiceReq source);

    OrderUpdateCustomerServiceInfo toCSUpdate(CustomerServiceReq source);

    List<OrderUpdateCustomerServiceInfo> toCSUpdate(List<CustomerServiceReq> source);

    CreateComplaintOrderResp toResp(ComplaintOrderCreateSoOut source);

    AddDistributionRecordResp toResp(OrderAddDistributionRecordSoOut source);

    PickUpOrderResp toResp(OrderPickUpSoOut source);

    UpdateHandlerResp toResp(OrderUpdateHandlerSoOut source);

    @Mapping(target = "result", source = "recordResult")
    AddFollowRecordResp toResp(OrderFollowUpRecordSoOut source);

    RemindOrderResp toResp(OrderRemindSoOut source);

    UpdateCustomerServiceResp toResp(OrderUpdateCustomerServiceSoOut source);

    ComplaintOrderUpgradeSoIn toSoIn(ComplaintOrderUpgradeReq req);

    OrderEditComplaintSoIn toSoIn(EditComplaintReq req);

    SubmitReviewSoIn toSoIn(SubmitReviewReq req);

    EditComplaintResp toResp(OrderEditComplaintSoOut source);

    SubmitReviewResp toResp(SubmitReviewSoOut source);

}
