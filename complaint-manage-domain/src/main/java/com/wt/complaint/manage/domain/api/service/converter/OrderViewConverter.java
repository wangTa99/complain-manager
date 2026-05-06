package com.wt.complaint.manage.domain.api.service.converter;

import com.google.gson.reflect.TypeToken;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.RecordInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.UserComplaintOrderMainGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.AttachmentSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintOrderCreateExpandSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.TemplateFieldSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.TemplateStructSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.*;
import com.wt.complaint.manage.domain.model.UserComplaintOrderInfo;
import com.wt.nr.common.utils.GsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangzheyang
 * @date 2024/12/23
 */
@Mapper
public interface OrderViewConverter {

    OrderViewConverter INSTANCE = Mappers.getMapper(OrderViewConverter.class);

    @Mapping(source = "createTime", target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "operatorMid", target = "operatorId")
    @Mapping(source = "superTicketNo", target = "stNo")
    SimpleComplaintDetailSoOut.ComplaintInfoGoOut toComplaintInfoGoOut(ComplaintOrderGoOut source);

    List<TemplateStructSoOut> toTemplateStructSoOut(List<TemplateStructSoIn> source);

    TemplateStructSoOut toTemplateStructSoOut(TemplateStructSoIn source);

    @Mapping(target = "attachments", source = "attachmentList")
    DetailFieldSoOut toDetailFieldSoOut(TemplateFieldSoIn source);

    AttachmentSoOut toAttachmentSoOut(AttachmentSoIn source);

    RecordInfoSoOut toRecordInfoSoOut(RecordInfoGoIn source);

    @Mapping(target = "ucOrderViewInfoList", source = "userComplaintOrderInfoList")
    UcOrderBatchInfoSoOut toUcOrderBatchInfoSoOut(UserComplaintOrderMainGoOut source);

    @Mapping(target = "handleMid", source = "operatorMid")
    @Mapping(source = "createTime", target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "finishTime", target = "finishTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "serviceScene", source = "complaintContent", qualifiedByName = "toServiceScene")
    @Mapping(target ="userComplaintDetailInfos", source = "complaintContent", qualifiedByName = "toUserComplaintDetailInfo")
    UserComplaintDetailSoOut toUserComplaintOrderInfo(UserComplaintOrderInfo source);

    @Named("toServiceScene")
    default String toServiceScene(String content) {
        List<TemplateStructSoIn> ucContentStructList = new ArrayList<>();
        if (StringUtils.isNotBlank(content)) {
            ucContentStructList = GsonUtil.fromJson(content, new TypeToken<List<TemplateStructSoIn>>() {
            }.getType());
        }
        ComplaintOrderCreateExpandSoIn complaintOrderCreateExpandSoIn = new ComplaintOrderCreateExpandSoIn();
        complaintOrderCreateExpandSoIn.setComplaintInfo(ucContentStructList);
        Object fieldsValue = complaintOrderCreateExpandSoIn.getFieldsValue("serviceScene");
        if (fieldsValue == null) {
            return "";
        }
        return fieldsValue.toString();
    }

    @Named("toUserComplaintDetailInfo")
    default List<TemplateStructSoOut> toUserComplaintDetailInfo(String complaintContent) {
        List<TemplateStructSoIn> soIn = GsonUtil.fromJson(complaintContent, new TypeToken<List<TemplateStructSoIn>>() {
        }.getType());
        if (soIn == null) {
            return new ArrayList<>();
        }
        List<TemplateStructSoOut> soOut = new ArrayList<>();
        for (TemplateStructSoIn templateStructSoIn : soIn) {
            soOut.add(toTemplateStructSoOut(templateStructSoIn));
        }
        return soOut;
    }
}
