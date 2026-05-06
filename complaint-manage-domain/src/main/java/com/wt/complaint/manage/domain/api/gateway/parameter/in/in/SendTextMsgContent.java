package com.wt.complaint.manage.domain.api.gateway.parameter.in;

import lombok.Data;

import java.io.Serializable;

/**
 * 飞书消息发送内容请求体
 * 封装飞书消息文本内容
 * @author zhangzheyang
 * @date 2025/5/7
 */
@Data
public class SendTextMsgContent implements Serializable {

    private static final long serialVersionUID = -3596647393179241914L;

    private String text;
}
