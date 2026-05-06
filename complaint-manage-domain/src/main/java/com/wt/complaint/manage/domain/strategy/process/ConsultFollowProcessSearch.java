package com.wt.complaint.manage.domain.strategy.process;

import com.wt.complaint.manage.domain.api.service.interfaces.ComplaintViewService;
import com.wt.complaint.manage.domain.api.service.interfaces.UserConsultViewService;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintProcessSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintProcessListSoOut;
import com.wt.complaint.manage.domain.constant.StrategyConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service(StrategyConstant.CONSULT_ORDER_FOLLOW_PROCESS)
public class ConsultFollowProcessSearch extends AbstractSearch {

    @Autowired
    private UserConsultViewService userConsultViewService;

    @Override
    public ComplaintProcessListSoOut getFollowUpRecords(ComplaintProcessSoIn soIn) {
        return userConsultViewService.getComplaintProcessRecords(soIn);
    }

}
