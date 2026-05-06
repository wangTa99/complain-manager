package com.wt.complaint.manage.domain.utils;

import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarEmployee;
import com.wt.complaint.manage.domain.api.service.parameter.out.ConsultDetailSoOut;
import com.wt.complaint.manage.domain.model.UserConsultOrderInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ComplaintConsultUtil {

    private ComplaintConsultUtil() {}

    /**
     * 解析逗号分隔的门店id字符串为列表
     */
    public static List<String> parseOrgIds(String orgIds) {
        if (StringUtils.isBlank(orgIds)) {
            return Collections.emptyList();
        }
        return Arrays.stream(orgIds.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    /**
     * 构造咨询详情对�?
     * @param orderInfo 咨询投诉�?
     * @param creator 创建�?
     * @param followStoreName 跟进门店
     * @param follower 跟进�?
     * @return 咨询详情对象
     */
    public static ConsultDetailSoOut buildConsultDetailSoOut(UserConsultOrderInfo orderInfo,
                                                             CarEmployee creator,
                                                             String followStoreName,
                                                             CarEmployee follower) {
        ConsultDetailSoOut soOut = new ConsultDetailSoOut();
        soOut.setConsultNo(orderInfo.getConsultNo());
        // contactNameC / contactPhoneC 为密文，直接透传（解密由上层或网关解密服务处理）
        soOut.setCustomerName(orderInfo.getContactNameC());
        soOut.setCustomerPhone(orderInfo.getContactPhoneC());
        soOut.setCarNo(orderInfo.getCarNo());
        soOut.setVin(orderInfo.getVid());
        soOut.setConsultType(orderInfo.getConsultType());
        soOut.setContactPerson(orderInfo.getContactNameC());
        soOut.setContactPhone(orderInfo.getContactPhoneC());
        soOut.setCreator(creator != null ? creator.getName() : null);
        soOut.setCreateTime(orderInfo.getCreateTime() != null
                ? new java.text.SimpleDateFormat(DateUtil.DATE_FORMAT_STR_YMDHMS).format(orderInfo.getCreateTime())
                : null);
        soOut.setSuperTicketNo(orderInfo.getSuperTicketNo());
        soOut.setWarrantyServiceStore(null); // 维保单门店需通过superTicketNo查询，暂不实�?
        soOut.setFollowStore(followStoreName);
        soOut.setFollower(follower != null ? follower.getName() : null);
        soOut.setCallbackTime(orderInfo.getExpectingBackTime() != null
                ? new java.text.SimpleDateFormat(DateUtil.DATE_FORMAT_STR_YMDHMS).format(orderInfo.getExpectingBackTime())
                : null);
        soOut.setAppealDesc(orderInfo.getProblemDesc());
        soOut.setAttachmentList(Collections.emptyList());
        return soOut;
    }

    /**
     * 判断是否紧急（priority >= 16 为紧急）
     */
    public static boolean isUrgent(Byte priority) {
        return priority != null && priority.intValue() >= 16;
    }

}
