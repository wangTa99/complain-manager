package com.wt.complaint.manage.infrastructure.gatewayimpl.rpc;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import com.wt.complaint.manage.api.model.ProvinceDTO;
import com.wt.complaint.manage.api.model.constont.DubboConstant;
import com.wt.complaint.manage.api.model.resp.CityZoneDTO;
import com.wt.complaint.manage.api.model.resp.LittleZoneDTO;
import com.wt.complaint.manage.api.model.resp.ZoneDTO;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.GetProvinceCityHasStoreGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ZoneListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarStoreListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.maindataapi.api.ProvinceCityProvider;
import com.wt.maindataapi.api.StoreProvider;
import com.wt.maindataapi.api.ZoneProvider;
import com.wt.maindataapi.model.OrgDataDto;
import com.wt.maindataapi.model.OrgResponse;
import com.wt.maindataapi.model.PageResp;
import com.wt.maindataapi.model.dto.province.GetProvinceCityHasStoreResponse;
import com.wt.maindataapi.model.dto.zone.CityZoneDto;
import com.wt.maindataapi.model.dto.zone.LittleZoneDto;
import com.wt.maindataapi.model.dto.zone.LittleZoneListDto;
import com.wt.maindataapi.model.dto.zone.ZoneDto;
import com.wt.maindataapi.model.dto.zone.ZoneListDto;
import com.wt.maindataapi.model.req.GetProvinceCityHasStoreRequest;
import com.wt.maindataapi.model.req.org.OrgBaseEntity;
import com.wt.maindataapi.model.req.store.StoreListAllRequest;
import com.wt.maindataapi.model.req.zone.CityZoneListReq;
import com.wt.maindataapi.model.req.zone.LittleZoneListReq;
import com.wt.maindataapi.model.req.zone.ZoneListReq;
import com.wt.nr.common.utils.GsonUtil;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;
import com.xiaomi.nr.order.api.dto.response.orderinfobackend.OrderDetailResp;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import javax.annotation.Resource;

@Slf4j
@Service
public class StoreRemoteGatewayImpl implements StoreRemoteGateway {

    private static final int MAX_PAGE = 500;

    /**
     * 查询分区信息最大分页数�?
     */
    private static final int MAX_ZONE_PAGE_SIZE = 20;

    @DubboReference(interfaceClass = StoreProvider.class,
            group = "${dubbo.group.store}",
            version = "1.0",
            timeout = DubboConstant.TIME_OUT)
    private StoreProvider storeProvider;

    @DubboReference(interfaceClass = ZoneProvider.class,
            group = "${dubbo.group.store}",
            version = "1.0",
            timeout = DubboConstant.TIME_OUT)
    private ZoneProvider zoneProvider;

    @DubboReference(interfaceClass = ProvinceCityProvider.class,
            group = "${dubbo.group.store}",
            version = "1.0",
            timeout = DubboConstant.TIME_OUT)
    private ProvinceCityProvider provinceCityProvider;

    private static final int PAGE_SIZE = 20; // 每页查询数量

    private static final String GET_ZONE_LIST_MESSAGE = "获取大区列表信息失败"; // 获取大区列表信息失败

    @Resource
    private MoneThreadPoolExecutor commonThreadPoolExecutor;

