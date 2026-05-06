package com.wt.complaint.manage.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobSuccessDto implements Serializable {

    /**
     * 文件URL
     */
    private String fileUrl;
}
