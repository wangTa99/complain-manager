package com.wt.complaint.manage.domain.api.service.parameter.out;

import com.wt.complaint.manage.api.model.ProvinceDTO;
import com.wt.complaint.manage.api.model.resp.LittleZoneDTO;
import com.wt.complaint.manage.api.model.resp.ZoneDTO;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintFollowProcessGoOut;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ExportComplaintListFillDataSoOut implements Serializable {

    /**
     * 查询最后一次申请结案记�?
     */
    private List<ComplaintFollowProcessGoOut> complaintFollowProcessGoOutList;

    /**
     * 最新一次提交复盘跟进记录列表（用于导出复盘材料�?
     */
    private List<ComplaintFollowProcessGoOut> submitReviewFollowProcessGoOutList;

    /**
     * 大区数据
     */
    private List<ZoneDTO> zoneList;

    /**
     * 小区数据
     */
    private List<LittleZoneDTO> littleZoneList;

    /**
     * 所有城市数�?
     */
    private List<ProvinceDTO> provinceList;
}
