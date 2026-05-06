package com.wt.complaint.manage.infrastructure.model.param;

import lombok.Data;

import java.util.List;

@Data
public class UserConsultOrderSearchParam {
    private String superTicketNo;

    private String consultNo;

    private List<String> consultNoList;

    private List<String> stNoList;

    private String idempotentKey;



    private String vid;

    private Byte orderStatus;

    private boolean master;

    /**
     * 门店id列表（用于多门店过滤�?
     */
    private List<String> orgIdList;

    /**
     * 关键字（模糊匹配 consult_no、car_no、vid�?
     */
    private String key;

    /**
     * 分页偏移�?
     */
    private Integer pageOffset;

    /**
     * 每页条数
     */
    private Integer pageSize;

    private Long operatorMid;


    private Byte consultType;

    private String vin;


    private Integer handleResult;



    private Integer urgencyLevel;


    private String createTimeStart;


    private String createTimeEnd;


    private String finishTimeStart;


    private String finishTimeEnd;
}
