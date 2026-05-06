package com.wt.complaint.manage.domain.converter;

import com.wt.complaint.manage.api.model.Attachment;
import com.wt.complaint.manage.api.model.enums.ResponsibilityEnum;
import com.wt.complaint.manage.api.model.enums.ResponsibleEnum;
import com.wt.complaint.manage.api.model.resp.ClosingTagDTO;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.RetailComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.RetailComplaintDetaiGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.AttachmentSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintRelationClosingTagSoOut;
import com.wt.complaint.manage.domain.bo.DeliverComplaintBO;
import com.wt.complaint.manage.domain.model.ComplaintBasicInfo;
import com.wt.complaint.manage.domain.model.CreateChatGroupEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author zhangzheyang
 * @date 2024/12/25
 */
@Mapper
public interface DomainConverter {

    DomainConverter INSTANCE = Mappers.getMapper(DomainConverter.class);

    ClosingTagDTO toTagDto(ComplaintRelationClosingTagSoOut source);

    List<ClosingTagDTO> toTagDtoList(List<ComplaintRelationClosingTagSoOut> source);

    AttachmentSoIn toAttachment(Attachment source);

    List<AttachmentSoIn> toAttachmentList(List<Attachment> source);

    @Mapping(target = "vinSufix", source = "vinSufix", qualifiedByName = "stringToInt")
    ComplaintOrderGoOut toGoOut(ComplaintOrderInfoGoIn source);

    @Mapping(target = "vinSufix", source = "vinSufix", qualifiedByName = "intToString")
    ComplaintOrderInfoGoIn toGoIn(ComplaintOrderGoOut source);

    @Mapping(target = "complaintNo", source = "drNo")
    @Mapping(target = "deliverRetailComplaintStatus", source = "orderStatus")
    @Mapping(target = "responsibility", source = "responsible", qualifiedByName = "responsibleToResponsibility")
    @Mapping(target = "finishTime", source = "realFinishTime")
    @Mapping(target = "firstResponseTime", source = "realFirstResponseTime")
    ComplaintOrderInfoGoIn convertToGoIn(DeliverComplaintBO source);

    CreateChatGroupEvent convertToCreateChatGroupEvent(RetailComplaintOrderInfoGoIn source);

    List<ComplaintOrderInfoGoIn> convertToGoInList(List<DeliverComplaintBO> source);

    @Mapping(target = "stNo", source = "superTicketNo")
    ComplaintBasicInfo convertToBasicInfo(RetailComplaintOrderInfoGoIn source);

    @Mapping(target = "stNo", source = "superTicketNo")
    ComplaintBasicInfo convertToBasicInfo(RetailComplaintDetaiGoOut source);

    @Mapping(target = "stNo", source = "superTicketNo")
    ComplaintBasicInfo convertToBasicInfo(DeliverComplaintBO source);


    @Named("responsibleToResponsibility")
    default Integer responsibleToResponsibility(Integer responsible) {
        if (responsible == null) {
            return null;
        }
        ResponsibleEnum responsibleEnum = ResponsibleEnum.getEnumByCode(responsible);
        if (responsibleEnum == null) {
            return null;
        }
        int result;
        if (responsibleEnum == ResponsibleEnum.NOT_RESPONSIBLE) {
            result = ResponsibilityEnum.NO.getCode();
        } else {
            // 其他情况都默认为有责
            result = ResponsibilityEnum.YES.getCode();
        }
        return result;
    }

    @Named("intToString")
    default String intToString(Integer source) {
        return source == null ? null : String.valueOf(source);
    }

    @Named("stringToInt")
    default Integer stringToInt(String source) {
        return StringUtils.isEmpty(source) ? null : NumberUtils.toInt(source);
    }
}
