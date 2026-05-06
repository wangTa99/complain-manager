package com.wt.complaint.manage.backups;

import com.wt.complaint.manage.api.model.req.DemoReq;
import com.wt.complaint.manage.api.provider.DemoProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class PolicyCenterTest {
    
    @Resource
    private DemoProvider demoProvider;
    
    @Test
    public void getCommentList() {
        DemoReq req = DemoReq.builder().id(1L)
                .aaa("s")
                .build();
        Assert.assertNotNull(demoProvider.toggleLike(req));
    }
    
}
