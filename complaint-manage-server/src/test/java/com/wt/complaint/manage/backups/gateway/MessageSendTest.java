package com.wt.complaint.manage.backups.gateway;

import com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import com.xiaomi.nr.messagehub.sdk.dto.bean.MiOfficeEmail;
import com.xiaomi.nr.messagehub.sdk.dto.enums.BaseChannel;
import com.xiaomi.nr.messagehub.sdk.dto.enums.ChannelEnum;
import com.xiaomi.nr.messagehub.sdk.dto.request.SendRequest;
import com.xiaomi.nr.messagehub.sdk.service.ReceiverDubboService;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author zhengziwei
 * @date 2024/11/4 下午5:15
 */
//@Ignore
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ComplaintManageBootstrap.class)
public class MessageSendTest {
    
    @DubboReference(group = "${dubbo.group.message}", timeout = 5000, check = false, interfaceClass =
            ReceiverDubboService.class, version = "1.0")
    private ReceiverDubboService receiverDubboService;
    
    @Test
    public void send() {
        SendRequest sendRequest =
                RetailJsonUtil.fromJson("{\"requestId\":\"2283573dba054fc5b68f4bee357891j1\"," +
                                "\"ctime\":1761893876,\"sceneId\":300001043,\"bizType\":" +
                                "\"car_third_performance\",\"toChannelList\":[{\"payload\":{\"itemName\":\"车商城单品创建测�?" +
                                "黑色\"},\"bizType\":\"car_third_performance\",\"channelName\":\"MI_OFFICE_EMAIL\"}]," +
                                "\"emailList\":[\"zhangzheyang@xiaomi.com\"]}",
                        SendRequest.class);
        ArrayList<BaseChannel> toChannelList = new ArrayList<>();
        HashMap<String, String> payload = new HashMap<>();
        payload.put("itemName", "车商城单品创建测�?黑色");
        MiOfficeEmail miOfficeEmail = new MiOfficeEmail(payload);
        miOfficeEmail.setChannelName(ChannelEnum.MI_OFFICE_EMAIL.name());
        toChannelList.add(miOfficeEmail);
        sendRequest.setToChannelList(toChannelList);

        System.out.println("sendRequest = " + RetailJsonUtil.toJson(sendRequest));
        Result<Boolean> send = receiverDubboService.send(sendRequest);
        System.out.println("send = " + send);
    }


}
