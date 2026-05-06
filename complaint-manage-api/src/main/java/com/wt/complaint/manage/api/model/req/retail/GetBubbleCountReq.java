package com.wt.complaint.manage.api.model.req.retail;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.Builder;
import lombok.Data;

/**
 * @author v-wangkai40
 */
@Builder
@Data
public class GetBubbleCountReq implements java.io.Serializable {

    private static final long serialVersionUID = 1822941452702307318L;

    @ApiDocClassDefine(value = "orgCode", description = "下钻门店")
    private String orgCode;
}
