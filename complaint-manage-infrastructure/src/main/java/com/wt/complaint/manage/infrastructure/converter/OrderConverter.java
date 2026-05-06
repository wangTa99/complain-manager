package com.wt.complaint.manage.infrastructure.converter;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.OrderListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintListSearchInfo;
import com.wt.complaint.manage.domain.api.service.parameter.out.deliver.DeliverComplaintListGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.RetailComplaintListGoOut;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.infrastructure.model.ComplaintOrderDO;
import com.wt.complaint.manage.infrastructure.model.DeliverComplaintDO;
import com.wt.complaint.manage.infrastructure.model.RetailComplaintDO;
import com.wt.complaint.manage.infrastructure.model.param.OrderListParam;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import java.util.Date;
import java.util.List;

import static com.wt.complaint.manage.domain.exception.ErrorCodeEnums.INVALID_NUMBER_FORMAT;

@Mapper
public interface OrderConverter {
    OrderConverter INSTANCE = Mappers.getMapper(OrderConverter.class);

    ComplaintOrderDO toOrderDO(ComplaintOrderInfoGoIn source);

    List<ComplaintOrderDO> toOrderDO(List<ComplaintOrderInfoGoIn> source);

    @Mappings({@Mapping(target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss"), @Mapping(target = "firstResponseTime", dateFormat = "yyyy-MM-dd HH:mm:ss"), @Mapping(target = "finishTime", dateFormat = "yyyy-MM-dd HH:mm:ss"), @Mapping(target = "upgradeTime", dateFormat = "yyyy-MM-dd HH:mm:ss")

    })
    ComplaintListSearchInfo toSearchInfo(ComplaintOrderDO source);

    List<ComplaintListSearchInfo> toSearchInfoList(List<ComplaintOrderDO> source);

    OrderListParam toOrderListParam(OrderListGoIn source);

    ComplaintOrderInfoGoIn toOrderInfoGoIn(ComplaintOrderDO source);

    List<ComplaintOrderInfoGoIn> toOrderInfoGoIn(List<ComplaintOrderDO> source);

    @Mapping(source = "vinSufix", target = "vinSufix", qualifiedByName = "stringToInt")
    ComplaintOrderGoOut toGoOut(ComplaintOrderDO source);

    List<ComplaintOrderGoOut> toGoOutList(List<ComplaintOrderDO> source);

    List<DeliverComplaintListGoOut> deliverToComplaintGoOutList(List<DeliverComplaintDO> source);

    @Mapping(source = "lastReminderTime", target = "lastReminderTime", qualifiedByName = "dateToLongMillis")
    @Mapping(source = "createTime", target = "createTime", qualifiedByName = "dateToLongMillis")
    @Mapping(source = "expectedFirstResponseTime", target = "expectedFirstResponseTime", qualifiedByName = "dateToLongMillis")
    @Mapping(source = "realFirstResponseTime", target = "realFirstResponseTime", qualifiedByName = "dateToLongMillis")
    @Mapping(source = "expectedFinishTime", target = "expectedFinishTime", qualifiedByName = "dateToLongMillis")
    @Mapping(source = "realFinishTime", target = "realFinishTime", qualifiedByName = "dateToLongMillis")
    DeliverComplaintListGoOut deliverToComplaintGoOut(DeliverComplaintDO source);

    List<RetailComplaintListGoOut> retailToComplaintGoOutList(List<RetailComplaintDO> source);

    @Named("stringToInt")
    default Integer stringToInt(String source) {
        if (StringUtils.isBlank(source)) {
            return 0;
        }
        try {
            return Integer.parseInt(source);
        } catch (NumberFormatException e) {
            throw new BusinessException(INVALID_NUMBER_FORMAT, "vin�?位非数字格式");
        }
    }

    @Named("dateToLongMillis")
    default Long dateToLongMillis(Date date) {
        return date == null ? 0L : date.getTime();
    }

}
