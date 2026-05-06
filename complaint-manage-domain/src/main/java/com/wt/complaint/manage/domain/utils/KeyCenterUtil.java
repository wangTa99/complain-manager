package com.wt.complaint.manage.domain.utils;

import cn.hutool.crypto.digest.MD5;
import com.xiaomi.keycenter.KeycenterHelper;
import com.xiaomi.keycenter.common.iface.DataProtectionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class KeyCenterUtil {
    @Value("${kc.sid}")
    private String sid;

    @PostConstruct
    public void init() {
        KeycenterHelper.config(sid);
    }


    public static String encrypt(String data) {
        try {
            return KeycenterHelper.encrypt(data);
        } catch (DataProtectionException e) {
            return "";
        }
    }

    public static String decrypt(String data) {
        try {
            return KeycenterHelper.decrypt(data);
        } catch (DataProtectionException e) {
            return "";
        }
    }

    /**
     * 返回32位md5摘要�?
     *
     * @param data
     * @return
     */
    public static String md5(String data) {
        return MD5.create().digestHex(data);
    }
}
