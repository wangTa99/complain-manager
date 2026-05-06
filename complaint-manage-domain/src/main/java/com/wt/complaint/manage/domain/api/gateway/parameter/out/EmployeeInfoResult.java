package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EmployeeInfoResult {
    private Long userId;
    private Long miId;
    private String name;
    private String phone;
    private String idCard;
    private Byte isCardType;
    private Byte isAuth;
    private String email;
    private String emplId;
    private Integer type;
    private Integer userState;
    private Byte isOfficial;
    private List<HeadPosition> headPositions;
    private List<HeadPosition> channelPositions;
    private List<ZonePosition> bigZonePositions;
    private List<ZonePosition> littleZonePositions;
    private List<StorePosition> storePositions;

    @Data
    public static class HeadPosition {
        private Integer userDepartmentPositionId;
        private Integer positionId;
        private Integer positionType;
        private String positionName;
        private Integer departmentId;
        private Date updateTime;
        private Integer privilegeState;
    }

    @Data
    public static class ZonePosition {
        private String zoneName;
        private Integer zoneId;
        private String zoneCode;
        private Integer userDepartmentPositionId;
        private Integer positionId;
        private Integer positionType;
        private String positionName;
        private Integer departmentId;
        private Date updateTime;
        private Integer privilegeState;
    }

    @Data
    public static class StorePosition {
        private String orgId;
        private String storeName;
        private Integer userDepartmentPositionId;
        private Integer positionId;
        private Integer positionType;
        private String positionName;
        private Integer departmentId;
        private Date updateTime;
        private Integer privilegeState;
    }

}
