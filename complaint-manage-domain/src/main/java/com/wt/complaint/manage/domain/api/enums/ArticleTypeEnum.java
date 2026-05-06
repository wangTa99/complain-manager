package com.wt.complaint.manage.domain.api.enums;

/**
 * 文章类型枚举
 *
 * @author wangshanjun
 * @date 2021/6/17
 */
public enum ArticleTypeEnum {
    
    /**
     * 零售学院文章
     */
    COLLEGE(1, "学院文章"),
    /**
     * 公告中心文章
     */
    ANNOUNCEMENT(2, "公告文章"),
    
    /**
     * 政策中心文章
     */
    POLICY(3, "政策文章");
    
    private final Integer code;
    
    private final String desc;
    
    ArticleTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public Integer getCode() {
        return code;
    }
    
    
    public String getDesc() {
        return desc;
    }
    
    
}
