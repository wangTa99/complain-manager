package com.wt.complaint.manage.backups.listener;

import cn.hutool.core.collection.CollUtil;
import com.wt.complaint.manage.domain.api.gateway.interfaces.RetailComplaintGateway;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.wt.complaint.manage.domain.listener.CreateChatGroupListener;
import com.wt.complaint.manage.domain.model.CreateChatGroupEvent;

import java.util.ArrayList;
import java.util.Date;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class CreateChatGroupListenerTest {

    @Resource
    private CreateChatGroupListener createChatGroupListener;

    @Resource
    private RetailComplaintGateway retailComplaintGateway;

    @Test
    public void testHandleEvent() {
//        retailComplaintGateway.getRetailComplaintDetail()
        CreateChatGroupEvent event = new CreateChatGroupEvent();
        event.setDrNo("DR256461032691323");
        event.setOrgId("J0001");
        event.setZoneId(13);
        event.setLittleZoneId(44);
        event.setCustomerServiceMid(3150447795L);
        event.setOperatorMid(3150425845L);
        event.setOperatorPositionId(466);
        event.setCreateTime(new Date());
        event.setComplaintContent("[{\"groupName\":\"基本信息\",\"groupOrder\":1,\"fields\":[{\"id\":153,\"order\":1,\"required\":1,\"fieldType\":2,\"fieldName\":\"交付门店\",\"fieldCode\":\"orgId\",\"value\":[{\"code\":\"J0001\",\"desc\":\"小米汽车海淀区总店36小米汽车海淀区总店36小米汽车海淀区总店36\"}],\"attachmentList\":[]},{\"id\":154,\"order\":2,\"required\":1,\"fieldType\":2,\"fieldName\":\"销售订单\",\"fieldCode\":\"tradeOrderId\",\"value\":[{\"code\":\"5256461035656816\",\"desc\":\"5256461035656816\"}],\"attachmentList\":[]},{\"id\":99,\"order\":3,\"required\":1,\"fieldType\":3,\"fieldName\":\"问题分类\",\"fieldCode\":\"issueType\",\"value\":[{\"code\":\"206\",\"desc\":\"购买支付相关\",\"pathId\":\"186/204/206\",\"pathName\":\"小米汽车APP/商城_周边/购买支付相关\"}]},{\"id\":108,\"order\":4,\"required\":1,\"fieldType\":2,\"fieldName\":\"风险等级\",\"fieldCode\":\"riskLevel\",\"value\":[{\"code\":\"4\",\"desc\":\"L4\"}],\"attachmentList\":[]},{\"id\":109,\"order\":5,\"required\":1,\"fieldType\":4,\"fieldName\":\"问题详情\",\"fieldCode\":\"problemDesc\",\"value\":[{\"code\":\"\",\"desc\":\"12312312312\"}],\"attachmentList\":[]},{\"id\":148,\"order\":6,\"required\":1,\"fieldType\":3,\"fieldName\":\"投诉场景\",\"fieldCode\":\"\",\"value\":[{\"code\":\"135\",\"desc\":\"道路救援不满\",\"pathId\":\"133/134/135\",\"pathName\":\"服务/服务产品和权�?道路救援不满\"}],\"attachmentList\":[]},{\"id\":149,\"order\":7,\"required\":1,\"fieldType\":2,\"fieldName\":\"举报场景\",\"fieldCode\":\"serviceScene\",\"value\":[{\"code\":\"1\",\"desc\":\"过度维修\"}],\"attachmentList\":[]}]}]");
        event.setRiskLevel(4);
        event.setContactNameC("GBAjHTZiswl9EWk34xQo9uZYGBJ_7TROjPhIooYdJdPMLPMtrf8YEKHobuXW0CYhDlGUOt6GJucYFME6TdyzdjBn-XsbJQ732QE1GlCYJQASAA");
        event.setContactPhoneC("b0ce770936ecca4c3dd4e8e87911ecb1");
        event.setProblemDesc("小米汽车APP/商城_周边/购买支付相关");

        createChatGroupListener.handleEvent(event);
    }

    @Test
    public void createChatGroup() {
//        retailComplaintGateway.getRetailComplaintDetail()
        ArrayList<String> email = CollUtil.newArrayList("zhangzheyang", "v-huxiankang", "p-zhouyuanmeng");

//        createChatGroupListener.createChatGroup("交付客诉单拉群测�?, email);
    }
}
