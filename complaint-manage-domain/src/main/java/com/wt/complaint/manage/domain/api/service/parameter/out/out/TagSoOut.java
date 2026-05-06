package com.wt.complaint.manage.domain.api.service.parameter.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TagSoOut implements Serializable {

    private static final long serialVersionUID = 4411083089993524481L;

    /**
     * tag 英文字符�?
     */
    private String code;

    /**
     * tag 中文描述
     */
    private String desc;
}
