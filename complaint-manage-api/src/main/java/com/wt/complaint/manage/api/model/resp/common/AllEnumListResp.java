package com.wt.complaint.manage.api.model.resp.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 枚举列表
 * @author linjiehong
 * @date 2025/5/19 13:34
 */
@Data
public class AllEnumListResp implements Serializable {
    private List<Map<String, Object>> statusEnumList;
}
