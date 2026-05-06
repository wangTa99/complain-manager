package com.wt.complaint.manage.domain.constant;

import com.google.common.collect.Sets;
import com.wt.proretail.newcommon.account.ProretailRoleEnum;

import java.util.HashSet;

/**
 * @author zhengziwei
 * @date 2023/6/14 11:33 上午
 */
public class MrRoleConstant {
    /**
     * 店长
     */
    public static final String CAR_ORG_MANAGER = ProretailRoleEnum.CAR_ORG_MANAGER.getKey();

    /**
     * 服务代表
     */
    public static final String RECEIVER = ProretailRoleEnum.CAR_SERVICE_REPRESENTATIVE.getKey();

    /**
     * 服务顾问主管
     */
    public static final String RECEIVER_MANAGER = ProretailRoleEnum.CAR_SERVICE_MANAGER.getKey();

    /**
     * 技�?
     */
    public static final String TECHNICIAN = ProretailRoleEnum.CAR_TECHNICIAN.getKey();

    /**
     * 技术主�?
     */
    public static final String TECHNICIAN_MANAGER = ProretailRoleEnum.CAR_TECHNICAL_LEADER.getKey();

    /**
     * 门店库管
     */
    public static final String STORE_KEEPER = ProretailRoleEnum.CAR_STORE_KEEPER.getKey();

    /**
     * 质量专员
     */
    public static final String QUALITY_OFFICER = ProretailRoleEnum.CAR_QUALITY_OFFICER.getKey();

    /**
     * 品牌派驻代表
     */
    public static final String CAR_BRAND_REPRESENTATIVE = ProretailRoleEnum.CAR_BRAND_REPRESENTATIVE.getKey();

    /**
     * 区域技术支�?
     */
    public static final String REGION_TECHNICAL_SUPPORT = "region_technical_support";

    /**
     * 区域技术支�?
     */
    public static final String REGION_TECHNICAL_SUPPORT_UPC = "5";

    /**
     * 服务运营
     */
    public static final String SERVICE_OPERATE = "service_operate";

    /**
     * 索赔专员
     */
    public static final String CLAIMANT_OFFICER = ProretailRoleEnum.CAR_CLAIMANT_OFFICER.getKey();

    /**
     * 售后工作台产�?
     */
    public static final String PROGRAMMER = "programmer";

    /**
     * 交付工作台产�?
     */
    public static final String DELIVER_PROGRAMMER = "dev";

    /**
     * 区域运营管理
     */
    public static final String REGION_OPERATIONS_MANAGEMENT = "region_operations_management";

    /**
     * 超管
     */
    public static final String SUPER_ADMIN = "super_admin";

}
