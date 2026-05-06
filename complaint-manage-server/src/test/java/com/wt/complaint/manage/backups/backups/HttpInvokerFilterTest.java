package com.wt.complaint.manage.backups;

import com.wt.complaint.manage.api.model.enums.ComplaintStatusEnum;
import com.wt.complaint.manage.api.model.enums.OnlyViewEnum;
import com.wt.complaint.manage.api.model.enums.PadTabEnum;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintListSearchGoIn;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.strategy.complaintlist.PadComplaintListSearch;
import com.wt.complaint.manage.domain.utils.DateUtil;
import com.wt.proretail.newcommon.http.HttpClientException;
import com.wt.proretail.newcommon.http.HttpClientFacade;
import com.wt.proretail.newcommon.http.HttpClientTypes;
import com.wt.proretail.newcommon.http.MediaTypes;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * desc:
 *
 * @author by lizhao
 * @date 2021/6/28 11:22
 */
@Slf4j
public class HttpInvokerFilterTest {
    
    private final HttpClientFacade httpClient = new HttpClientFacade(HttpClientTypes.OK_HTTP_CLIENT);
    
    @Test
    public void getCommentList() {
        String url = "https://www.baidu.com";
        HashMap<String, String> objectObjectHashMap = new HashMap<>();
        String result = httpClient.doGet(url, objectObjectHashMap);
        System.out.println(result);
    }
    
    @Test(expected = HttpClientException.class)
    public void testPostRequest() {
        String url = "https://www.baidu.com";
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("6676", "asdasd");
        headerMap.put("888", "4234");
        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("111", "asdasd");
        bodyMap.put("222", "4234");
        String result = httpClient.doPost(url, headerMap, bodyMap, MediaTypes.APPLICATION_JSON_UTF8);
    }
    @Test(expected = HttpClientException.class)
    public void testPostRequest2() {
        String url = "https://xmmionegw-outer.be.mi.com/mtop/nrme/proretail/v1/recommendSaleStatV2";
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Cookie", "upc_nr_token=b7f376d7e93e391e7b15455703c769f2f446142bbb174e");
//        HashMap<String, Object> bodyMap = new HashMap<>();
//        bodyMap.put("zoneId", "103");
        String request = "[{}]";
        String result = httpClient.doPost(url, headerMap, request, MediaTypes.APPLICATION_JSON_UTF8);
    }
    
    @Test(expected = HttpClientException.class)
    public void testPostRequest3() {
        String url = "http://xmmione-outer.be.mi.com/api/apiinfo/list";
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Cookie", "JSESSIONID=A0C87E469978009ABC68333A0B194EB2");
        //        HashMap<String, Object> bodyMap = new HashMap<>();
        //        bodyMap.put("zoneId", "103");
        String request = "{pageNo: 1, pageSize: 10, serviceName: \"proretail\", pathString: \"proretail\", urlString: \"proretail\"}";
        String result = httpClient.doPost(url, headerMap, request, MediaTypes.APPLICATION_JSON_UTF8);
    }


    @Resource
    PadComplaintListSearch padComplaintListSearch;



    // 测试padTabEnum为null时是否抛出异�?
    @Test
    @DisplayName("测试padTabEnum为null时抛出BusinessException异常")
    public void testGenNewSearchGoIn_nullPadTabEnum() {
        PadComplaintListSearch padComplaintListSearch = new PadComplaintListSearch();
        ComplaintListSearchGoIn goIn = new ComplaintListSearchGoIn();
        assertThrows(BusinessException.class, () -> {
            padComplaintListSearch.genNewSearchGoIn(null, goIn);
        });
    }

