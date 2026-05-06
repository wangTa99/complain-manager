package com.wt.complaint.manage.infrastructure.gatewayimpl.rpc;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import com.wt.complaint.manage.api.model.constont.DubboConstant;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.*;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.*;
import com.wt.complaint.manage.domain.enumInfo.CarEmployeePrivilegeStateEnum;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import static com.wt.complaint.manage.domain.exception.ErrorCodeEnums.THIRD_SERVICE_ERROR;

import com.wt.complaint.manage.domain.utils.RedisUtil;
import com.wt.complaint.manage.infrastructure.converter.EiamConvert;
import com.wt.maindatacommon.constant.DubboConst;
import com.wt.nr.common.utils.GsonUtil;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import com.xiaomi.nr.eiam.api.dto.user.BatchGetUserInfoRequest;
import com.xiaomi.nr.eiam.api.dto.user.GetUserInfoRequest;
import com.xiaomi.nr.eiam.api.service.EmployeeService;
import com.xiaomi.nr.eiam.api.vo.user.GetUserInfoResponse;
import com.xiaomi.nr.eiam.car.api.dto.GetZoneEmployeeRequest;
import com.xiaomi.nr.eiam.car.api.dto.GetZoneEmployeeResponse;
import com.xiaomi.nr.eiam.car.api.dto.employee.*;
import com.xiaomi.nr.eiam.car.api.dto.store.GetStoreEmployeeRequest;
import com.xiaomi.nr.eiam.car.api.service.CarEmployeeService;
import com.xiaomi.nr.eiam.car.api.service.CarPositionService;
import com.xiaomi.nr.eiam.car.api.service.CarStoreService;
import com.xiaomi.nr.eiam.car.api.service.CarZoneService;
import com.xiaomi.nr.eiam.car.api.vo.position.ListByPositionIdAndStateResponse;
import com.xiaomi.nr.eiam.car.api.vo.store.GetStoreEmployeeResponse;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EiamRemoteGatewayImpl implements EiamRemoteGateway {
    private static final int MAX_PAGE = 50;

    private static final byte EFFECTIVE_STATE = 1;
    private static final String USER_NOT_EXIST_MESSAGE = "大区或小区下的岗位不存在";

    @DubboReference(group = "${dubbo.group.eiam}", check = false, interfaceClass = EmployeeService.class, timeout = DubboConstant.TIME_OUT)
    private EmployeeService employeeService;
    @DubboReference(group = "${dubbo.group.eiam}", check = false, interfaceClass = CarStoreService.class, timeout = DubboConstant.TIME_OUT)
    private CarStoreService carStoreService;
    @DubboReference(group = "${dubbo.group.eiam}", check = false, interfaceClass = CarPositionService.class, timeout
            = DubboConstant.TIME_OUT)
    private CarPositionService carPositionService;
    @DubboReference(group = "${dubbo.group.eiam}", check = false, interfaceClass = CarZoneService.class, timeout
            = DubboConstant.TIME_OUT)
    private CarZoneService carZoneService;
    @DubboReference(group = "${dubbo.group.eiam}", check = false, interfaceClass = CarEmployeeService.class, timeout = DubboConst.DUBBO_TIMEOUT)
    private CarEmployeeService carEmployeeService;

    @Resource
    private RedisUtil redisUtil;

    @Value("${server.type}")
    String env;

    private static final String CAR_POSITION_REF_CACHE_KEY = "COMPLAINT_EIAM_CAR_POSITION_REF";

    private static final Type CAR_POSITION_REF_TYPE = new TypeToken<Map<Integer, List<String>>>() {
    }.getType();

    @Override
    public EmployeeInfoGoOut getEmployee(Long mid) {
        List<EmployeeInfoGoOut> employeeListPage = getEmployeeListPage(Collections.singletonList(mid));
        return CollectionUtils.isEmpty(employeeListPage) ? new EmployeeInfoGoOut() : employeeListPage.get(0);
    }

    @Override
    public List<EmployeeInfoGoOut> getEmployeeList(EmployeeListGoIn employeeListGoIn) {
        if (Objects.isNull(employeeListGoIn) || CollectionUtils.isEmpty(employeeListGoIn.getMiIdList())) {
            return Collections.emptyList();
        }
        // 过滤�?的mid
        List<Long> miIdListAll = employeeListGoIn.getMiIdList()
                .stream()
                .distinct()
                .filter(Objects::nonNull)
                .filter(mid -> !mid.equals(0L))
                .collect(Collectors.toList());

        List<EmployeeInfoGoOut> res = new ArrayList<>();
        if (!CollectionUtils.isEmpty(miIdListAll)) {
            List<List<Long>> partitionList = Lists.partition(miIdListAll, MAX_PAGE);
            for (List<Long> midList : partitionList) {
                res.addAll(getEmployeeListPage(midList));
            }
        }
        res.forEach(EmployeeInfoGoOut::fillEmailPrefix);
        return res;
    }

    /**
     * 查询人员mid和姓名的映射关系
     */
    public Map<Long, String> getNameByMid(List<Long> midList) {
        if (CollectionUtils.isEmpty(midList)) {
            return new HashMap<>();
        }
        EmployeeListGoIn midListParam =
                EmployeeListGoIn.builder()
                        .miIdList(midList)
                        .build();
        List<EmployeeInfoGoOut> employeeList = getEmployeeList(midListParam);
        return employeeList
                .stream()
                .collect(Collectors.toMap(EmployeeInfoGoOut::getMiId, EmployeeInfoGoOut::getName,
                        (a, b) -> a));
    }

    @Override
    public List<EmployeeInfoGoOut> queryEmployeeByStore(StoreEmployeeListGoIn goIn) {
        GetStoreEmployeeRequest request = new GetStoreEmployeeRequest();
        request.setOrgId(goIn.getOrgId());
        request.setPrivilegeState(CarEmployeePrivilegeStateEnum.VALID.getCode());
        request.setPositionIdList(goIn.getPositionIdList());
        try {
            log.info("start EiamRemoteGateway#queryEmployeeByStore req{}", GsonUtil.toJson(goIn));
            Result<List<GetStoreEmployeeResponse>> result = carStoreService.getStoreEmployee(request);
            if (result.getCode() != GeneralCodes.OK.getCode()) {
                log.error("组织中台人员查询门店对应角色人员失败,req:{},res:{}", GsonUtil.toJson(goIn),
                        GsonUtil.toJson(result));
                return new ArrayList<>();
            }
            log.info("queryEmployeeByStore 组织中台人员查询门店对应角色人员成功,req:{},res:{}", GsonUtil.toJson(goIn),
                    GsonUtil.toJson(result));
            List<EmployeeInfoGoOut> infoBOList = result.getData()
                    .stream()
                    .map(obj -> new EmployeeInfoGoOut(obj.getMiId(), obj.getName(), obj.getPositionId(), obj.getPhone(),
                            obj.getEmail(), ""))
                    .collect(Collectors.toList());
            infoBOList.forEach(EmployeeInfoGoOut::fillEmailPrefix);
            return infoBOList;
        } catch (Exception e) {
            log.error("组织中台人员查询门店对应角色人员失败,req:{},e:", GsonUtil.toJson(request), e);
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "查询汽车组织中台门店对应角色人员失败");
        }
    }

    @Override
    public List<ZonePositionUserGoOut> getZonePositionUser(ZonePositionUserGoIn goIn) {
        log.info("start call getZonePositionUser, goIn:{}", GsonUtil.toJson(goIn));
        GetZonePositionUserRequest request = new GetZonePositionUserRequest();
        request.setBigZoneIdList(goIn.getBigZoneIdList());
        // 过滤�?�?因为这个是默认�?
        if (!CollectionUtils.isEmpty(goIn.getBigZoneIdList())) {
            request.setBigZoneIdList(goIn.getBigZoneIdList().stream().filter(e -> e != 0).collect(Collectors.toList()));
        }
        if (!CollectionUtils.isEmpty(goIn.getLittleZoneIdList())) {
            request.setLittleZoneIdList(
                    goIn.getLittleZoneIdList().stream().filter(e -> e != 0).collect(Collectors.toList()));
        }
        if (!CollectionUtils.isEmpty(goIn.getCityZoneIdList())) {
            request.setCityZoneIdList(
                    goIn.getCityZoneIdList().stream().filter(e -> e != 0).collect(Collectors.toList()));
        }
        request.setPositionId(goIn.getPositionId());
        // 这里大区id列表或小区id列表不能都为�?否则下游会报�?
        if (CollectionUtils.isEmpty(request.getBigZoneIdList()) &&
                CollectionUtils.isEmpty(request.getLittleZoneIdList()) &&
                CollectionUtils.isEmpty(request.getCityZoneIdList())) {
            log.error("warn getZonePositionUser param wrong, request:{}",
                    GsonUtil.toJson(request));
            return Collections.emptyList();
        }
        Result<List<GetZonePositionUserResponse>> result;
        try {
            log.info("start carPositionService.getZonePositionUser, request:{}", GsonUtil.toJson(request));
            result = carPositionService.getZonePositionUser(request);
            log.info("success call carPositionService.getZonePositionUser, request:{}, result:{}",
                    GsonUtil.toJson(request), GsonUtil.toJson(result));
        } catch (Exception e) {
            log.error("call carPositionService.getZonePositionUser error, request:{}", GsonUtil.toJson(request), e);
            throw new BusinessException(THIRD_SERVICE_ERROR, "根据区域查询用户信息异常");
        }
        if (result == null) {
            log.error("call carPositionService.getZonePositionUser result is null, request:{}, result:{}",
                    GsonUtil.toJson(request), GsonUtil.toJson(result));
            throw new BusinessException(THIRD_SERVICE_ERROR, "根据区域查询用户信息返回结果为空");
        }
        if (result.getCode() != GeneralCodes.OK.getCode() || result.getData() == null) {
            if (USER_NOT_EXIST_MESSAGE.equals(result.getMessage())) {
                log.info("该区域下,岗位下人员不存在,返回空列�?request:{},result:{}",
                        GsonUtil.toJson(request), GsonUtil.toJson(result));
                return Collections.emptyList();
            }
            log.error("call carPositionService.getZonePositionUser fail, request:{}, result:{}",
                    GsonUtil.toJson(request), GsonUtil.toJson(result));
            throw new BusinessException(THIRD_SERVICE_ERROR, "根据区域查询用户信息失败");
        }
        return EiamConvert.INSTANCE.toGoOutList(result.getData());
    }

    @Override
    public List<UserBaseInfoGoOut> listByPositionIdAndState(List<Integer> positionIdList, Integer pageNum,
                                                            Integer pageSize) {
        ListByPositionIdAndStateRequest request = new ListByPositionIdAndStateRequest();
        request.setPositionIdList(positionIdList);
        request.setPositionStates(Collections.singletonList(EFFECTIVE_STATE));
        request.setPrivilegeStates(Collections.singletonList(EFFECTIVE_STATE));
        request.setPageNum(pageNum);
        request.setPageSize(pageSize);
        log.info("start call listByPositionIdAndState, request:{}", GsonUtil.toJson(request));
        Result<ListByPositionIdAndStateResponse> result;
        try {
            result = carPositionService.listByPositionIdAndState(request);
            log.info("success call carPositionService.listByPositionIdAndState, request:{}, result:{}",
                    GsonUtil.toJson(request), GsonUtil.toJson(result));
        } catch (Exception e) {
            log.error("call carPositionService.listByPositionIdAndState error, request:{}", GsonUtil.toJson(request),
                    e);
            throw new BusinessException(THIRD_SERVICE_ERROR, "通过岗位获取人员列表请求异常");
        }
        if (result == null || result.getCode() != GeneralCodes.OK.getCode() || result.getData() == null) {
            log.error("call carPositionService.listByPositionIdAndState fail, request:{}, result:{}",
                    GsonUtil.toJson(request), GsonUtil.toJson(result));
            throw new BusinessException(THIRD_SERVICE_ERROR, "通过岗位获取人员列表请求失败");
        }
        List<ListByPositionIdAndStateResponse.UserBaseInfo> userList = result.getData().getUserList();
        return EiamConvert.INSTANCE.toUserGoOutList(userList);
    }

    private List<EmployeeInfoGoOut> getEmployeeListPage(List<Long> midList) {
        BatchGetUserInfoRequest request = new BatchGetUserInfoRequest();
        request.setMiIdList(midList);
        try {
            log.info("EiamRemoteGatewayImpl#getEmployeeListPage req{}", GsonUtil.toJson(request));
            Result<List<GetUserInfoResponse>> result = employeeService.batchGetUserInfo(request);
            log.info("EiamRemoteGatewayImpl#getEmployeeListPage res{}", GsonUtil.toJson(result));
            if (result.getCode() != GeneralCodes.OK.getCode()) {
                log.error("组织中台人员查询失败,EiamRemoteGatewayImpl#getEmployeeListPage req:{}," +
                                "EiamRemoteGatewayImpl#getEmployeeListPage res:{}", GsonUtil.toJson(request),
                        GsonUtil.toJson(result));
                return new ArrayList<>();
            }
            log.info(
                    "组织中台人员查询成功,EiamRemoteGatewayImpl#getEmployeeListPage req:{}, EiamRemoteGatewayImpl#getEmployeeListPage " +
                            "res:{}", GsonUtil.toJson(request),
                    GsonUtil.toJson(result));
            return EiamConvert.INSTANCE.toBoList(result.getData());
        } catch (Exception e) {
            log.error("组织中台人员查询失败,EiamRemoteGatewayImpl#getEmployeeListPage req:{},e:", GsonUtil.toJson(request),
                    e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<GetZoneEmployeeGoOut> getZoneEmployee(GetZoneEmployeeGoIn getZoneEmployeeGoIn) {
        GetZoneEmployeeRequest request = new GetZoneEmployeeRequest();
        request.setZoneId(getZoneEmployeeGoIn.getZoneId());
        request.setPositionId(getZoneEmployeeGoIn.getPositionId());
        request.setPrivilegeState(1);
        try {
            log.info("EiamRemoteGatewayImpl#getZoneEmployee req{}", GsonUtil.toJson(request));
            Result<List<GetZoneEmployeeResponse>> result = carZoneService.getZoneEmployee(request);
            log.info("EiamRemoteGatewayImpl#getZoneEmployee res{}", GsonUtil.toJson(result));
            if (result.getCode() != GeneralCodes.OK.getCode()) {
                log.error("组织中台人员查询失败,EiamRemoteGatewayImpl#getZoneEmployee req:{}," +
                                "EiamRemoteGatewayImpl#getZoneEmployee res:{}", GsonUtil.toJson(request),
                        GsonUtil.toJson(result));
                return new ArrayList<>();
            }
            log.info(
                    "组织中台人员查询成功,EiamRemoteGatewayImpl#getZoneEmployee req:{},EiamRemoteGatewayImpl#getZoneEmployee " +
                            "res:{}", GsonUtil.toJson(request),
                    GsonUtil.toJson(result));
            return EiamConvert.INSTANCE.toZoneEmployeeGoOut(result.getData());
        } catch (Exception e) {
            log.error("组织中台人员查询失败,EiamRemoteGatewayImpl#getZoneEmployee req:{},e:", GsonUtil.toJson(request),
                    e);
            return new ArrayList<>();
        }
    }

    public EmployeeInfoGoOut getEmployeeInfoByEmail(String email) {
        GetUserInfoRequest request = new GetUserInfoRequest();
        request.setEmail(email);
        try {
            log.info("EiamRemoteGatewayImpl#getEmployeeInfoByEmail req{}", GsonUtil.toJson(request));
            Result<GetUserInfoResponse> result = employeeService.getUserInfo(request);
            log.info("EiamRemoteGatewayImpl#getEmployeeInfoByEmail res{}", GsonUtil.toJson(result));
            if (result.getCode() != GeneralCodes.OK.getCode()) {
                log.error("组织中台人员查询失败,EiamRemoteGatewayImpl#getEmployeeInfoByEmail req:{}," +
                                "EiamRemoteGatewayImpl#getEmployeeInfoByEmail res:{}",
                        GsonUtil.toJson(request), GsonUtil.toJson(result));
                return null;
            }
            log.info("组织中台人员查询成功,EiamRemoteGatewayImpl#getEmployeeInfoByEmail req:{}," +
                            "EiamRemoteGatewayImpl#getEmployeeInfoByEmail res:{}",
                    GsonUtil.toJson(request), GsonUtil.toJson(result));
            return EiamConvert.INSTANCE.toBo(result.getData());
        } catch (Exception e) {
            log.error("组织中台人员查询失败,EiamRemoteGatewayImpl#getEmployeeInfoByEmail req:{},e:",
                    GsonUtil.toJson(request), e);
            return null;
        }
    }

    @Override
    public Map<Integer, List<String>> getCarPositionRef() {
        Map<Integer, List<String>> cache = redisUtil.getCache(CAR_POSITION_REF_CACHE_KEY + "-" + env, CAR_POSITION_REF_TYPE);

        if (MapUtil.isNotEmpty(cache)) {
            log.info("CarEmployeeGatewayImpl.getCarPositionRef cache resp : {}", RetailJsonUtil.toJson(cache));
            return cache;
        }

        Result<Map<Integer, List<String>>> positionRoleMap = carPositionService.getPositionRoleMap();
        log.info("CarEmployeeGatewayImpl.getCarPositionRef resp : {}", RetailJsonUtil.toJson(positionRoleMap));
        if (positionRoleMap == null || MapUtil.isEmpty(positionRoleMap.getData())) {
            log.error("缺失关键数据！获取组织中台岗位角色映射为空！");
            throw new RuntimeException("缺失关键数据！获取组织中台岗位角色映射为空！");
        }
        Map<Integer, List<String>> positionRef = positionRoleMap.getData();
        redisUtil.setCache(CAR_POSITION_REF_CACHE_KEY + "-" + env, positionRef, 10 * 60);
        return positionRef;
    }

    @Override
    public EmployeeInfoResult queryCarEmployeeV2(GetEmployeeInfoParam param) {
        if (Objects.isNull(param)) {
            return new EmployeeInfoResult();
        }
        GetEmployeeInfoRequest request = Convert.convert(GetEmployeeInfoRequest.class, param);
        try {
            log.info("queryCarEmployeeV2 req{}", GsonUtil.toJson(request));
            Result<EmployeeInfoResponse> employeeInfoV2 = carEmployeeService.getEmployeeInfoV2(request);
            if (employeeInfoV2.getCode() != GeneralCodes.OK.getCode()) {
                log.error("组织中台人员查询失败,getEmployeeInfoV2.res:{}", GsonUtil.toJson(employeeInfoV2));
                return new EmployeeInfoResult();
            }
            log.info("组织中台查询人员信息成功,getEmployeeInfoV2.res:{}", GsonUtil.toJson(employeeInfoV2));
            return Convert.convert(EmployeeInfoResult.class, employeeInfoV2.getData());
        } catch (Exception e) {
            log.error("组织中台人员查询失败,getEmployeeInfoV2,e:", e);
            return new EmployeeInfoResult();
        }
    }

}
