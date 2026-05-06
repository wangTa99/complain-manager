package com.wt.complaint.manage.app.convert;

import cn.hutool.core.convert.Convert;
import com.wt.commons.utils.StringUtils;
import com.wt.complaint.manage.api.model.constont.DateConstant;
import com.wt.complaint.manage.api.model.req.deliver.DeliverComplaintListReq;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.DeliverComplaintListGoIn;
import com.wt.complaint.manage.domain.utils.KeyCenterUtil;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author zhangzheyang
 * @date 2024/12/24
 */
@Mapper
public interface DeliverComplaintConvert {

    DeliverComplaintConvert INSTANCE = Mappers.getMapper(DeliverComplaintConvert.class);

    default DeliverComplaintListGoIn toListGoIn(DeliverComplaintListReq req) {
        DeliverComplaintListGoIn goIn = Convert.convert(DeliverComplaintListGoIn.class, req);
        goIn.setOffset((req.getPageNum() - 1) * req.getPageSize());
        if (StringUtils.isNotEmpty(req.getOrgIds())) {
            goIn.setOrgIds(Arrays.asList(req.getOrgIds().split(",")));
        }
        // 电话号码使用MD5加密匹配
        if (StringUtils.isNotEmpty(goIn.getContactPhone())){
            goIn.setContactPhoneMd5(KeyCenterUtil.md5(goIn.getContactPhone()));
        }
        // 联系人名字使用MD5加密匹配
        if (StringUtils.isNotEmpty(goIn.getContactName())){
            goIn.setContactNameMd5(KeyCenterUtil.md5(goIn.getContactName()));
        }
        setTimeRange(req.getCreateTime(), goIn::setCreateTimeStart, goIn::setCreateTimeEnd);
        setTimeRange(req.getRealFirstResponseTime(), goIn::setRealFirstResponseTimeStart, goIn::setRealFirstResponseTimeEnd);
        setTimeRange(req.getRealFinishTime(), goIn::setRealFinishTimeStart, goIn::setRealFinishTimeEnd);

        return goIn;
    }

    default void setTimeRange(List<String> time, Consumer<String> setStart, Consumer<String> setEnd){
        if (time != null && time.size() == 2) {
            if (time.get(0) != null) {
                setStart.accept(time.get(0) + DateConstant.DATETIME_START);
            }
            if (time.get(1) != null) {
                setEnd.accept(time.get(1) + DateConstant.DATETIME_END);
            }
        }
    }


}
