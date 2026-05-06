package com.wt.complaint.manage.domain.resp;

import lombok.Data;

@Data
public class ChatIdMapping {
    /**
     * 输入的群id
     */
    private String source;
    /**
     * 映射环境的群id，如果输入为私有化，则target为saas环境群id，否则相�?
     */
    private String target;
    /**
     * 固定�?group"
     */
    private String targetType;
    /**
     * source对应的环境是否为saas
     */
    private Boolean isSaas;
}