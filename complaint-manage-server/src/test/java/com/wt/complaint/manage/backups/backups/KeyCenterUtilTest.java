package com.wt.complaint.manage.backups;

import com.wt.complaint.manage.domain.utils.KeyCenterUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author zhangzheyang
 * @date 2025/1/4
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class KeyCenterUtilTest {

    @Test
    public void testEncrypt() {
        System.out.println(KeyCenterUtil.encrypt("bks5bSI9cE1gHvkbabmoFwkl"));
    }
}
