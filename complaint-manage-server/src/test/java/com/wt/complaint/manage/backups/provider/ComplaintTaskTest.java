package com.wt.complaint.manage.backups.provider;

import com.wt.complaint.manage.api.model.req.task.TimeOutTagTaskReq;
import com.wt.complaint.manage.api.model.resp.task.TimeOutTagTaskResp;
import com.wt.complaint.manage.api.task.TimeOutTagTask;
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
public class ComplaintTaskTest {
    @Resource
    TimeOutTagTask timeOutTagTask;

    @Test
    public void coolRequestTest() {
        TimeOutTagTaskReq timeOutTagTaskReq = new TimeOutTagTaskReq();
//        timeOutTagTaskReq.setComplaintNoList(Lists.newArrayList("RC948441000051378"));
        Result<TimeOutTagTaskResp> timeOutTagTaskRespResult = timeOutTagTask.syncTimeOutTag(timeOutTagTaskReq);
        log.info(timeOutTagTaskRespResult.toString());
    }
}
