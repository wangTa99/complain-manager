package com.wt.complaint.manage.infrastructure.gatewayimpl.rpc;

import com.wt.complaint.manage.api.model.resp.CityZoneDTO;
import com.wt.maindataapi.api.ZoneProvider;
import com.wt.maindataapi.model.PageResp;
import com.wt.maindataapi.model.dto.zone.CityZoneDto;
import com.wt.maindataapi.model.req.zone.CityZoneListReq;
import com.xiaomi.youpin.infra.rpc.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class StoreRemoteGatewayImplUnitTest {

    @InjectMocks
    private StoreRemoteGatewayImpl storeRemoteGateway;

    @Mock
    private ZoneProvider zoneProvider;

    @Test
    void testGetCityZoneList_emptyInput() {
        List<CityZoneDTO> result = storeRemoteGateway.getCityZoneList(new ArrayList<>());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testGetCityZoneList_success_splitBatches() {
        // 构�?25 �?cityZoneId，预期分�?20 �?5 两批
        List<Integer> cityIds = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            cityIds.add(i);
        }

        // 第一批返�?20 �?
        Result<PageResp<CityZoneDto>> batch1 = Result.success(null);
        PageResp<CityZoneDto> page1 = new PageResp<>();
        List<CityZoneDto> list1 = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            CityZoneDto dto = new CityZoneDto();
            dto.setCityZoneId(i);
            dto.setCityZoneName("CZ-" + i);
            dto.setCityZoneCode("C" + i);
            list1.add(dto);
        }
        page1.setList(list1);
        batch1.setData(page1);

        // 第二批返�?5 �?
        Result<PageResp<CityZoneDto>> batch2 = Result.success(null);
        PageResp<CityZoneDto> page2 = new PageResp<>();
        List<CityZoneDto> list2 = new ArrayList<>();
        for (int i = 21; i <= 25; i++) {
            CityZoneDto dto = new CityZoneDto();
            dto.setCityZoneId(i);
            dto.setCityZoneName("CZ-" + i);
            dto.setCityZoneCode("C" + i);
            list2.add(dto);
        }
        page2.setList(list2);
        batch2.setData(page2);

        when(zoneProvider.getCityZoneList(any(CityZoneListReq.class)))
                .thenReturn(batch1)
                .thenReturn(batch2);

        List<CityZoneDTO> result = storeRemoteGateway.getCityZoneList(cityIds);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(25, result.size());
        // 验证分批调用次数
        verify(zoneProvider, times(2)).getCityZoneList(any(CityZoneListReq.class));
        // 简单抽样校验映�?
        Assertions.assertEquals("CZ-1", result.get(0).getCityZoneName());
        Assertions.assertEquals("CZ-25", result.get(24).getCityZoneName());
    }

    @Test
    void testGetCityZoneList_failure_returnEmpty() {
        List<Integer> cityIds = Arrays.asList(1, 2, 3);

        // 返回�?OK �?code 以触发异常并被方法捕获，最终返回空列表
        Result<PageResp<CityZoneDto>> bad = Result.success(null);
        bad.setData(null);
        when(zoneProvider.getCityZoneList(any(CityZoneListReq.class))).thenReturn(bad);

        List<CityZoneDTO> result = storeRemoteGateway.getCityZoneList(cityIds);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }
}