    // 测试padTabEnum为TOTAL的情�?
    @Test
    @DisplayName("测试padTabEnum为TOTAL时返回正确结�?)
    public void testGenNewSearchGoIn_TOTAL() {
        PadComplaintListSearch padComplaintListSearch = new PadComplaintListSearch();
        ComplaintListSearchGoIn goIn = new ComplaintListSearchGoIn();
        ComplaintListSearchGoIn result = padComplaintListSearch.genNewSearchGoIn(PadTabEnum.TOTAL, goIn);
        assertEquals(goIn, result);
    }

    // 测试padTabEnum为PENDING_ORDER的情�?
    @Test
    @DisplayName("测试padTabEnum为PENDING_ORDER时设置正确的statusList")
    public void testGenNewSearchGoIn_PENDING_ORDER() {
        PadComplaintListSearch padComplaintListSearch = new PadComplaintListSearch();
        ComplaintListSearchGoIn goIn = new ComplaintListSearchGoIn();
        ComplaintListSearchGoIn result = padComplaintListSearch.genNewSearchGoIn(PadTabEnum.PENDING_ORDER, goIn);
        assertEquals(Collections.singletonList(ComplaintStatusEnum.PENDING_ORDER.getCode()), result.getStatusList());
    }

    // 测试padTabEnum为IN_PROGRESS的情�?
    @Test
    @DisplayName("测试padTabEnum为IN_PROGRESS时设置正确的statusList")
    public void testGenNewSearchGoIn_IN_PROGRESS() {
        PadComplaintListSearch padComplaintListSearch = new PadComplaintListSearch();
        ComplaintListSearchGoIn goIn = new ComplaintListSearchGoIn();
        ComplaintListSearchGoIn result = padComplaintListSearch.genNewSearchGoIn(PadTabEnum.IN_PROGRESS, goIn);
        assertEquals(Arrays.asList(ComplaintStatusEnum.FIRST_RESPONSE_PENDING.getCode(),
                ComplaintStatusEnum.APPLY_FINISH_PENDING.getCode()), result.getStatusList());
    }

    // 测试padTabEnum为APPROACHING_TIMEOUT的情�?
    @Test
    @DisplayName("测试padTabEnum为APPROACHING_TIMEOUT时设置正确的conditionGroups")
    public void testGenNewSearchGoIn_APPROACHING_TIMEOUT() {
        PadComplaintListSearch padComplaintListSearch = new PadComplaintListSearch();
        ComplaintListSearchGoIn goIn = new ComplaintListSearchGoIn();
        ComplaintListSearchGoIn result = padComplaintListSearch.genNewSearchGoIn(PadTabEnum.APPROACHING_TIMEOUT, goIn);

        List<ComplaintListSearchGoIn.ConditionGroup> conditionGroups = result.getConditionGroups();
        assertNotNull(conditionGroups);
        assertEquals(3, conditionGroups.size());

        // 进一步验证第一个ConditionGroup的属�?
        ComplaintListSearchGoIn.ConditionGroup group1 = conditionGroups.get(0);
        assertEquals(Arrays.asList(1, 2), group1.riskLevelList);
        assertEquals(DateUtil.hoursAgo(20), group1.createTimeStart);
        assertEquals(DateUtil.hoursAgo(24), group1.createTimeEnd);
        assertEquals(Arrays.asList(ComplaintStatusEnum.PENDING_ORDER.getCode(),
                ComplaintStatusEnum.ORG_REASSIGN_PENDING.getCode(),
                ComplaintStatusEnum.FIRST_RESPONSE_PENDING.getCode()), group1.statusList);

        // 验证第二个ConditionGroup的属�?
        ComplaintListSearchGoIn.ConditionGroup group2 = conditionGroups.get(1);
        assertEquals(Arrays.asList(3, 4), group2.riskLevelList);
        assertEquals(DateUtil.hoursAgo(8), group2.createTimeStart);
        assertEquals(DateUtil.hoursAgo(12), group2.createTimeEnd);
        assertEquals(Arrays.asList(ComplaintStatusEnum.PENDING_ORDER.getCode(),
                ComplaintStatusEnum.ORG_REASSIGN_PENDING.getCode(),
                ComplaintStatusEnum.FIRST_RESPONSE_PENDING.getCode()), group2.statusList);

        // 验证第三个ConditionGroup的属�?
        ComplaintListSearchGoIn.ConditionGroup group3 = conditionGroups.get(2);
        assertEquals(DateUtil.hoursAgo(60), group3.createTimeStart);
        assertEquals(DateUtil.hoursAgo(72), group3.createTimeEnd);
        assertEquals(Arrays.asList(ComplaintStatusEnum.APPLY_FINISH_PENDING.getCode(),
                ComplaintStatusEnum.FINISH_EVALUATION_PENDING.getCode()), group3.statusList);
    }

    // 测试padTabEnum为FINISH_EVALUATION_PENDING的情�?
    @Test
    @DisplayName("测试padTabEnum为FINISH_EVALUATION_PENDING时设置正确的statusList")
    public void testGenNewSearchGoIn_FINISH_EVALUATION_PENDING() {
        PadComplaintListSearch padComplaintListSearch = new PadComplaintListSearch();
        ComplaintListSearchGoIn goIn = new ComplaintListSearchGoIn();
        ComplaintListSearchGoIn result = padComplaintListSearch.genNewSearchGoIn(PadTabEnum.FINISH_EVALUATION_PENDING, goIn);
        assertEquals(Collections.singletonList(ComplaintStatusEnum.FINISH_EVALUATION_PENDING.getCode()), result.getStatusList());
    }

    // 测试padTabEnum为ONLY_VIEW的情�?
    @Test
    @DisplayName("测试padTabEnum为ONLY_VIEW时设置正确的onlyView属�?)
    public void testGenNewSearchGoIn_ONLY_VIEW() {
        PadComplaintListSearch padComplaintListSearch = new PadComplaintListSearch();
        ComplaintListSearchGoIn goIn = new ComplaintListSearchGoIn();
        ComplaintListSearchGoIn result = padComplaintListSearch.genNewSearchGoIn(PadTabEnum.ONLY_VIEW, goIn);
        assertEquals(OnlyViewEnum.YES.getCode(), result.getOnlyView());
    }

    // 测试padTabEnum为PENDING_REVIEW（待复盘）的情况
    @Test
    @DisplayName("测试padTabEnum为PENDING_REVIEW时设置reviewed=0")
    public void testGenNewSearchGoIn_PENDING_REVIEW() {
        PadComplaintListSearch padComplaintListSearch = new PadComplaintListSearch();
        ComplaintListSearchGoIn goIn = new ComplaintListSearchGoIn();
        ComplaintListSearchGoIn result = padComplaintListSearch.genNewSearchGoIn(PadTabEnum.PENDING_REVIEW, goIn);
        assertEquals(Integer.valueOf(0), result.getReviewed());
    }


    
}
