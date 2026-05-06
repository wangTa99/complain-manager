package com.wt.complaint.manage.app.task;

import com.wt.complaint.manage.domain.api.service.interfaces.DataFixTaskService;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.xiaomi.youpin.infra.rpc.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DataFixTaskImplUnitTest {

    @InjectMocks
    private DataFixTaskImpl dataFixTaskImpl;

    @Mock
    private DataFixTaskService dataFixTaskService;

    @Test
    void testFillComplaintSceneTask_Success() {
        doNothing().when(dataFixTaskService).fillComplaintSceneTask(anyString());
        Result<String> result = dataFixTaskImpl.fillComplaintSceneTask("req-1");
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getCode());
        verify(dataFixTaskService, times(1)).fillComplaintSceneTask(anyString());
    }

    @Test
    void testFillComplaintSceneTask_Exception() {
        doThrow(new RuntimeException("boom")).when(dataFixTaskService).fillComplaintSceneTask(anyString());
        Result<String> result = dataFixTaskImpl.fillComplaintSceneTask("req-1");
        Assertions.assertNotNull(result);
        Assertions.assertEquals(ErrorCodeEnums.BUS_ERROR.getErrorCode().getCode(), result.getCode());
    }

    @Test
    void testFixOperatorPosition_Success() {
        doNothing().when(dataFixTaskService).fixOperatorPosition(anyString());
        Result<String> result = dataFixTaskImpl.fixOperatorPosition("req-2");
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getCode());
        verify(dataFixTaskService, times(1)).fixOperatorPosition(anyString());
    }

    @Test
    void testFixOperatorPosition_Exception() {
        doThrow(new RuntimeException("boom")).when(dataFixTaskService).fixOperatorPosition(anyString());
        Result<String> result = dataFixTaskImpl.fixOperatorPosition("req-2");
        Assertions.assertNotNull(result);
        Assertions.assertEquals(ErrorCodeEnums.BUS_ERROR.getErrorCode().getCode(), result.getCode());
    }

    @Test
    void testUpdateZoneData_Success() {
        doNothing().when(dataFixTaskService).updateZoneData(anyString());
        Result<String> result = dataFixTaskImpl.updateZoneData("req-3");
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getCode());
        verify(dataFixTaskService, times(1)).updateZoneData(anyString());
    }

    @Test
    void testUpdateZoneData_Exception() {
        doThrow(new RuntimeException("boom")).when(dataFixTaskService).updateZoneData(anyString());
        Result<String> result = dataFixTaskImpl.updateZoneData("req-3");
        Assertions.assertNotNull(result);
        Assertions.assertEquals(ErrorCodeEnums.BUS_ERROR.getErrorCode().getCode(), result.getCode());
    }

    @Test
    void testConvertResponsibilityToTag_Success() {
        // Mock service to return 5 processed records
        when(dataFixTaskService.convertResponsibilityToTag(anyString())).thenReturn(5);
        
        Result<Integer> result = dataFixTaskImpl.convertResponsibilityToTag("CP202403150001");
        
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getCode());
        Assertions.assertEquals(5, result.getData());
        verify(dataFixTaskService, times(1)).convertResponsibilityToTag("CP202403150001");
    }

    @Test
    void testConvertResponsibilityToTag_Success_NoComplaintNo() {
        // Mock service to return 100 processed records when no complaintNo specified
        when(dataFixTaskService.convertResponsibilityToTag(null)).thenReturn(100);
        
        Result<Integer> result = dataFixTaskImpl.convertResponsibilityToTag(null);
        
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getCode());
        Assertions.assertEquals(100, result.getData());
        verify(dataFixTaskService, times(1)).convertResponsibilityToTag(null);
    }

    @Test
    void testConvertResponsibilityToTag_Success_EmptyComplaintNo() {
        // Mock service to return 50 processed records when empty complaintNo specified
        when(dataFixTaskService.convertResponsibilityToTag("")).thenReturn(50);
        
        Result<Integer> result = dataFixTaskImpl.convertResponsibilityToTag("");
        
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getCode());
        Assertions.assertEquals(50, result.getData());
        verify(dataFixTaskService, times(1)).convertResponsibilityToTag("");
    }

    @Test
    void testConvertResponsibilityToTag_Success_ZeroRecords() {
        // Mock service to return 0 when no records need processing
        when(dataFixTaskService.convertResponsibilityToTag(anyString())).thenReturn(0);
        
        Result<Integer> result = dataFixTaskImpl.convertResponsibilityToTag("CP202403150999");
        
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getCode());
        Assertions.assertEquals(0, result.getData());
        verify(dataFixTaskService, times(1)).convertResponsibilityToTag("CP202403150999");
    }

    @Test
    void testConvertResponsibilityToTag_Exception() {
        // Mock service to throw exception
        when(dataFixTaskService.convertResponsibilityToTag(anyString()))
                .thenThrow(new RuntimeException("Database connection failed"));
        
        Result<Integer> result = dataFixTaskImpl.convertResponsibilityToTag("CP202403150001");
        
        Assertions.assertNotNull(result);
        Assertions.assertEquals(ErrorCodeEnums.BUS_ERROR.getErrorCode().getCode(), result.getCode());
        Assertions.assertEquals("Database connection failed", result.getMessage());
        verify(dataFixTaskService, times(1)).convertResponsibilityToTag("CP202403150001");
    }

}