    @Override
    public List<StoreInfoGoOut> listCarStore(CarStoreListGoIn carStoreListParam) {
        if (!carStoreListParam.validParam()) {
            return new ArrayList<>();
        }
        StoreListAllRequest req = new StoreListAllRequest();
        req.setOrgId(carStoreListParam.getOrgIdList());
        req.setFilter(carStoreListParam.getFilter());
        req.setGroupId(carStoreListParam.getGroupIdList());
        req.setNeedReturnTestStore(true);
        req.setPageIndex(1);
        req.setPageSize(MAX_PAGE);
        Result<OrgResponse> resData = null;
        try {
            resData = storeProvider.selectStoreListBeta(req);
            log.info("listCarStore req:{},res:{}", GsonUtil.toJson(req), GsonUtil.toJson(resData));
            List<OrgBaseEntity> orgBaseList = Optional.ofNullable(resData.getData()).map(OrgResponse::getOrgList)
                    .orElse(new ArrayList<>()).stream().map(OrgDataDto::getOrgBase).collect(Collectors.toList());
            List<StoreInfoGoOut> storeInfoList = new ArrayList<>();
            for (OrgBaseEntity orgBaseEntity : orgBaseList) {
                StoreInfoGoOut storeInfo = StoreInfoGoOut.builder()
                        .orgId(orgBaseEntity.getOrgId())
                        .orgName(orgBaseEntity.getShopName())
                        .cityId(Objects.nonNull(orgBaseEntity.getCity()) ? orgBaseEntity.getCity().toString() : "")
                        .zoneId(orgBaseEntity.getZoneId())
                        .zoneCode(orgBaseEntity.getZoneCode())
                        .littleZoneId(orgBaseEntity.getLittleZoneId())
                        .littleZoneCode(orgBaseEntity.getLittleZoneCode())
                        .cityZoneId(orgBaseEntity.getCityZoneId())
                        .cityZoneCode(orgBaseEntity.getCityZoneCode())
                        .businessMode(orgBaseEntity.getBusinessMode())
                        .build();
                storeInfoList.add(storeInfo);
            }
            return storeInfoList;
        } catch (Exception e) {
            log.error("listCarStore err req:{},e:", GsonUtil.toJson(req), e);
            return Collections.emptyList();
        }
    }

    @Override
    public StoreInfoGoOut getStoreInfo(String orgId) {
        CarStoreListGoIn carStoreListGoIn = new CarStoreListGoIn();
        carStoreListGoIn.setFilter(new String[] {"base"});
        carStoreListGoIn.setOrgIdList(Collections.singletonList(orgId));
        log.info("start call StoreRemoteGateway#getStoreInfo, req:{}", RetailJsonUtil.toJson(carStoreListGoIn));
        List<StoreInfoGoOut> storeInfoGoOuts = listCarStore(carStoreListGoIn);
        StoreInfoGoOut carStore =
                CollectionUtils.isEmpty(storeInfoGoOuts) ? StoreInfoGoOut.builder().build() : storeInfoGoOuts.get(0);
        log.info("success call getStoreInfo, req:{}, res:{}",
                RetailJsonUtil.toJson(carStoreListGoIn), RetailJsonUtil.toJson(carStore));
        return carStore;
    }

    @Override
    public List<StoreInfoGoOut> getStoreListInfo(List<String> orgIdList) {
        CarStoreListGoIn carStoreListGoIn = new CarStoreListGoIn();
        carStoreListGoIn.setFilter(new String[] {"base"});
        carStoreListGoIn.setOrgIdList(orgIdList);
        log.info("start call StoreRemoteGateway#getStoreListInfo, req:{}", RetailJsonUtil.toJson(carStoreListGoIn));
        List<StoreInfoGoOut> storeInfoGoOuts = listCarStore(carStoreListGoIn);
        log.info("success StoreRemoteGateway#getStoreListInfo, req:{}, res:{}",
                RetailJsonUtil.toJson(carStoreListGoIn), RetailJsonUtil.toJson(storeInfoGoOuts));
        return storeInfoGoOuts;
    }

    @Override
    public Map<String, String> getStoreNameMap(List<String> orgIdList) {
        List<StoreInfoGoOut> storeListInfo = getStoreListInfo(orgIdList);

        return storeListInfo.stream().collect(Collectors.toMap(StoreInfoGoOut::getOrgId,
                StoreInfoGoOut::getOrgName, (a, b) -> a));
    }

