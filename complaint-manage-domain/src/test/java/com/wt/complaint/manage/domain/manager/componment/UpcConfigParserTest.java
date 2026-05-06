package com.wt.complaint.manage.domain.manager.componment;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.rpc.UpcConfigGoIn;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.manager.UserActionAuthContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpcConfigParserTest {

    @InjectMocks
    private UpcConfigParser parser;

    @Mock
    private com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway eiamRemoteGateway;

    @Mock
    private com.wt.complaint.manage.domain.manager.UserAuthManager userAuthManager;

    @Test
    void shouldThrowWhenOrgIdBlank() {
        UpcConfigGoIn goIn = UpcConfigGoIn.builder()
                .moduleKey("complaintFrame")
                .mid("1")
                .orgId("")
                .currRole("roleA")
                .build();
        Assertions.assertThrows(BusinessException.class, () -> parser.getRoleList(goIn));
    }

    @Test
    void shouldFallbackToCurrentRoleWhenNoStorePosition() {
        UpcConfigGoIn goIn = UpcConfigGoIn.builder()
                .moduleKey("complaintFrame")
                .mid("1")
                .orgId("org1")
                .currRole("roleA")
                .build();
        when(eiamRemoteGateway.getCarPositionRef()).thenThrow(new RuntimeException("mock"));
        List<String> roles = parser.getRoleList(goIn);
        Assertions.assertEquals(Collections.singletonList("roleA"), roles);
    }

    @Test
    void calcButtonsShouldSwallowFunctionException() {
        List<String> resources = Collections.singletonList("complaintFrame.status_1.applyFinish_unknownFunc");
        UserActionAuthContext context = new UserActionAuthContext();
        context.setStatus(1);
        DummyObj obj = new DummyObj();
        obj.setStatus(1);
        List<String> buttons = parser.calcButtons(resources, context, obj);
        Assertions.assertTrue(buttons.isEmpty(), "unknown function should not break parsing and yields empty buttons");
    }

    @Test
    void calcButtonsShouldReturnWhenConditionAndFunctionPass() {
        List<String> resources = Collections.singletonList("complaintFrame.status_1.applyFinish_applyFinishV2");
        UserActionAuthContext context = new UserActionAuthContext();
        context.setStatus(1);
        DummyObj obj = new DummyObj();
        obj.setStatus(1);
        when(userAuthManager.applyFinishV2(any(UserActionAuthContext.class))).thenReturn(true);

        List<String> buttons = parser.calcButtons(resources, context, obj);

        Assertions.assertEquals(Collections.singletonList("applyFinish"), buttons);
    }

    private static class DummyObj {
        private Integer status;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }
    }
}
