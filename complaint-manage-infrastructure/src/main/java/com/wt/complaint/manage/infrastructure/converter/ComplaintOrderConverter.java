package com.wt.complaint.manage.infrastructure.converter;

import com.wt.complaint.manage.api.model.resp.UserComplaintDetailFrameResp;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintDetailFrameSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintListSearchInfo;
import com.wt.complaint.manage.domain.bo.DeliverComplaintBO;
import com.wt.complaint.manage.infrastructure.model.UserComplaintOrderDetailDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ComplaintOrderConverter {
    ComplaintOrderConverter INSTANCE = Mappers.getMapper(ComplaintOrderConverter.class);

    @Mappings({@Mapping(target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss"),
            @Mapping(target = "finishTime", dateFormat = "yyyy-MM-dd HH:mm:ss")

    })
    UserComplaintListSearchInfo toSearchInfo(UserComplaintOrderDetailDO source);

    List<UserComplaintListSearchInfo> toSearchInfoList(List<UserComplaintOrderDetailDO> source);

    UserComplaintDetailFrameResp convertToResp(UserComplaintDetailFrameSoOut source);
}
