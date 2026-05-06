package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 依据手机号查询线索信息返回结�?
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetCLueInfoByPhoneGoOut {

    private static final long serialVersionUID = 1L;

    /**
     * 线索状�?
     */
    private Integer status;

    /**
     * 线索id
     */
    private Long clueId;

    /**
     * 用户id
     */
    private Long vUid;

    /**
     * 清洗类型
     */
    private Integer cleanType;

    /**
     * 清晰渠道
     */
    private String cleanStore;

    /**
     * 清洗渠道归属人mid
     */
    private Long cleanStoreOwner;

    /**
     * 商机id
     */
    private Long opportunityId;

    /**
     * 销售门�?
     */
    private String saleStore;

    /**
     * 销售归属人mid
     */
    private Long saleStoreOwner;

    /**
     * 意向门店
     */
    private String intendedStore;
}
