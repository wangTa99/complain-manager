package com.wt.complaint.manage.infrastructure.outer.mysqlwrite;

import com.wt.complaint.manage.infrastructure.outer.mysqlwrite.dato.DimOrgResult;

import java.util.List;

/**
 * @author lizhao
 * @date 2020-11-22
 */
public interface DimOrgMysqlWriteMapper {
    
    /**
     * 项目骨架代码，可删除
     *
     * @return 两条门店信息
     */
    List<DimOrgResult> loadAll2();
    
}
