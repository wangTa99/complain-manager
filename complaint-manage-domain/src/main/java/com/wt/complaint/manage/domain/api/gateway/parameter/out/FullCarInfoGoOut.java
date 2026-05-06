package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;

/**
 * @author zhangzheyang
 * @date 2024/12/23
 */
public class FullCarInfoGoOut {
    /**
     * 车牌�?
     */
    private String carNo;

    /**
     * 车辆图片
     */
    private String carImg;

    /**
     * 车型
     */
    private String carType;

    private String vin;

    /**
     * 车主miID
     */
    private Long ownerMiId;

    /**
     * 车主名称
     */
    private String ownerName;

    /**
     * 车主电话
     */
    private String ownerTel;

    /**
     * 车主邮箱
     */
    private String ownerEmail;

    /**
     * 软件版本
     */
    private String currentVersion;
}
