package com.wt.complaint.manage.app.convert;

import com.wt.complaint.manage.api.model.req.consult.ConsultDetailReq;
import com.wt.complaint.manage.api.model.req.consult.ConsultListReq;

import com.wt.complaint.manage.api.model.req.consult.StatisticsItemReq;
import com.wt.complaint.manage.api.model.resp.consult.ConsultDetailResp;
import com.wt.complaint.manage.api.model.resp.consult.ConsultListDTO;
import com.wt.complaint.manage.api.model.resp.consult.ConsultListResp;
import com.wt.complaint.manage.api.model.resp.consult.ConsultStatisticsItemResp;
import com.wt.complaint.manage.domain.api.service.parameter.in.ConsultDetailSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ConsultListSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ConsultStatisticsSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.ConsultDetailSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.ConsultListItemSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.ConsultListSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.ConsultStatisticsSoOut;
import com.wt.complaint.manage.domain.utils.DateUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Objects;
import java.util.Date;

/**
 * 咨询单对象转换器
 */
@Mapper
public interface ConsultConvert {

    ConsultConvert INSTANCE = Mappers.getMapper(ConsultConvert.class);

    ConsultListSoIn toSoIn(ConsultListReq source);

    ConsultStatisticsSoIn toSoIn(StatisticsItemReq source);

    ConsultDetailSoIn toSoIn(ConsultDetailReq source);

    ConsultStatisticsItemResp toResp(ConsultStatisticsSoOut source);


    ConsultListResp toResp(ConsultListSoOut source);

    List<ConsultStatisticsItemResp> toResp(List<ConsultStatisticsSoOut> soOut);
    @Mapping(target = "expectedCallbackTime", source = "expectedCallbackTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "createTime", target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    ConsultListDTO toDTO(ConsultListItemSoOut source);

    ConsultDetailResp toResp(ConsultDetailSoOut source);

    /**
     * 日期转字符串(分钟�?
     *
     * @param source 日期
     * @return 字符�?
     */
    @Named("dateToMinuteStr")
    default String dateToMinuteStr(Date source) {
        if (Objects.isNull(source)) {
            return "";
        }
        try {
            return DateUtil.getTimeStrByTimeStampMS(source.getTime(), "yyyyMMddHHmm");
        } catch (Exception e) {
            return "";
        }
    }


}
