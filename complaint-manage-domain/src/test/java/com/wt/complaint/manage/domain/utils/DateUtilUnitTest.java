package com.wt.complaint.manage.domain.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author zhangzheyang
 * @date 2025/7/28
 */
@ExtendWith(MockitoExtension.class)
public class DateUtilUnitTest {


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testHoursAgo() {
        String result = DateUtil.hoursAgo(24);
        System.out.println("testHoursAgo: " + "result");
        Assertions.assertNotNull(result);
    }
}
