package com.wt.complaint.manage.backups.gateway;

import cn.hutool.core.collection.CollUtil;
import com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap;
import com.wt.complaint.manage.domain.api.gateway.interfaces.http.LarkGateway;
import com.wt.complaint.manage.domain.listener.CreateChatGroupListener;
import com.wt.complaint.manage.infrastructure.gatewayimpl.ComplaintAuditGatewayImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import javax.annotation.Resource;

/**
 * @author zhangzheyang
 * @date 2025/1/8
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ComplaintManageBootstrap.class)
public class LarkGatewayTest {

    @Resource
    private LarkGateway larkGateway;
    @Resource
    private CreateChatGroupListener createChatGroupListener;

    @Test
    public void deleteClosingTagByComplaintNo() {
        List<String> strings = larkGateway.filterValidUser(CollUtil.newArrayList("v-huxiankang","chentianxiang2"));
        System.out.println("strings = " + strings);
    }

    @Test
    public void queryUserIdByEmailPrefixNo() {
        List<String> strings = larkGateway.queryUserIdByEmailPrefix(CollUtil.newArrayList("v-huxiankang","zhangzheyang",
                "chentianxiang2"));
        System.out.println("strings = " + strings);
    }

    @Test
    public void createChatGroupPrefixNo() {
//        createChatGroupListener.createChatGroup("测试一键拉�?, CollUtil.newArrayList("v-huxiankang","chentianxiang2"));
    }
}
