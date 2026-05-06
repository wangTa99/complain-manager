package com.wt.complaint.manage.backups.provider;

import com.wt.complaint.manage.api.model.req.task.TimeOutTagTaskReq;
import com.wt.complaint.manage.api.model.resp.task.TimeOutTagTaskResp;
import com.wt.complaint.manage.api.task.DataFixTask;
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
public class DataFixTaskTest {
    @Resource
    DataFixTask dataFixTask;

    @Test
    public void fillComplaintSceneTaskTest() {
        Result<String> result = dataFixTask.fillComplaintSceneTask("");
        log.info(result.toString());
    }

    @Test
    public void fixOperatorPositionTest() {
        Result<String> result = dataFixTask.fixOperatorPosition("");
        log.info(result.toString());
    }

    @Test
    public void updateZoneDataTest() {
        Result<String> result = dataFixTask.updateZoneData("");
        log.info(result.toString());
    }

    @Test
    public void coolRequestResponsibilityToTagTest() {
        // 测试处理所有有责投诉单
        Result<Integer> result = dataFixTask.convertResponsibilityToTag("");
        log.info("convertResponsibilityToTagTest result: {}", result.toString());
    }

    @Test
    public void coolRequestResponsibilityToTagWithComplaintNoTest() {
        // 测试处理指定投诉单号
        String complaintNo = "TS257881003103535";
        Result<Integer> result = dataFixTask.convertResponsibilityToTag(complaintNo);
        log.info("convertResponsibilityToTagWithComplaintNoTest complaintNo: {}, result: {}", complaintNo, result.toString());
    }
}
