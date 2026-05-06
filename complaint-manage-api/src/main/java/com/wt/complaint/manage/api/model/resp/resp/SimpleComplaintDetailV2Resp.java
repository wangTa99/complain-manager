package com.wt.complaint.manage.api.model.resp;

import com.wt.car.common.privacy.annotation.MaskAndEncrypted;
import com.wt.car.common.privacy.enums.MaskTypeEnum;
import com.wt.car.common.privacy.vo.BaseVO;
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
public class SimpleComplaintDetailV2Resp implements Serializable {

    private static final long serialVersionUID = 3731148895619221800L;

    @ApiDocClassDefine(value = "carInfo", description = "车辆信息")
    private CarInfo carInfo;

    @ApiDocClassDefine(value = "complaintInfo", description = "投诉单信�?)
    private SimpleComplaintDetailResp.ComplaintInfo complaintInfo;


    @Data
    public static class CarInfo extends BaseVO {

        private static final long serialVersionUID = -2833928901990225109L;

        @ApiDocClassDefine(value = "carNo", description = "车牌�?)
        private String carNo;

        @ApiDocClassDefine(value = "carImg", description = "车辆图片")
        private String carImg;

        @ApiDocClassDefine(value = "carType", description = "车型")
        private String carType;

        @MaskAndEncrypted(maskType = MaskTypeEnum.VIN)
        @ApiDocClassDefine(value = "vin", description = "vin")
        private String vin;

        @ApiDocClassDefine(value = "vid", description = "vid")
        private String vid;

        @MaskAndEncrypted(maskType = MaskTypeEnum.MID)
        @ApiDocClassDefine(value = "ownerMiId", description = "车主miID")
        private String ownerMiId;

        @MaskAndEncrypted(maskType = MaskTypeEnum.NAME)
        @ApiDocClassDefine(value = "ownerName", description = "车主名称")
        private String ownerName;

        @MaskAndEncrypted(maskType = MaskTypeEnum.PHONE)
        @ApiDocClassDefine(value = "ownerTel", description = "车主电话")
        private String ownerTel;

        @ApiDocClassDefine(value = "currentVersion", description = "软件版本�?)
        private String currentVersion;
    }
}
