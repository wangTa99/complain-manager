package com.wt.complaint.manage.app.convert;

import com.wt.complaint.manage.api.model.resp.UcOrderViewInfo;
import com.wt.complaint.manage.api.model.resp.view.UcOrderInfoBatchResp;
import com.wt.complaint.manage.domain.api.service.parameter.out.UcOrderBatchInfoSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintDetailSoOut;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 客诉类单据视图转换器
 * @author linjiehong
 * @date 2025/5/29 18:58
 */
@Mapper
public interface UserComplaintViewConvert {
    UserComplaintViewConvert INSTANCE = Mappers.getMapper(UserComplaintViewConvert.class);

    @Mapping(target = "ucOrderViewInfoList", source = "ucOrderViewInfoList")
    UcOrderInfoBatchResp toUcOrderInfoBatchResp(UcOrderBatchInfoSoOut source);

    @Mapping(target="serviceSceneList", source="serviceScene")
    UcOrderViewInfo toUcOrderViewInfo(UserComplaintDetailSoOut source);
}
