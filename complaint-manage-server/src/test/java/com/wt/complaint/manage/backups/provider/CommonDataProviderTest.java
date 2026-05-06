package com.wt.complaint.manage.backups.provider;

import com.wt.complaint.manage.api.model.req.common.CommonDataReq;
import com.wt.complaint.manage.api.model.resp.common.AllEnumListResp;
import com.wt.complaint.manage.api.model.resp.common.CommonOptionResp;
import com.wt.complaint.manage.api.provider.CommonDataProvider;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author linjiehong
 * @date 2025/5/30 17:19
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class CommonDataProviderTest {
    @Resource
    private CommonDataProvider commonDataProvider;

    @Test
    public void testGetCommonData() {
        CommonDataReq req = new CommonDataReq();
        Result<AllEnumListResp> statusList = commonDataProvider.getStatusList(req);
        Assert.assertNotNull(statusList);
    }

    @Test
    public void testOptionList() {
        Result<Map<String, List<CommonOptionResp>>> optionList = commonDataProvider.getOptionList();
        Assert.assertNotNull(optionList);
    }


}
