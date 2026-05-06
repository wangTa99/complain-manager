package com.wt.complaint.manage.backups.provider;

import com.wt.complaint.manage.api.provider.BpmCallBackProvider;
import com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap;
import com.wt.complaint.manage.domain.constant.ComplaintInfoConstant;
import com.xiaomi.newretail.bpm.api.model.callback.OnStatusChangedRequest;
import com.xiaomi.newretail.bpm.api.model.callback.ProcessAction;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ComplaintManageBootstrap.class)
public class BpmComplaintOperateProviderTest {

    @Resource
    BpmCallBackProvider callBackProvider;

    /**
     * 服务客诉申请免责bpm callBack
     */
    @Test
    public void coolRequestResponsibilityExemptionCallBack_test() {
        OnStatusChangedRequest request = new OnStatusChangedRequest();
        request.setProcessInstanceId("987e5b95-269b-11f1-a2c5-7ecf062e03fb");
        request.setOperator("v-zhengshuiguang");
        request.setAction(ProcessAction.Refuse);
        Map<String, Object> extraMap = new HashMap<>();
        extraMap.put(ComplaintInfoConstant.BPM_COMPLAINT_NO_KEY, "TS256851017534107");
        request.setExtra(extraMap);
        System.err.println(callBackProvider.responsibilityExemptionCallback(request));
    }
}
