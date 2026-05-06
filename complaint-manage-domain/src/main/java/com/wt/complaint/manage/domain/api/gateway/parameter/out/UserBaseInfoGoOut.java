package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangzheyang
 * @date 2025/1/3
 */
@Data
public class UserBaseInfoGoOut implements Serializable {

    private static final long serialVersionUID = -1666921083189419944L;

    /**
     * 米聊�?
     */
    private Long miId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 邮箱
     */
    private String email;
}
