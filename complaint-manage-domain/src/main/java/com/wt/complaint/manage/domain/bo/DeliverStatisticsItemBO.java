package com.wt.complaint.manage.domain.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliverStatisticsItemBO implements Serializable {

    private static final long serialVersionUID = 8721652550374125997L;
    
    private Integer pendingFirstResponseCount;

    private Integer handlingCount;

    private Integer pendingResponsibilityCount;

    private Integer remindCount;

    private Integer firstResponseTimeoutCount;

    private Integer finishTimeoutCount;

}
