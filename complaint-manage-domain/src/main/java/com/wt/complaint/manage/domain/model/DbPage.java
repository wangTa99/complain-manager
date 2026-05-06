package com.wt.complaint.manage.domain.model;

import lombok.Data;

import java.util.List;

/**
 * 分页
 */
@Data
public class DbPage<T> {

    private Integer total;

    private List<T> data;

}
