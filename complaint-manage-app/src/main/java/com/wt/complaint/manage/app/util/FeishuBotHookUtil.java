package com.wt.complaint.manage.app.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.google.gson.annotations.SerializedName;
import com.wt.nr.common.utils.GsonUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class FeishuBotHookUtil {
    private static final String FEI_SHU_HOOK_URL = "https://open.feishu.cn/open-apis/bot/v2/hook/c8033731-de97-4501-ba5d-6a2bebc1a64c";

    @Getter
    private static class TextRequest {

        @SerializedName("msg_type")
        private String msgType = "text";
        private Map<String, String> content = new HashMap<>();

        TextRequest(String message) {
            content.put("text", message);
        }
    }

    public static boolean text(String text, String env) {
        String hookUrl = FEI_SHU_HOOK_URL;
        TextRequest request = new TextRequest(env.toUpperCase() + ":" + text);
        String body = GsonUtil.toJson(request);
        HttpResponse response = sendRequest(hookUrl, body);
        boolean ret = response.isOk();
        if (!ret) {
            log.error("FeishuBotHookUtil-text err,response:{}", response.toString());
        } else {
            log.info("FeishuBotHookUtil-text success,response:{}", response.toString());
        }
        return ret;
    }

    private static HttpResponse sendRequest(String hookUrl, String body) {
        log.info("FeishuBotHookUtil-post body:{}", body);
        HttpRequest post = HttpUtil.createPost(hookUrl);
        post.header("Content-Type", "application/json").
                body(body);
        return post.execute();
    }

    public static void main(String[] args) {
        text("BPM 流程异常: 测试发送消�?, "dev");
    }

}
