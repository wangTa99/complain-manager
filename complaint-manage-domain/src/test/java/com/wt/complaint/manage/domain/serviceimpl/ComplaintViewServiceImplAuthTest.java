package com.wt.complaint.manage.domain.serviceimpl;

import com.google.common.collect.ImmutableList;
import com.wt.complaint.manage.api.model.resp.UserActionAuth;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintOrderRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.OrderListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.rpc.UpcConfigGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintFrameInfoSoIn;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.manager.UserActionAuthContext;
import com.wt.complaint.manage.domain.manager.componment.UpcConfigLocalCache;
import com.wt.complaint.manage.domain.manager.componment.UpcConfigParser;
import com.wt.proretail.newcommon.account.ProretailRoleEnum;
import org.apache.dubbo.rpc.RpcContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComplaintViewServiceImplAuthTest {

    @InjectMocks
    private ComplaintViewServiceImpl complaintViewService;

    @Mock
    private ComplaintOrderRepositoryGateway complaintOrderRepositoryGateway;

    @Mock
    private UpcConfigParser upcConfigParser;

    @Mock
    private UpcConfigLocalCache localCache;

    @AfterEach
    void tearDown() {
        RpcContext.getContext().clearAttachments();
    }

    @Test
    void shouldThrowWhenMiIdMissing() {
        RpcContext.getContext().setAttachment("$curr_role", "roleA");
        RpcContext.getContext().setAttachment("$upc_miID", "");
        ComplaintFrameInfoSoIn soIn = new ComplaintFrameInfoSoIn();
        soIn.setComplaintNo("TS-1");

        Assertions.assertThrows(BusinessException.class, () -> complaintViewService.getComplaintAuth(soIn));
    }

    @Test
    void shouldThrowWhenOrderMissing() {
        RpcContext.getContext().setAttachment("$curr_role", "roleA");
        RpcContext.getContext().setAttachment("$upc_miID", "10001");
        when(complaintOrderRepositoryGateway.findList(any(OrderListGoIn.class))).thenReturn(Collections.emptyList());

        ComplaintFrameInfoSoIn soIn = new ComplaintFrameInfoSoIn();
        soIn.setComplaintNo("TS-unknown");
        Assertions.assertThrows(BusinessException.class, () -> complaintViewService.getComplaintAuth(soIn));
    }

    @Test
    void shouldThrowWhenRoleMissing() {
        RpcContext.getContext().setAttachment("$curr_role", "");
        RpcContext.getContext().setAttachment("$upc_miID", "10001");

        ComplaintFrameInfoSoIn soIn = new ComplaintFrameInfoSoIn();
        soIn.setComplaintNo("TS-1");

        Assertions.assertThrows(BusinessException.class, () -> complaintViewService.getComplaintAuth(soIn));
    }

    @Test
    void shouldFilterServiceRepButtonsWhenNotOwner() {
        RpcContext.getContext().setAttachment("$curr_role", ProretailRoleEnum.CAR_SERVICE_REPRESENTATIVE.getKey());
        RpcContext.getContext().setAttachment("$upc_miID", "20001");

        ComplaintOrderInfoGoIn order = ComplaintOrderInfoGoIn.builder()
                .complaintNo("TS-2")
                .orgId("org-1")
                .operatorMid(30001L)
                .responsibility(1)
                .status(2)
                .build();
        when(complaintOrderRepositoryGateway.findList(any(OrderListGoIn.class)))
                .thenReturn(Collections.singletonList(order));

        List<String> roleKeys = new ArrayList<>();
        roleKeys.add(ProretailRoleEnum.CAR_SERVICE_REPRESENTATIVE.getKey());
        roleKeys.add("manager");
        when(upcConfigParser.getRoleList(any(UpcConfigGoIn.class))).thenReturn(roleKeys);

        Map<String, List<String>> upcConfig = new HashMap<>();
        upcConfig.put("complaintFrame|" + ProretailRoleEnum.CAR_SERVICE_REPRESENTATIVE.getKey(),
                ImmutableList.of("complaintFrame.pickUp", "complaintFrame.dispatch"));
        upcConfig.put("complaintFrame|manager", ImmutableList.of("complaintFrame.applyFinish"));
        when(localCache.getUpcConfigMap()).thenReturn(upcConfig);

        ArgumentCaptor<List<String>> resourceCaptor = ArgumentCaptor.forClass(List.class);
        doReturn(Arrays.asList("pickUp", "applyFinish"))
                .when(upcConfigParser).calcButtons(resourceCaptor.capture(), any(), eq(order));

        ComplaintFrameInfoSoIn soIn = new ComplaintFrameInfoSoIn();
        soIn.setComplaintNo("TS-2");
        soIn.setOrgId(order.getOrgId());

        UserActionAuth auth = complaintViewService.getComplaintAuth(soIn).getUserActionAuth();

        List<String> resourcesPassed = resourceCaptor.getValue();
        Assertions.assertTrue(resourcesPassed.contains("complaintFrame.pickUp"));
        Assertions.assertFalse(resourcesPassed.contains("complaintFrame.dispatch"),
                "dispatch should be filtered out for non-owner service rep");

        Assertions.assertEquals(Arrays.asList("pickUp", "applyFinish"), auth.getActionsList());
        Assertions.assertEquals(Arrays.asList("pickUp", "applyFinish"), auth.getButtons());
    }

    @Test
    void shouldKeepServiceRepButtonsWhenOwner() {
        RpcContext.getContext().setAttachment("$curr_role", ProretailRoleEnum.CAR_SERVICE_REPRESENTATIVE.getKey());
        RpcContext.getContext().setAttachment("$upc_miID", "20001");

        ComplaintOrderInfoGoIn order = ComplaintOrderInfoGoIn.builder()
                .complaintNo("TS-3")
                .orgId("org-3")
                .operatorMid(20001L)
                .responsibility(1)
                .status(2)
                .build();
        when(complaintOrderRepositoryGateway.findList(any(OrderListGoIn.class)))
                .thenReturn(Collections.singletonList(order));

        when(upcConfigParser.getRoleList(any(UpcConfigGoIn.class)))
                .thenReturn(Collections.singletonList(ProretailRoleEnum.CAR_SERVICE_REPRESENTATIVE.getKey()));

        Map<String, List<String>> upcConfig = new HashMap<>();
        upcConfig.put("complaintFrame|" + ProretailRoleEnum.CAR_SERVICE_REPRESENTATIVE.getKey(),
                ImmutableList.of("complaintFrame.pickUp", "complaintFrame.dispatch"));
        when(localCache.getUpcConfigMap()).thenReturn(upcConfig);

        ArgumentCaptor<List<String>> resourceCaptor = ArgumentCaptor.forClass(List.class);
        doReturn(Collections.singletonList("dispatch"))
                .when(upcConfigParser).calcButtons(resourceCaptor.capture(), any(), eq(order));

        ComplaintFrameInfoSoIn soIn = new ComplaintFrameInfoSoIn();
        soIn.setComplaintNo("TS-3");
        soIn.setOrgId(order.getOrgId());

        complaintViewService.getComplaintAuth(soIn);

        List<String> resourcesPassed = resourceCaptor.getValue();
        Assertions.assertTrue(resourcesPassed.contains("complaintFrame.dispatch"),
                "owner should keep service rep resources");
    }

    /**
     * getComplaintAuth 将客诉单�?createSource、reviewed、exemptionApplyTimes 写入 context 供按钮计算使�?
     */
    @Test
    void getComplaintAuth_contextHasCreateSourceReviewedExemptionApplyTimes() {
        RpcContext.getContext().setAttachment("$curr_role", "manager");
        RpcContext.getContext().setAttachment("$upc_miID", "10001");

        ComplaintOrderInfoGoIn order = ComplaintOrderInfoGoIn.builder()
                .complaintNo("TS-ctx")
                .orgId("org-1")
                .operatorMid(10001L)
                .responsibility(0)
                .status(2)
                .createSource(1)
                .reviewed(1)
                .exemptionApplyTimes(2)
                .build();
        when(complaintOrderRepositoryGateway.findList(any(OrderListGoIn.class)))
                .thenReturn(Collections.singletonList(order));

        when(upcConfigParser.getRoleList(any(UpcConfigGoIn.class))).thenReturn(Collections.singletonList("manager"));
        Map<String, List<String>> upcConfig = new HashMap<>();
        upcConfig.put("complaintFrame|manager", ImmutableList.of("complaintFrame.applyFinish"));
        when(localCache.getUpcConfigMap()).thenReturn(upcConfig);

        ArgumentCaptor<UserActionAuthContext> contextCaptor = ArgumentCaptor.forClass(UserActionAuthContext.class);
        doReturn(Collections.singletonList("applyFinish"))
                .when(upcConfigParser).calcButtons(any(), contextCaptor.capture(), eq(order));

        ComplaintFrameInfoSoIn soIn = new ComplaintFrameInfoSoIn();
        soIn.setComplaintNo("TS-ctx");
        soIn.setOrgId("org-1");

        complaintViewService.getComplaintAuth(soIn);

        UserActionAuthContext context = contextCaptor.getValue();
        Assertions.assertNotNull(context);
        Assertions.assertEquals(Integer.valueOf(1), context.getCreateSource());
        Assertions.assertEquals(Integer.valueOf(1), context.getReviewed());
        Assertions.assertEquals(Integer.valueOf(2), context.getExemptionApplyTimes());
    }
}
