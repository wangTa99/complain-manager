package com.wt.complaint.manage.domain.api.service.parameter.in.approve;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintAuditDetailSoIn implements Serializable {

    private static final long serialVersionUID = 1015616565726267145L;

    /**
     * 审批id
     */
    private Long id;

    /**
     * 登录人mid
     */
    private Long mid;

    /**
     * 如果当前岗位是城市体验专�?这里表示城市体验专家负责的小区id列表
     */
    private List<Integer> littleZoneIdList;

    /**
     * 如果当前岗位是区域体验专家，这里表示负责的大区id列表
     */
    private List<Integer> zoneIdList;

}
