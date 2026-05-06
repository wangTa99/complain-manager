package com.wt.complaint.manage.app.convert;

import com.wt.complaint.manage.api.model.req.ComplaintDetailBatchReq;
import com.wt.complaint.manage.api.model.req.ComplaintDetailFrameReq;
import com.wt.complaint.manage.api.model.req.ComplaintDetailReq;
import com.wt.complaint.manage.api.model.req.ComplaintFollowUpRecordsReq;
import com.wt.complaint.manage.api.model.req.ComplaintHandlerListReq;
import com.wt.complaint.manage.api.model.req.ComplaintListSearchReq;
import com.wt.complaint.manage.api.model.req.operate.FieldValue;
import com.wt.complaint.manage.api.model.resp.ComplaintDetailBatchResp;
import com.wt.complaint.manage.api.model.resp.ComplaintDetailFrameResp;
import com.wt.complaint.manage.api.model.resp.ComplaintDetailResp;
import com.wt.complaint.manage.api.model.resp.ComplaintEditDetailResp;
import com.wt.complaint.manage.api.model.resp.ComplaintFollowUpRecordsResp;
import com.wt.complaint.manage.api.model.resp.ComplaintHandlerInfo;
import com.wt.complaint.manage.api.model.resp.ComplaintHandlerListResp;
import com.wt.complaint.manage.api.model.resp.ComplaintListSearchResp;
import com.wt.complaint.manage.api.model.resp.CountComplaintListTabResp;
import com.wt.complaint.manage.api.model.resp.SimpleComplaintDetailResp;
import com.wt.complaint.manage.api.model.resp.SimpleComplaintDetailV2Resp;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintBatchDetailSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintDetailSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintFrameInfoSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintListSearchGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintProcessSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.FieldValueSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.GetComplaintHandlerSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintBatchDetailSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintDetailSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintEditDetailSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintFrameInfoSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintListSearchSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintProcessListSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.CountComplaintListTabSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.GetComplaintHandlerInfo;
import com.wt.complaint.manage.domain.api.service.parameter.out.GetComplaintHandlerSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.SimpleComplaintDetailSoOut;
import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mapper
public interface ComplaintViewConvert {

    ComplaintViewConvert INSTANCE = Mappers.getMapper(ComplaintViewConvert.class);

    @Mapping(source = "cityList", target = "cityList", qualifiedByName = "cityListToString")
    ComplaintListSearchGoIn convertToGoIn(ComplaintListSearchReq source);

    ComplaintFrameInfoSoIn convertToSoIn(ComplaintDetailFrameReq source);

    ComplaintDetailSoIn convertToSoIn(ComplaintDetailReq source);

    ComplaintProcessSoIn convertToSoIn(ComplaintFollowUpRecordsReq source);

    GetComplaintHandlerSoIn convertToSoIn(ComplaintHandlerListReq source);

    ComplaintBatchDetailSoIn convertToSoIn(ComplaintDetailBatchReq source);

    ComplaintListSearchResp convertToResp(ComplaintListSearchSoOut source);

    ComplaintDetailResp convertToResp(ComplaintDetailSoOut source);

    @Mapping(target = "complaint", source = "complaint", qualifiedByName = "fieldValueSoInToFieldValue")
    ComplaintEditDetailResp convertToResp(ComplaintEditDetailSoOut source);

    @Named("fieldValueSoInToFieldValue")
    default FieldValue fieldValueSoInToFieldValue(FieldValueSoIn source) {
        if (source == null) {
            return null;
        }
        return FieldValue.builder()
                .code(source.getCode())
                .desc(source.getDesc())
                .pathId(source.getPathId())
                .pathName(source.getPathName())
                .build();
    }

    CountComplaintListTabResp convertToResp(CountComplaintListTabSoOut source);

    SimpleComplaintDetailResp convertToResp(SimpleComplaintDetailSoOut source);

    SimpleComplaintDetailV2Resp convertToRespV2(SimpleComplaintDetailSoOut source);

    ComplaintDetailFrameResp convertToResp(ComplaintFrameInfoSoOut source);

    @Mapping(target = "records", source = "processList")
    ComplaintFollowUpRecordsResp convertToResp(ComplaintProcessListSoOut source);

    @Mapping(target = "handlerList", source = "handlerInfoList")
    ComplaintHandlerListResp convertToResp(GetComplaintHandlerSoOut source);

    @Mapping(target = "complaintDetailRespList", source = "detailSoOutList")
    ComplaintDetailBatchResp convertToResp(ComplaintBatchDetailSoOut source);

    ComplaintHandlerInfo convertToHandler(GetComplaintHandlerInfo source);

    List<ComplaintHandlerInfo> convertToHandler(List<GetComplaintHandlerInfo> source);

    SimpleComplaintDetailResp.CarInfo toCarInfo(SimpleComplaintDetailSoOut.CarInfoSoOut source);

    @Mapping(source = "riskLevel", target = "riskLevel", qualifiedByName = "riskLevelToStr")
    SimpleComplaintDetailResp.ComplaintInfo toComplaintInfo(SimpleComplaintDetailSoOut.ComplaintInfoGoOut source);

    @Named("riskLevelToStr")
    default String riskLevelToStr(Integer riskLevel) {
        if (riskLevel == null) {
            return "";
        }
        return "L" + riskLevel;
    }

    @Named("cityListToString")
    default List<String> cityListToString(List<Integer> cityList) {
        if (CollectionUtils.isEmpty(cityList)) {
            return Collections.emptyList();
        }
        return cityList.stream()
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.toList());
    }
}
