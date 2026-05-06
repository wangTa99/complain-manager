package com.wt.complaint.manage.backups.message;
import com.wt.complaint.manage.domain.api.enums.PositionEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.strategy.message.RemindMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class RemindMessageTest {

    @Resource
    private RemindMessage remindMessage;

    @Resource
    private ComplaintGateway complaintGateway;

    @Resource
    ApplicationEventPublisher eventPublisher;

    @Test
    public void testRemindMessage() {
        String complaintNo = "TS248191003051628";
        ComplaintOrderGoOut complaintOrderGoOut = complaintGateway.selectByComplaintNo(complaintNo);
        MessageInformedEvent messageInformedEvent = remindMessage.createMessageInformedEvent(complaintOrderGoOut, new HashMap<>());
        System.out.println(messageInformedEvent);
        remindMessage.getAllEmailSet(complaintOrderGoOut);
        remindMessage.getFinalEmailSetByZoneAndPosition(complaintOrderGoOut, Arrays.asList(PositionEnum.CITY_SERVICE_MANAGER, PositionEnum.URBAN_EXPERIENCE_EXPERT));
        eventPublisher.publishEvent(messageInformedEvent);
    }
}
