package com.wt.complaint.manage.backups.manager;

import com.wt.complaint.manage.api.model.enums.ReportOrderStatusEnum;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintOrderDetailSoOut;
import com.wt.complaint.manage.domain.constant.ReportActionConst;
import com.wt.complaint.manage.domain.manager.ReportAuthManager;
import com.wt.complaint.manage.backups.BaseTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author linjiehong
 * @date 2025/6/3 16:34
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class ReportAuthManagerTest extends BaseTest {
    @Resource
    ReportAuthManager reportAuthManager;

    @Test
    public void testGetDetailActionAuth() {
        Long mid = 3150447733L;
        String actionKey = ReportActionConst.ADD_FOLLOW_UP_RECORDS;
        UserComplaintOrderDetailSoOut soOut = new UserComplaintOrderDetailSoOut();
        soOut.setZoneId("39");
        soOut.setOrderStatus(ReportOrderStatusEnum.PENDING_JUDGE.getCode());
        reportAuthManager.hasDetailActionAuth(mid, actionKey, soOut);
    }
}
