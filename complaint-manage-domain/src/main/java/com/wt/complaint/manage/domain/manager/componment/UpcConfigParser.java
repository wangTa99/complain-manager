package com.wt.complaint.manage.domain.manager.componment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.GetEmployeeInfoParam;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.rpc.UpcConfigGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoResult;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.manager.UserActionAuthContext;
import com.wt.complaint.manage.domain.manager.UserAuthManager;
import com.wt.nr.common.utils.GsonUtil;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@SuppressWarnings({"squid:S3252", "squid:S3776"})
public class UpcConfigParser {

    @Resource
    UserAuthManager userAuthManager;

    @Resource
    EiamRemoteGateway eiamRemoteGateway;

    // 根据入参获取角色
    public List<String> getRoleList(UpcConfigGoIn soIn) {
        log.info("getRoleList soIn:{}", RetailJsonUtil.toJson(soIn));
        if (StrUtil.isBlank(soIn.getOrgId())) {
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "获取角色时门�?ID 不能为空");
        }

        List<String> roleKeyList = new ArrayList<>();
        try {
            // 获取岗位映射关系 �?此人在门店下有效的岗�?
            Map<Integer, List<String>> carPositionRef = eiamRemoteGateway.getCarPositionRef();
            EmployeeInfoResult employeeInfoResult = eiamRemoteGateway.queryCarEmployeeV2(GetEmployeeInfoParam.builder()
                                                                                                             .miId(Long.valueOf(soIn.getMid()))
                                                                                                             .build());
            if (employeeInfoResult != null && CollUtil.isNotEmpty(employeeInfoResult.getStorePositions())) {
                List<EmployeeInfoResult.StorePosition> storePositions = employeeInfoResult.getStorePositions();
                List<Integer> positionList = storePositions.stream()
                                                           // 岗位有效
                                                           .filter(t -> 1 == t.getPrivilegeState())
                                                           // 本门店内
                                                           .filter(t -> StrUtil.equals(soIn.getOrgId(), t.getOrgId()))
                                                           .map(EmployeeInfoResult.StorePosition::getPositionId)
                                                           .filter(Objects::nonNull)
                                                           .distinct()
                                                           .collect(Collectors.toList());
                // 用映射转成角�?
                roleKeyList = positionList.stream()
                                          .map(t -> carPositionRef.getOrDefault(t, new ArrayList<>()))
                                          .flatMap(List::stream)
                                          .distinct()
                                          .collect(Collectors.toList());
            }
            log.info("getRoleList roleKeyList:{}", RetailJsonUtil.toJson(roleKeyList));
        } catch (Exception e) {
            log.error("[UpcConfigParser#getRoleList] get upc role key list failed. soIn:{}", RetailJsonUtil.toJson(soIn), e);
        }

        // 如果异常，使用当前角�?
        if (CollUtil.isEmpty(roleKeyList)) {
            roleKeyList.add(soIn.getCurrRole());
        }

        log.info("getRoleList soOut:{}", RetailJsonUtil.toJson(roleKeyList));
        return roleKeyList;
    }

    // 计算按钮
    public List<String> calcButtons(List<String> resources,
                                    UserActionAuthContext context,
                                    Object obj) {
        log.info("calcButtons resources:{}, context:{},obj:{}", RetailJsonUtil.toJson(resources), RetailJsonUtil.toJson(context), RetailJsonUtil.toJson(obj));
        long startTime = System.currentTimeMillis();

        // 1. 过滤符合规则的资�?
        List<String> filterResources = resources.stream()
                                                .filter(t -> {
                                                    List<String> conditions = UpcResourceAnalyzer.getConditions(t);
                                                    // 1. 如果没规则，放行
                                                    if (CollUtil.isEmpty(conditions)) {
                                                       return true;
                                                    }

                                                    // 2. 如果有规则，判定是否命中规则
                                                    boolean contains = false;
                                                    for (String condition : conditions) {
                                                        List<String> ruleList = CollUtil.toList(condition.split("_"));
                                                        String key = ruleList.remove(0);

                                                        // 2.1 反射获取规则所需字段
                                                        Object fieldValue = BeanUtil.getFieldValue(obj, key);
                                                        if (fieldValue == null) {
                                                            log.warn("Field value is null for field:{}, obj:{}", key, GsonUtil.toJson(obj));
                                                            continue;
                                                        }

                                                        // 2.2 计算字段值是否匹配规�?
                                                        try {
                                                            contains = ruleList.stream()
                                                                               .map(r -> Convert.convert(fieldValue.getClass(), r))
                                                                               .collect(Collectors.toList())
                                                                               .contains(fieldValue);
                                                        } catch (Exception e) {
                                                            log.error("Failed to convert and compare, key:{}, fieldValue:{}", key, GsonUtil.toJson(fieldValue), e);
                                                            continue;
                                                        }

                                                        if (!contains) {
                                                            break;
                                                        }
                                                    }
                                                    // 2.3 规则通过，放�?
                                                    return contains;
                                                })
                                                .collect(Collectors.toList());
        log.info("本次判定命中并生效的资源：resource:{}, obj:{}", GsonUtil.toJson(filterResources), GsonUtil.toJson(obj));


        // 2. 过滤符合规则的资�?
        Set<String> functions = new HashSet<>();
        for (String resource : filterResources) {
            List<String> func = UpcResourceAnalyzer.getFunctions(resource);
            if (func.size() >= 2) {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "暂时不支持多函数资源," + resource);
            }
            functions.addAll(func);
        }


        // 3. 计算生效函数结果
        // todo: 改成并行
        Map<String, Boolean> funcRes = new HashMap<>();
        for (String function : functions) {
            try {
                Method func = UserAuthManager.class.getMethod(function, context.getClass());
                Boolean res = (Boolean) func.invoke(userAuthManager, context);
                funcRes.put(function, res);
            } catch (Exception e) {
                log.error("function invoke failed, func:{}", function, e);
                funcRes.put(function, false);
            }
        }
        log.info("函数判定结果: funcRes:{}", GsonUtil.toJson(funcRes));


        // 4. 匹配可用按钮
        Set<String> buttons = new HashSet<>();
        for (String resource : filterResources) {
            String fullButton = UpcResourceAnalyzer.getFullButton(resource);
            List<String> sections = CollUtil.toList(fullButton.split("_"));
            String key = sections.remove(0);

            // 判定是否命中函数逻辑
            boolean res = true;
            for (String func : sections) {
                if (!funcRes.getOrDefault(func, false)) {
                    res = false;
                    break;
                }
            }
            if (res) {
                buttons.add(key);
            }
        }

        return new ArrayList<>(buttons);
    }
}
