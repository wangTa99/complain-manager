package com.wt.complaint.manage.domain.api.gateway.parameter.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 查询有效用户信息请求�?
 *
 * @author huxiankang
 * @date 2025/11/5
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BatchGetIdUserReqBody {

    private List<String> emails;

}
