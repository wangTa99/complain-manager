package com.wt.complaint.manage.api.model.resp.deliver;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 客诉单列表返回体
 *
 * @author huxiankang
 * @date 2025/6/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliverComplaintListResp implements Serializable {

    @ApiDocClassDefine(value = "total", description = "总条�?)
    private Long total;

    @ApiDocClassDefine(value = "systemTime", description = "系统时间")
    private long systemTime;

    @ApiDocClassDefine(value = "pageNum", description = "页码")
    private Integer pageNum;

    @ApiDocClassDefine(value = "pageSize", description = "每页大小")
    private Integer pageSize;

    @ApiDocClassDefine(value = "dataList", description = "数据列表")
    private List<DeliverComplaintListDTO> dataList;
}
