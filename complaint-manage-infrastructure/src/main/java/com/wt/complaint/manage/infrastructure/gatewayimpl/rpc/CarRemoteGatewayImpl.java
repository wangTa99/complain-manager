package com.wt.complaint.manage.infrastructure.gatewayimpl.rpc;

import com.wt.complaint.manage.api.model.constont.DubboConstant;
import com.wt.complaint.manage.api.model.enums.LabelTypeEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarTagGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.GetDynamicInfoResponseGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.OwnerInfoItemGoOut;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.infrastructure.converter.CarRemoteConverter;
import com.wt.nr.common.utils.GsonUtil;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import com.xiaomi.nr.cis.api.dto.*;
import com.xiaomi.nr.cis.api.service.CisQueryService;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
@Service
public class CarRemoteGatewayImpl implements CarRemoteGateway {
    @Value("${car.label.tagDefId}")
    private Long carTagDefId;

    @DubboReference(interfaceClass = CisQueryService.class, group = "${dubbo.group.cis}", version = "1.0", retries = 0, timeout = DubboConstant.TIME_OUT)
    private CisQueryService cisQueryService;

    public static final String VIN_NOT_FOUND_VID_MESSAGE = "通过vin查不到vid信息";

    @Override
    public List<CarInfoGoOut> getCarSimpleInfo(List<String> vidList, List<String> vinList) {
        if (CollectionUtils.isEmpty(vidList) && CollectionUtils.isEmpty(vinList)) {
            return new ArrayList<>();
        }
        GetSimpleInfoRequest request = new GetSimpleInfoRequest();
        request.setVidList(vidList);
        request.setVinList(vinList);
        request.setWithConfigInfo(true);
        request.setTagDefIds(new HashSet(Collections.singleton(carTagDefId)));
        Result<GetSimpleInfoResponse> resData = null;
        try {
            resData = cisQueryService.getSimpleInfo(request);
            log.info("getCarSimpleInfo req:{} resp:{}", GsonUtil.toJson(request), GsonUtil.toJson(resData));
            if (resData == null || resData.getCode() != 0) {
                if (resData != null && VIN_NOT_FOUND_VID_MESSAGE.equals(resData.getMessage())) {
                    log.warn("getCarSimpleInfo 通过vin查不到vid信息, req:{},resp:{}", GsonUtil.toJson(request), GsonUtil.toJson(resData));
                }else {
                    log.error("getCarSimpleInfo result is not ok, req:{},resp:{}", GsonUtil.toJson(request), GsonUtil.toJson(resData));
                }
                return new ArrayList<>();
            }
        } catch (Exception e) {
            log.error("getCarSimpleInfo err req:{},e:", GsonUtil.toJson(request), e);
            return new ArrayList<>();
        }
        List<CarInfoGoOut> carInfos = new ArrayList<>();
        List<SimpleInfoItemDto> simpleInfoItems = Optional.ofNullable(resData.getData())
            .map(GetSimpleInfoResponse::getItems).orElse(new ArrayList<>());
        Map<String, List<CarItemDTO>> carItemMap = Optional.ofNullable(resData.getData())
            .map(GetSimpleInfoResponse::getCarItemMap).orElse(new HashMap<>());
        Map<String, List<CarTagDTO>> carTagMap = Optional.ofNullable(resData.getData())
            .map(GetSimpleInfoResponse::getCarTagMap).orElse(new HashMap<>());
        for (SimpleInfoItemDto simpleInfoItem : simpleInfoItems) {
            CarInfoGoOut carInfo = CarInfoGoOut.builder()
                .vid(simpleInfoItem.getVid())
                .vin(simpleInfoItem.getVin())
                .carType(simpleInfoItem.getCarEdition())
                .carImg(simpleInfoItem.getImageUrl())
                .carPurposeName(simpleInfoItem.getCarPurposeName())
                .build();
            // 填充配置信息
            List<CarItemDTO> carItems = carItemMap.getOrDefault(simpleInfoItem.getVid(), new ArrayList<>());
            Map<String, String> itemMap = new HashMap<>();
            for (CarItemDTO carItem : carItems) {
                itemMap.put(carItem.getIdentityEnum(),
                    Optional.ofNullable(carItem.getItemValue()).map(CarItemValueDTO::getName).orElse(""));
            }
            carInfo.setItemMap(itemMap);
            // 填充标签
            List<CarTagDTO> carTagList = carTagMap.getOrDefault(simpleInfoItem.getVid(), new ArrayList<>());
            CarTagGoOut labelBO = getCarTagGoOut(carTagList);
            carInfo.setCarTag(labelBO);
            carInfos.add(carInfo);
        }
        log.info("call CarRemoteGateway#getCarSimpleInfo success, req:{}, res:{}",
                RetailJsonUtil.toJson(request),
                RetailJsonUtil.toJson(carInfos));
        return carInfos;
    }

