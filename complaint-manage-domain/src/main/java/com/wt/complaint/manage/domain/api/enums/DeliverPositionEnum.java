package com.wt.complaint.manage.domain.api.enums;

import com.wt.complaint.manage.api.model.resp.common.CommonOptionResp;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 交付岗位枚举
 * @author huxiankang
 * @date 2025/6/24
 */
@AllArgsConstructor
@Getter
@Slf4j
public enum DeliverPositionEnum {

    // A�?
    POSITION_A(466, 109, "交付邀约专�?, "交付邀约专�?, "JFYYZY"),
    // B�?
    POSITION_B(89, 102, "交付接待专员", "交付接待专员", "JFJDZY"),
    // A岗主�?
    POSITION_A_LEADER(421, 109, "交付邀约主�?, "交付邀约主�?, "XQJFYYZG"),
    // B岗主�?
    POSITION_B_LEADER(88, 102, "交付接待主管", "交付接待主管", "JFJDZG"),
    // 店长
    DELIVERY_CENTER_MANAGER(87, 102, "交付中心店长", "交付中心店长", "JFZXDZ"),
    // 区域邀约经�?
    REGIONAL_INVITE_MANAGER(85, 108, "区域邀约经�?, "区域邀约经�?, "JFYYZG"),
    // 大区�?
    REGIONAL_DELIVERY_HEAD(226, 108, "区域交付负责�?, "区域交付负责�?, "car_regional_delivery"),
    // 总部交付运营
    HEADQUARTERS_DELIVERY_OPERATOR(71, 106, "交付运营", "交付运营", "JFYY");

    /**
     * 中台岗位id
     */
    private final Integer positionId;
    /**
     * 中台岗位类型
     */
    private final Integer positionType;

    /**
     * 新零售中台岗位名�?
     */
    private final String midPositionName;

    /**
     * UPC系统岗位名称
     */
    private final String systemPositionName;

    /**
     * UPC系统岗位key
     */
    private final String systemPositionKey;

    /**
     * LinkedHashMap维护有序的键值对
     * key：组织中台岗位id
     * value：对应交付岗位枚举类
     */
    private static final LinkedHashMap<Integer, DeliverPositionEnum> POSITION_MAPPING;

    /*
     * 有序键值对存放顺序=优先�?
     * 总部运营>大区�?区域邀约经�?A岗主�?店长>A�?B岗主�?B�?
     */
    static {
        POSITION_MAPPING = new LinkedHashMap<>();
        // 总部运营
        POSITION_MAPPING.put(HEADQUARTERS_DELIVERY_OPERATOR.positionId, HEADQUARTERS_DELIVERY_OPERATOR);
        // 大区�?
        POSITION_MAPPING.put(REGIONAL_DELIVERY_HEAD.positionId, REGIONAL_DELIVERY_HEAD);
        // 区域邀约经�?
        POSITION_MAPPING.put(REGIONAL_INVITE_MANAGER.positionId, REGIONAL_INVITE_MANAGER);
        // A岗主�?
        POSITION_MAPPING.put(POSITION_A_LEADER.positionId, POSITION_A_LEADER);
        // 店长
        POSITION_MAPPING.put(DELIVERY_CENTER_MANAGER.positionId, DELIVERY_CENTER_MANAGER);
        // A�?
        POSITION_MAPPING.put(POSITION_A.positionId, POSITION_A);
        // B岗主�?
        POSITION_MAPPING.put(POSITION_B_LEADER.positionId, POSITION_B_LEADER);
        // B�?
        POSITION_MAPPING.put(POSITION_B.positionId, POSITION_B);
    }

    /**
     * 根据岗位id获取岗位描述
     * @param positionId 岗位id
     * @return 岗位描述
     */
    public static String getDescByCode(Integer positionId) {
        for (DeliverPositionEnum value : DeliverPositionEnum.values()) {
            if (Objects.equals(value.getPositionId(), positionId)) {
                return value.getSystemPositionName();
            }
        }
        return null;
    }

    /**
     * 根据组织中台岗位id列表，获取生效岗�?
     * 如果存在多个岗位，返回权限最高的岗位 总部运营>大区�?A岗主�?店长>A�?B岗主�?B�?
     * @param positionIdList 组织中台岗位id列表
     * @return 岗位枚举
     */
    public static DeliverPositionEnum getDeliveryPositionEnum(List<Integer> positionIdList) {
        log.info("DeliverPositionEnum#getDeliveryPositionEnum：positionIdList={}", positionIdList);
        if (positionIdList == null || positionIdList.isEmpty()) {
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "未知身份，无权限");
        }

        for (Map.Entry<Integer, DeliverPositionEnum> entry : POSITION_MAPPING.entrySet()) {
            if (positionIdList.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "未知身份，无权限");
    }

    public static List<CommonOptionResp> getCommonOptionList() {
        return Stream.of(POSITION_A, POSITION_B, POSITION_A_LEADER,POSITION_B_LEADER, REGIONAL_INVITE_MANAGER,DELIVERY_CENTER_MANAGER)
                .map(value ->
                CommonOptionResp.builder()
                        .statusCode(value.getPositionId())
                        .statusName(value.getSystemPositionName())
                        .build()
        ).collect(Collectors.toList());
    }

}
