package com.wt.complaint.manage.domain.manager;

import com.wt.complaint.manage.api.model.enums.ComplaintStatusEnum;
import com.wt.complaint.manage.api.model.enums.CreateSourceEnum;
import com.wt.complaint.manage.api.model.enums.ResponsibilityEnum;
import com.wt.complaint.manage.api.model.enums.ReviewedEnum;
import com.wt.complaint.manage.domain.api.enums.ComplaintTypeEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintAuditGateway;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintAuditListSoOut;
import com.wt.complaint.manage.domain.constant.ComplaintInfoConstant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAuthManagerTest {

    @InjectMocks
    private UserAuthManager userAuthManager;

    @Mock
    private ComplaintAuditGateway auditGateway;

    private ComplaintAuditListSoOut buildAuditOut(long total) {
        ComplaintAuditListSoOut soOut = new ComplaintAuditListSoOut();
        soOut.setTotal(total);
        return soOut;
    }

    @Test
    void applyNoDutyV2ShouldReturnFalseWhenNoResponsibility() {
        UserActionAuthContext context = new UserActionAuthContext();
        context.setResponsibility(ResponsibilityEnum.NO.getCode());
        Assertions.assertFalse(userAuthManager.applyNoDutyV2(context));
    }

    @Test
    void applyNoDutyV2ShouldCheckOngoingAudit() {
        UserActionAuthContext context = new UserActionAuthContext();
        context.setResponsibility(ResponsibilityEnum.YES.getCode());
        context.setComplaintNo("TS-1");
        when(auditGateway.searchComplaintAuditList(any())).thenReturn(buildAuditOut(1));
        Assertions.assertFalse(userAuthManager.applyNoDutyV2(context));

        when(auditGateway.searchComplaintAuditList(any())).thenReturn(buildAuditOut(0));
        Assertions.assertTrue(userAuthManager.applyNoDutyV2(context));
    }

    @Test
    void apply72NoFinishV2ShouldCheckAudit() {
        UserActionAuthContext context = new UserActionAuthContext();
        context.setComplaintNo("TS-2");
        when(auditGateway.searchComplaintAuditList(any())).thenReturn(buildAuditOut(0));
        Assertions.assertTrue(userAuthManager.apply72NoFinishV2(context));

        when(auditGateway.searchComplaintAuditList(any())).thenReturn(buildAuditOut(2));
        Assertions.assertFalse(userAuthManager.apply72NoFinishV2(context));
    }

    @Test
    void applyFinishV2ShouldCheckAudit() {
        UserActionAuthContext context = new UserActionAuthContext();
        context.setComplaintNo("TS-3");
        when(auditGateway.searchComplaintAuditList(any())).thenReturn(buildAuditOut(0));
        Assertions.assertTrue(userAuthManager.applyFinishV2(context));

        when(auditGateway.searchComplaintAuditList(any())).thenReturn(buildAuditOut(3));
        Assertions.assertFalse(userAuthManager.applyFinishV2(context));
    }

    @Test
    void applyNoDutyV2ShouldReturnFalseWhenExemptionApplyTimesReached() {
        UserActionAuthContext context = new UserActionAuthContext();
        context.setResponsibility(ResponsibilityEnum.YES.getCode());
        context.setExemptionApplyTimes(ComplaintInfoConstant.RESPONSIBILITY_EXEMPTION_MAX_APPLY_TIMES);
        Assertions.assertFalse(userAuthManager.applyNoDutyV2(context));
        verify(auditGateway, never()).searchComplaintAuditList(any());
    }

    @Test
    void applySubmitReviewShouldMatchOnlineServiceComplaintRules() {
        UserActionAuthContext ok = new UserActionAuthContext();
        ok.setCreateSource(CreateSourceEnum.ONLINE_CS.getCode());
        ok.setComplaintType(ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        ok.setReviewed(ReviewedEnum.NO.getCode());
        ok.setStatus(ComplaintStatusEnum.FIRST_RESPONSE_PENDING.getCode());
        Assertions.assertTrue(userAuthManager.applySubmitReview(ok));

        UserActionAuthContext reviewed = new UserActionAuthContext();
        reviewed.setCreateSource(CreateSourceEnum.ONLINE_CS.getCode());
        reviewed.setComplaintType(ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        reviewed.setReviewed(ReviewedEnum.YES.getCode());
        reviewed.setStatus(ComplaintStatusEnum.FIRST_RESPONSE_PENDING.getCode());
        Assertions.assertFalse(userAuthManager.applySubmitReview(reviewed));
    }

    @Test
    void applyOrgChangeShouldBlockStoreSourceOrOngoingAudit() {
        UserActionAuthContext fromStore = new UserActionAuthContext();
        fromStore.setCreateSource(CreateSourceEnum.STORE.getCode());
        fromStore.setComplaintNo("ORG-1");
        Assertions.assertFalse(userAuthManager.applyOrgChange(fromStore));
        verify(auditGateway, never()).searchComplaintAuditList(any());

        UserActionAuthContext online = new UserActionAuthContext();
        online.setCreateSource(CreateSourceEnum.ONLINE_CS.getCode());
        online.setComplaintNo("ORG-2");
        when(auditGateway.searchComplaintAuditList(any())).thenReturn(buildAuditOut(1));
        Assertions.assertFalse(userAuthManager.applyOrgChange(online));

        when(auditGateway.searchComplaintAuditList(any())).thenReturn(buildAuditOut(0));
        Assertions.assertTrue(userAuthManager.applyOrgChange(online));
    }
}