    @Override
    public List<ZoneDTO> getZoneList(List<Integer> zoneIdList) {
        if (CollectionUtil.isEmpty(zoneIdList)) {
            return Collections.emptyList();
        }
        ZoneListReq zoneListReq = new ZoneListReq();
        zoneListReq.setZoneIdList(zoneIdList);
        try {
            log.info("getZoneList req:{}", GsonUtil.toJson(zoneIdList));
            Result<ZoneListDto> result = zoneProvider.getZoneList(zoneListReq);
            log.info("getZoneList resp:{}", GsonUtil.toJson(result));
            if (result == null || GeneralCodes.OK.getCode() != result.getCode() || result.getData() == null) {
                throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, GET_ZONE_LIST_MESSAGE);
            }
            ZoneListDto data = result.getData();
            List<ZoneDto> list = data.getList();
            return list.stream()
                    .map(zoneDto -> ZoneDTO.builder()
                            .zoneId(zoneDto.getZoneId())
                            .zoneCode(zoneDto.getZoneCode())
                            .zoneName(zoneDto.getZoneName())
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("getZoneList err req:{},e:", GsonUtil.toJson(zoneIdList), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<LittleZoneDTO> getLittleZoneList(List<Integer> littlezoneIdList) {
        if (CollectionUtil.isEmpty(littlezoneIdList)) {
            return Collections.emptyList();
        }
        LittleZoneListReq littleZoneListReq = new LittleZoneListReq();
        littleZoneListReq.setLittleZoneIdList(littlezoneIdList);
        try {
            log.info("getLittleZoneList req:{}", GsonUtil.toJson(littlezoneIdList));
            Result<LittleZoneListDto> result = zoneProvider.getLittleZoneList(littleZoneListReq);
            log.info("getLittleZoneList resp:{}", GsonUtil.toJson(result));
            if (result == null || GeneralCodes.OK.getCode() != result.getCode() || result.getData() == null) {
                throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "获取小区列表信息失败");
            }
            LittleZoneListDto data = result.getData();
            List<LittleZoneDto> list = data.getList();
            return list.stream()
                    .map(littleZoneDto -> LittleZoneDTO.builder()
                            .littleZoneId(littleZoneDto.getLittleZoneId())
                            .littleZoneCode(littleZoneDto.getLittleZoneCode())
                            .littleZoneName(littleZoneDto.getLittleZoneName())
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("getLittleZoneList err req:{},e:", GsonUtil.toJson(littlezoneIdList), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<CityZoneDTO> getCityZoneList(List<Integer> cityIdList) {
        log.info("getCityZoneList cityIdList:{}", GsonUtil.toJson(cityIdList));
        if (CollUtil.isEmpty(cityIdList)) {
            return Collections.emptyList();
        }
        try {
            List<List<Integer>> split = CollUtil.split(cityIdList, MAX_ZONE_PAGE_SIZE);
            List<CityZoneDTO> list = new ArrayList<>();
            for (List<Integer> splitCityIdList : split) {
                CityZoneListReq req = new CityZoneListReq();
                req.setCityZoneIdList(splitCityIdList);
                req.setPageIndex(1);
                req.setPageSize(splitCityIdList.size());
                log.info("getCityZoneList req:{}", GsonUtil.toJson(req));
                Result<PageResp<CityZoneDto>> result = zoneProvider.getCityZoneList(req);
                log.info("getCityZoneList resp:{}", GsonUtil.toJson(result));
                if (result == null || GeneralCodes.OK.getCode() != result.getCode()
                        || result.getData() == null) {
                    throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "获取城市列表信息失败");
                }
                PageResp<CityZoneDto> data = result.getData();
                if (CollUtil.isEmpty(data.getList())) {
                    continue;
                }
                List<CityZoneDTO> splitDataList = data.getList().stream()
                        .map(littleZoneDto -> CityZoneDTO.builder()
                                .cityZoneId(littleZoneDto.getCityZoneId())
                                .cityZoneName(littleZoneDto.getCityZoneName())
                                .cityZoneCode(littleZoneDto.getCityZoneCode())
                                .build())
                        .collect(Collectors.toList());
                list.addAll(splitDataList);
            }
            return list;
        } catch (Exception e) {
            log.error("getCityZoneList err req:{},e:", GsonUtil.toJson(cityIdList), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<ProvinceDTO> getProvinceCityHasStore(GetProvinceCityHasStoreGoIn goIn) {
        GetProvinceCityHasStoreRequest request = Convert.convert(GetProvinceCityHasStoreRequest.class, goIn);
        try {
            log.info("getProvinceCityHasStore req:{}", GsonUtil.toJson(request));
            Result<GetProvinceCityHasStoreResponse> result = provinceCityProvider.getProvinceCityHasStore(request);
            log.info("getProvinceCityHasStore resp:{}", GsonUtil.toJson(result));
            if (result == null || GeneralCodes.OK.getCode() != result.getCode() || result.getData() == null) {
                throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "获取含有汽车门店的省、市列表失败");
            }
            GetProvinceCityHasStoreResponse data = result.getData();
            List<GetProvinceCityHasStoreResponse.ProvinceDto> list = data.getList();
            List<ProvinceDTO> provinceDTOList = new ArrayList<>();
            for (GetProvinceCityHasStoreResponse.ProvinceDto provinceDto : list) {
                provinceDTOList.add(Convert.convert(ProvinceDTO.class, provinceDto));
            }
            return provinceDTOList;
        } catch (Exception e) {
            log.error("getProvinceCityHasStore err req:{},e:", GsonUtil.toJson(request), e);
            return Collections.emptyList();
        }
    }

    public List<ZoneDTO> getZoneList(ZoneListGoIn zoneListGoIn) {
        // 先发送一次请求获取总数据量
        ZoneListReq initialReq = Convert.convert(ZoneListReq.class, zoneListGoIn);
        initialReq.setPageIndex(1);
        initialReq.setPageSize(1); // 只请求一条数据，用于获取总数据量

        log.info("getZoneList initial req:{}", GsonUtil.toJson(initialReq));
        Result<ZoneListDto> initialResult = zoneProvider.getZoneList(initialReq);
        log.info("getZoneList initial resp:{}", GsonUtil.toJson(initialResult));

        if (initialResult == null || GeneralCodes.OK.getCode() != initialResult.getCode() ||
                initialResult.getData() == null) {
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, GET_ZONE_LIST_MESSAGE);
        }

        int totalCount = initialResult.getData().getTotal(); // 假设返回结果中有总数据量
        int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);

        List<Future<List<ZoneDTO>>> futures = new ArrayList<>();

        // 提交并行任务
        for (int pageIndex = 1; pageIndex <= totalPages; pageIndex++) {
            int finalPageIndex = pageIndex;
            futures.add(commonThreadPoolExecutor.submit(() -> {
                ZoneListReq req = Convert.convert(ZoneListReq.class, zoneListGoIn);
                req.setPageIndex(finalPageIndex);
                req.setPageSize(PAGE_SIZE);

                Result<ZoneListDto> result = zoneProvider.getZoneList(req);
                if (result == null || GeneralCodes.OK.getCode() != result.getCode() || result.getData() == null) {
                    throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, GET_ZONE_LIST_MESSAGE);
                }

                List<ZoneDto> list = result.getData().getList();
                return list.stream()
                        .map(zoneDto -> ZoneDTO.builder()
                                .zoneId(zoneDto.getZoneId())
                                .zoneCode(zoneDto.getZoneCode())
                                .zoneName(zoneDto.getZoneName())
                                .build())
                        .collect(Collectors.toList());
            }));
        }

        List<ZoneDTO> allZoneDTOs = new ArrayList<>();
        for (Future<List<ZoneDTO>> future : futures) {
            try {
                allZoneDTOs.addAll(future.get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Thread interrupted while getting zone list data", e);
                return Collections.emptyList();
            } catch (ExecutionException e) {
                log.error("Error executing task to get zone list data", e.getCause());
                return Collections.emptyList();
            }
        }
        return allZoneDTOs;
    }
}
