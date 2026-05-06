package com.wt.complaint.manage.backups.message;

import com.wt.complaint.manage.app.providerimpl.CronPushTaskImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author zhangzheyang
 * @date 2025/1/7
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class CronPushTest {

    @Resource
    private CronPushTaskImpl cronPushTask;

    @Test
    public void test() {
        cronPushTask.cronPush("test");
    }
}
