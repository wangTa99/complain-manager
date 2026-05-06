package com.wt.complaint.manage.backups.message;

import com.wt.complaint.manage.api.task.CronFeiShuRobotDeleteTokenTask;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class CronFeiShuRobotDeleteTokenTaskTest {

    @Resource
    private CronFeiShuRobotDeleteTokenTask cronFeiShuRobotDeleteTokenTask;

    @Test
    public void test() {
        Result<String> result = cronFeiShuRobotDeleteTokenTask.deleteFeiShuToken("test");
        log.info(result.toString());
    }
}
