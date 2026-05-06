package com.wt.complaint.manage.backups.message;

import com.wt.complaint.manage.domain.strategy.message.Application72hCannotBeClosedAuditMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhangzheyang
 * @date 2025/2/12
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class Application72hCannotBeClosedAuditMessageTest {

    @Resource
    private Application72hCannotBeClosedAuditMessage application72hCannotBeClosedAuditMessage;

    @Test
    public void testGetEmailListByPositionId() {
        List<String> result =  application72hCannotBeClosedAuditMessage.getEmailListByPositionId(174);
        System.out.println(result);
    }
}
