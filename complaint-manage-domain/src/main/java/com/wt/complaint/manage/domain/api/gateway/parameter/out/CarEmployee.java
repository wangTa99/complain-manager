package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import com.wt.complaint.manage.domain.api.enums.UserStateEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 汽车员工基本信息�?
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class CarEmployee {

    private Long userId;
    private Long miId;
    private String emplId;
    private String name;
    private String phone;
    private Byte idCardType;
    private String idCard;
    private Byte isAuth;
    private Long birthday;
    private Byte sex;
    private String email;
    private Integer privilegeState;
    private Integer positionId;
    private Integer type;
    /**
     * 用户状�?0-无效 1-有效 2-冻结
     */
    private Integer userState;
    private Integer eduLevel;
    private Byte isOfficial;
    private Long hireTime;
    private List<StorePosition> storePositions;
    private List<GridPosition> gridPositions;
    private List<ZonePosition> zonePositions;
    private List<ChannelPosition> channelPositions;
    private List<AreaPosition> areaPositions;
    private List<HeadPosition> headPositions;
    private List<BusinessPosition> carBusinessPositions;
    private List<GroupPosition> groupPositions;
    private List<ClientPosition> clientPositions;

    /**
     * 员工构造函�?
     * @param miId 小米ID
     * @param name 姓名
     * @param positionId 职位ID
     * @param phone 电话
     * @param email 邮箱
     */
    public CarEmployee(Long miId, String name, Integer positionId, String phone, String email) {
        this.miId = miId;
        this.name = name;
        this.positionId = positionId;
        this.phone = phone;
        this.email = email;
    }

    public boolean isAvailable() {
        return UserStateEnum.VALID.getCode().equals(userState);
    }

    /**
     * 门店职位
     */
    @Data
    public static class StorePosition implements Serializable {
        private Integer userDepartmentPositionId;
        private Integer positionId;
        private Integer positionType;
        private String positionName;
        private Integer departmentId;
        private Integer areaId;
        private Integer storeId;
        private String orgId;
        private Integer channelType;
        private Integer positionState;
        private Integer privilegeState;
        private String storeName;
        private Integer siteId;
        private Integer storeType;
        private Integer storeState;
    }

    /**
     * 网格职位
     */
    @Data
    public static class GridPosition implements Serializable {
        private Integer userDepartmentPositionId;
        private Integer positionId;
        private Integer positionType;
        private String positionName;
        private Integer departmentId;
        private Integer areaId;
        private Integer gridId;
        private String gridName;
        private String gridCode;
        private Integer zoneId;
        private String zoneName;
        private String zoneCode;
        private Integer channelType;
        private List<Integer> siteIds;
        private Integer positionState;
        private Integer privilegeState;
        private Integer gridState;
    }

    /**
     * 区域职位
     */
    @Data
    public static class ZonePosition implements Serializable {
        private Integer userDepartmentPositionId;
        private Integer positionId;
        private Integer positionType;
        private String positionName;
        private Integer departmentId;
        private Integer areaId;
        private String zoneName;
        private Integer zoneId;
        private String zoneCode;
        private Integer channelType;
        private List<Integer> siteIds;
        private Integer positionState;
        private Integer privilegeState;
        private Integer zoneState;
    }

    /**
     * 渠道职位
     */
    @Data
    public static class ChannelPosition implements Serializable {
        private Integer userDepartmentPositionId;
        private Integer positionId;
        private Integer positionType;
        private String positionName;
        private Integer departmentId;
        private Integer areaId;
        private String areaCode;
        private String areaName;
        private Integer channelId;
        private Integer channelType;
        private List<Integer> siteIds;
        private Integer positionState;
        private Integer privilegeState;
    }

    /**
     * 大区职位
     */
    @Data
    public static class AreaPosition implements Serializable {
        private Integer userDepartmentPositionId;
        private Integer positionId;
        private Integer positionType;
        private String positionName;
        private Integer departmentId;
        private String areaName;
        private Integer areaId;
        private String areaCode;
        private Integer channelType;
        private List<Integer> siteIds;
        private Integer positionState;
        private Integer privilegeState;
        private Integer areaState;
    }

    /**
     * 总部职位
     */
    @Data
    public static class HeadPosition implements Serializable {
        private Integer userDepartmentPositionId;
        private Integer positionId;
        private Integer positionType;
        private String positionName;
        private Integer departmentId;
        private Integer channelType;
        private List<Integer> siteIds;
        private Integer positionState;
        private Integer privilegeState;
    }

    /**
     * 业务线职�?
     */
    @Data
    public static class BusinessPosition implements Serializable {
        private Integer userDepartmentPositionId;
        private Integer positionId;
        private Integer positionType;
        private String positionName;
        private Long businessId;
        private Integer positionState;
        private Integer privilegeState;
        private Integer channelType;
    }

    /**
     * 群组职位
     */
    @Data
    public static class GroupPosition implements Serializable {
        private Integer userDepartmentPositionId;
        private Integer positionId;
        private Integer positionType;
        private String positionName;
        private Long groupId;
        private String groupName;
        private Integer positionState;
        private Integer privilegeState;
    }

    /**
     * 客户职位
     */
    @Data
    public static class ClientPosition implements Serializable {
        private Integer userDepartmentPositionId;
        private Integer positionId;
        private Integer positionType;
        private String positionName;
        private Long clientId;
        private String clientName;
        private Long groupId;
        private String groupName;
        private Integer positionState;
        private Integer privilegeState;
    }

}
