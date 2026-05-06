package com.wt.complaint.manage.domain.api.service.parameter.out;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleComplaintDetailSoOut implements Serializable {

    private static final long serialVersionUID = 3731148895619221800L;

    @ApiDocClassDefine(value = "carInfo", description = "车辆信息")
    private CarInfoSoOut carInfo;

    @ApiDocClassDefine(value = "complaintInfo", description = "投诉单信�?)
    private ComplaintInfoGoOut complaintInfo;


    @Data
    public static class CarInfoSoOut implements Serializable {

        private static final long serialVersionUID = -2833928901990225109L;

        @ApiDocClassDefine(value = "carNo", description = "车牌�?)
        private String carNo;

        @ApiDocClassDefine(value = "carImg", description = "车辆图片")
        private String carImg;

        @ApiDocClassDefine(value = "carType", description = "车型")
        private String carType;

        @ApiDocClassDefine(value = "vin", description = "vin")
        private String vin;

        @ApiDocClassDefine(value = "vid", description = "vid")
        private String vid;

        @ApiDocClassDefine(value = "ownerMiId", description = "车主miID")
        private Long ownerMiId;

        @ApiDocClassDefine(value = "ownerName", description = "车主名称")
        private String ownerName;

        @ApiDocClassDefine(value = "ownerTel", description = "车主电话")
        private String ownerTel;

        @ApiDocClassDefine(value = "currentVersion", description = "软件版本�?)
        private String currentVersion;
    }

    @Data
    public static class ComplaintInfoGoOut implements Serializable {

        private static final long serialVersionUID = 7407706941443617543L;

        @ApiDocClassDefine(value = "complaintNo", description = "投诉单号")
        private String complaintNo;

        @ApiDocClassDefine(value = "stNo", description = "超级工单�?)
        private String stNo;

        @ApiDocClassDefine(value = "soNo", description = "服务单号")
        private String soNo;

        @ApiDocClassDefine(value = "customerServiceMid", description = "客服mid")
        private Long customerServiceMid;

        @ApiDocClassDefine(value = "customerServiceName", description = "客服姓名")
        private String customerServiceName;

        @ApiDocClassDefine(value = "operatorEmailPrefix", description = "客服的邮箱前缀")
        private String customerServiceEmailPrefix;

        @ApiDocClassDefine(value = "createTime", description = "创建时间, 格式为yyyy-MM-dd HH:mm:ss")
        private String createTime;

        @ApiDocClassDefine(value = "orgName", description = "店铺名称")
        private String orgName;

        @ApiDocClassDefine(value = "orgId", description = "店铺id")
        private String orgId;

        @ApiDocClassDefine(value = "operatorId", description = "操作人id")
        private Long operatorId;

        @ApiDocClassDefine(value = "operatorName", description = "操作人姓�?)
        private String operatorName;

        @ApiDocClassDefine(value = "problemCategory", description = "问题分类")
        private String problemCategory;

        @ApiDocClassDefine(value = "problemDesc", description = "问题描述")
        private String problemDesc;

        @ApiDocClassDefine(value = "userDemand", description = "用户诉求")
        private String userDemand;

        @ApiDocClassDefine(value = "riskLevel", description = "风险级别, 1,2,3,4")
        private Integer riskLevel;
    }

}