    private CarTagGoOut getCarTagGoOut(List<CarTagDTO> carTagList) {
        CarTagGoOut labelBO = new CarTagGoOut();
        labelBO.setTagType(LabelTypeEnum.CAR_LABEL.getCode());
        List<CarTagGoOut.TagInfoGoOut> tagList = new ArrayList<>();
        for (CarTagDTO carTag : carTagList) {
            CarTagGoOut.TagInfoGoOut tagInfo = new CarTagGoOut.TagInfoGoOut();
            tagInfo.setTagCode(carTag.getTagValue());
            tagInfo.setTagName(carTag.getTagValue());
            tagList.add(tagInfo);
        }
        labelBO.setTagList(tagList);
        return labelBO;
    }

    @Override
    public List<OwnerInfoItemGoOut> getOwnerInfo(String midStr, List<String> vidList) {
        if (CollectionUtils.isEmpty(vidList)) {
            log.error("getOwnerInfo paramsEmpty. vidList:{}", vidList);
            return null;
        }
        GetOwnerInfoRequest request = new GetOwnerInfoRequest();
        request.setVidList(vidList);
        if (midStr != null && !midStr.isEmpty()) {
            request.setOperator(midStr);
        } else {
            request.setOperator("complaint-manage");
        }
        Result<GetOwnerInfoResponse> resData;
        try {
            log.info("start call CisQueryService#getOwnerInfo, req:{}", RetailJsonUtil.toJson(request));
            resData = cisQueryService.getOwnerInfo(request);
        } catch (Exception e) {
            log.error("call cis getOwnerInfo exception,request:{} ", GsonUtil.toJson(request), e);
            return null;
        }
        if (resData == null || resData.getCode() != GeneralCodes.OK.getCode() || resData.getData() == null) {
            log.warn("getOwnerInfo err, request:{}, resp:{}", GsonUtil.toJson(request), GsonUtil.toJson(resData));
            return null;
        }
        log.info("call CisQueryService#getOwnerInfo success, req:{}, res:{}", RetailJsonUtil.toJson(request),
                RetailJsonUtil.toJson(resData.getData()));
        return CarRemoteConverter.INSTANCE.toGoOutList(resData.getData().getItems());
    }

    @Override
    public GetDynamicInfoResponseGoOut getDynamicInfo(List<String> vidList) {
        try {
            GetDynamicInfoRequest dynamicInfoRequest = new GetDynamicInfoRequest();
            dynamicInfoRequest.setVidList(vidList);
            log.info("getDynamicInfo req{}", RetailJsonUtil.toJson(dynamicInfoRequest));
            Result<GetDynamicInfoResponse> result = cisQueryService.getDynamicInfo(dynamicInfoRequest);
            log.info("getDynamicInfo resp{}", RetailJsonUtil.toJson(result));
            if (result.getCode() != GeneralCodes.OK.getCode() || result.getData() == null) {
                log.warn("查询整车中心动态信息失�?req:{},res:{}", RetailJsonUtil.toJson(dynamicInfoRequest), RetailJsonUtil.toJson(result));
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "查询整车中心动态信息失�?);
            }
            return CarRemoteConverter.INSTANCE.toGoOut(result.getData());
        } catch (Exception e) {
            log.error("查询整车中心动态信息失败：vidList:{}", RetailJsonUtil.toJson(vidList), e);
            return null;
        }
    }

    @Override
    public String getVinByVid(String vid) {
        List<CarInfoGoOut> carInfoGoOutList = getCarSimpleInfo(Collections.singletonList(vid), null);
        if (CollectionUtils.isEmpty(carInfoGoOutList)) {
            log.info("getVinByVid carInfoGoOutList is empty, vid:{}", vid);
            return "";
        }
        CarInfoGoOut carInfoGoOut = carInfoGoOutList.get(0);
        return carInfoGoOut.getVin();
    }

    @Override
    @Nullable
    public CarInfoGoOut getCarInfoByVid(String vid) {
        log.info("start getCarInfoByVid, vid:{}", vid);
        List<CarInfoGoOut> carInfoGoOutList = getCarSimpleInfo(Collections.singletonList(vid), null);
        if (CollectionUtils.isEmpty(carInfoGoOutList)) {
            log.warn("getCarInfoByVid carInfoGoOutList is empty, vid:{}", vid);
            return null;
        }
        return carInfoGoOutList.get(0);
    }

    @Override
    public String getVidByVin(String vin) {
        List<CarInfoGoOut> carInfoGoOutList = getCarSimpleInfo(null, Collections.singletonList(vin));
        if (CollectionUtils.isEmpty(carInfoGoOutList)) {
            log.info("getVidByVin carInfoGoOutList is empty, vin:{}", vin);
            return "";
        }
        CarInfoGoOut carInfoGoOut = carInfoGoOutList.get(0);
        return carInfoGoOut.getVid();
    }
}
