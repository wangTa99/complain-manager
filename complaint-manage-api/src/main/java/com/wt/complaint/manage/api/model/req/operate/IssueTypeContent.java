package com.wt.complaint.manage.api.model.req.operate;

import lombok.Data;

import java.io.Serializable;

@Data
public class IssueTypeContent implements Serializable {

    private static final long serialVersionUID = 9151013215995786834L;

    /** 问题类别id */
    private Integer id;
    /** 名称 */
    private String name;
    /** 全路径名�?*/
    private String pathName;
    /** 全路径Id */
    private String pathId;

}
