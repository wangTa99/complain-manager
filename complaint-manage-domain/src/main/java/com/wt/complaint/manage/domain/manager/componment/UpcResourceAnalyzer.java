package com.wt.complaint.manage.domain.manager.componment;

import cn.hutool.core.collection.CollUtil;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.UtilityRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.UpcConfigGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.UpcModuleConfigGoOut;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.utils.UpcConfigBotHookUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 天工资源解析�?
 */
@Component
@Slf4j
public class UpcResourceAnalyzer {

    /** -------------------------- FLAG ---------------------- **/
    // 合法�?模块 标识
    private static final List<String> LEGAL_MODULE_KEYS = Arrays.asList(
            "complaintFrame"
    );

    // 合法�?按钮 标识
    private static final List<String> LEGAL_BUTTON_KEYS = Arrays.asList(
            // 工单详情
            "applyReassignStore",
            "dispatch",
            "pickUp",
            "addFollowUpRecords",
            "applyExemption",
            "appointmentMROrder",
            "issuePoints",
            "reassignHandler",
            "apply72HUnfinished",
            "applyFinish",
            "upgradeComplaint",
            "submitReview"
    );

    // 合法�?函数 标识
    private static final List<String> LEGAL_FUNC_KEYS = Arrays.asList(
            "applyOrgChange",
            "applyNoDutyV2",
            "apply72NoFinishV2",
            "applyFinishV2",
            "applySubmitReview"
    );

    // 合法�?条件 标识
    private static final List<String> LEGAL_CONDITION_KEYS = Arrays.asList(
            "status"
    );

    /** -------------------------- BEAN ---------------------- **/

    @Resource
    private UtilityRemoteGateway utilityGateway;

    @Value("${server.type}")
    private String env;

    /** -------------------------- 函数 ---------------------- **/

    public List<String> getLegalModuleKeys() {
        return LEGAL_MODULE_KEYS;
    }

    // 处理返回�?
    public Map<String, List<String>> getUpcConfigByModules(List<String> modules) {

        List<UpcModuleConfigGoOut> configList = utilityGateway.getUpcConfigByModules(modules);

        try {
            checkConfigList(configList);
        } catch (Exception e) {
            UpcConfigBotHookUtil.text("[complaint-manage] rpc 拉取配置失败, 请关�? " + e, env);
            throw e;
        }

        Map<String, List<String>> result = new HashMap<>();
        for (UpcModuleConfigGoOut conf : configList) {
            result.put(conf.getModuleKey() + "|" + conf.getRoleKey(),
                       conf.getConfigs().stream()
                                        .map(UpcConfigGoOut::getResourceTag)
                                        .collect(Collectors.toList()));
        }

        return result;
    }


    /**
     * 合法性检�?
     * (基本的配置合法性由 utility 保证，这里主要保证业务合法�?
     * ------------------------
     * 1. resourceTag 应该能被 . 分割成若干小节，第一节是模块，最后一节是按钮
     * 2. module key 是否合法
     * 3. 去掉第一节跟最后一节的，中间应该都是包�?_ 的判断条�?
     * 4. 每个条件都应该能�?_ 分割�?长度�?2 的数�?
     * 5. 判断 条件 key 是否合法
     * 6. 最后一节按钮，如果有特殊函数的�? 可以�?_ 分割成按钮跟函数
     * 7. 按钮 key 是否合法
     * 8. 函数 key 是否合法
     */
    public static void checkConfigList(List<UpcModuleConfigGoOut> configList) {
        for (UpcModuleConfigGoOut config : configList) {
            List<UpcConfigGoOut> configs = config.getConfigs();
            for (UpcConfigGoOut upcConfigGoOut : configs) {
                String resourceTag = upcConfigGoOut.getResourceTag();
                if (!LEGAL_MODULE_KEYS.contains(getModule(resourceTag))) {
                    throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "配置 key 不在白名单中, " + resourceTag);
                }
                if (!new HashSet<>(LEGAL_CONDITION_KEYS).containsAll(getConditionKeys(resourceTag))) {
                    throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "条件 key 不在白名单中, " + resourceTag);
                }
                if (!LEGAL_BUTTON_KEYS.contains(getButton(resourceTag))) {
                    throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "按钮 key 不在白名单中, " + resourceTag);
                }
                if (!new HashSet<>(LEGAL_FUNC_KEYS).containsAll(getFunctions(resourceTag))) {
                    throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "函数 key 不在白名单中, " + resourceTag);
                }
            }
        }
    }


    /** -------------------------- 解析单个 resource key ---------------------- **/
    // 获取 模块 key
    public static String getModule(String resourceTag) {
        return CollUtil.toList(resourceTag.split("\\.")).get(0);
    }

    // 获取 完整条件
    public static List<String> getConditions(String resourceTag) {
        List<String> sections = CollUtil.toList(resourceTag.split("\\."));
        if (sections.size() < 2) {
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "获取完整条件失败，错误的配置 key �? " + resourceTag);
        }
        sections.remove(sections.size() - 1);
        sections.remove(0);
        return sections;
    }

    // 获取 条件 key
    public static List<String> getConditionKeys(String resourceTag) {
        List<String> result = new ArrayList<>();
        List<String> conditions = getConditions(resourceTag);
        for (String condition : conditions) {
            List<String> sections = CollUtil.toList(condition.split("_"));
            if (sections.size() < 2) {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "拆分条件失败，错误的配置 key �? " + resourceTag);
            }
            result.add(sections.get(0));
        }
        return result;
    }

    // 获取 按钮
    public static String getButton(String resourceTag) {
        List<String> list = CollUtil.toList(resourceTag.split("\\."));
        return CollUtil.toList(list.get(list.size() - 1).split("_")).get(0);
    }

    // 获取 函数
    public static List<String> getFunctions(String resourceTag) {
        List<String> list = CollUtil.toList(resourceTag.split("\\."));
        List<String> sections = CollUtil.toList(list.get(list.size() - 1).split("_"));
        sections.remove(0);
        return sections;
    }

    // 获取按钮和函�?
    public static String getFullButton(String resourceTag) {
        List<String> sections = CollUtil.toList(resourceTag.split("\\."));
        return sections.get(sections.size() - 1);
    }
}
