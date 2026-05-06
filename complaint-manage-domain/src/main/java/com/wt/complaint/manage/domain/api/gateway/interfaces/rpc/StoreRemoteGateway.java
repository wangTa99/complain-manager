package com.wt.complaint.manage.domain.api.gateway.interfaces.rpc;

import com.wt.complaint.manage.api.model.ProvinceDTO;
import com.wt.complaint.manage.api.model.resp.CityZoneDTO;
import com.wt.complaint.manage.api.model.resp.LittleZoneDTO;
import com.wt.complaint.manage.api.model.resp.ZoneDTO;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.GetProvinceCityHasStoreGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ZoneListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarStoreListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;

import java.util.List;
import java.util.Map;

public interface StoreRemoteGateway {
    /**
     * 查询汽车门店
     *
     * @param param
     * @return
     */
    List<StoreInfoGoOut> listCarStore(CarStoreListGoIn param);

    StoreInfoGoOut getStoreInfo(String orgId);

    List<StoreInfoGoOut> getStoreListInfo(List<String> orgIdList);

    /**
     * 获取汽车门店名称Map
     *
     * @param orgIdList 门店id列表
     * @return key:门店id value:门店名称
     */
    Map<String, String> getStoreNameMap(List<String> orgIdList);

    /**
     * 获取大区数据
     */
    List<ZoneDTO> getZoneList(List<Integer> zoneIdList);

    /**
     * 获取小区数据
     */
    List<LittleZoneDTO> getLittleZoneList(List<Integer> littlezoneIdList);

    /**
     * 获取城市数据
     * @param cityIdList 城市id列表
     * @return 城市列表
     */
    List<CityZoneDTO> getCityZoneList(List<Integer> cityIdList);

    /**
     * 获取含有汽车门店的省、市列表
     */
    List<ProvinceDTO> getProvinceCityHasStore(GetProvinceCityHasStoreGoIn getProvinceCityHasStoreGoIn);

    /**
     * 获取大区列表
     *
     * @param zoneListGoIn 大区列表入参
     * @return 大区列表
     */
    List<ZoneDTO> getZoneList(ZoneListGoIn zoneListGoIn);
}
