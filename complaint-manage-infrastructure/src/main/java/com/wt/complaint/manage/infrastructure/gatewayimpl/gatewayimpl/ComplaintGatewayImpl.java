package com.wt.complaint.manage.infrastructure.gatewayimpl;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.wt.complaint.manage.api.model.enums.*;
import com.wt.complaint.manage.domain.api.enums.ComplaintTypeEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintGateway;

import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.GetDynamicInfoResponseGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintListSearchGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintListFillDataSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintListSearchInfo;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintListSearchSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.TagSoOut;
import com.wt.complaint.manage.domain.utils.DateUtil;
import com.wt.complaint.manage.domain.utils.KeyCenterUtil;
import com.wt.complaint.manage.infrastructure.converter.OrderConverter;
import com.wt.complaint.manage.infrastructure.mapper.ComplaintOrderMapper;
import com.wt.complaint.manage.infrastructure.mapper.ComplaintTagMapper;
import com.wt.complaint.manage.infrastructure.model.ComplaintOrderDO;
import com.wt.complaint.manage.infrastructure.model.ComplaintTagDO;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;
import com.xiaomi.newretail.common.tools.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.wt.complaint.manage.api.model.enums.ComplaintStatusEnum.*;

@Slf4j
@Service
@SuppressWarnings("all")
public class ComplaintGatewayImpl implements ComplaintGateway {

    @Resource
    private ComplaintOrderMapper complaintOrderMapper;

    @Resource
    private ComplaintTagMapper complaintTagMapper;

    @Resource
    private CarRemoteGateway carRemoteGateway;

    @Resource
    private StoreRemoteGateway storeRemoteGateway;

    @Resource
    private EiamRemoteGateway eiamRemoteGateway;

    @Resource
    private MoneThreadPoolExecutor complaintOrderListExecutor;

    private static void fillUserMidName(List<ComplaintListSearchInfo> searchInfoList, Map<Long, String> nameByMid) {
        if (nameByMid != null) {
            searchInfoList.stream().forEach(complaintListSearchInfo -> {
                if (complaintListSearchInfo.getCustomerServiceMid() != null) {
                    complaintListSearchInfo.setCustomerServiceName(nameByMid.get(complaintListSearchInfo.getCustomerServiceMid()));
                }
                if (complaintListSearchInfo.getOperatorMid() != null) {
                    complaintListSearchInfo.setOperatorName(nameByMid.get(complaintListSearchInfo.getOperatorMid()));
                }
            });
        }
    }

    private static List<Long> getUserMids(List<ComplaintListSearchInfo> searchInfoList) {
        List<Long> operatorMidList = searchInfoList.stream().map(ComplaintListSearchInfo::getOperatorMid).collect(Collectors.toList());
        List<Long> customerMidList = searchInfoList.stream().map(ComplaintListSearchInfo::getCustomerServiceMid).collect(Collectors.toList());
        Set<Long> midSet = new HashSet<>(operatorMidList);
        midSet.addAll(customerMidList);
        List<Long> mergedList = new ArrayList<>(midSet);
        return mergedList;
    }

    private static void fillVinData(List<ComplaintListSearchInfo> searchInfoList, GetDynamicInfoResponseGoOut getDynamicInfoResponseGoOut) {
        if (getDynamicInfoResponseGoOut != null) {
            List<GetDynamicInfoResponseGoOut.DynamicInfoItemDto> getDynamicInfoResponseGoOutItems = getDynamicInfoResponseGoOut.getItems();
            Map<String, List<GetDynamicInfoResponseGoOut.DynamicInfoItemDto>> dynamicInfoMap = getDynamicInfoResponseGoOutItems.stream().collect(Collectors.groupingBy(GetDynamicInfoResponseGoOut.DynamicInfoItemDto::getVid));
            if (dynamicInfoMap != null) {
                searchInfoList.stream().forEach(complaintListSearchInfo -> {
                    List<GetDynamicInfoResponseGoOut.DynamicInfoItemDto> dynamicInfoItemDtoList = dynamicInfoMap.get(complaintListSearchInfo.getVid());
                    if (!ObjectUtils.isEmpty(dynamicInfoItemDtoList)) {
                        complaintListSearchInfo.setVin(dynamicInfoItemDtoList.get(0).getVin());
                    }
                });
            }
        }
    }

    private static void fillStoreInfoData(List<ComplaintListSearchInfo> searchInfoList, List<StoreInfoGoOut> storeInfoGoOutList) {
        if (storeInfoGoOutList != null) {
            Map<String, List<StoreInfoGoOut>> storeInfoMap = storeInfoGoOutList.stream().collect(Collectors.groupingBy(StoreInfoGoOut::getOrgId));
            if (storeInfoMap != null) {
                searchInfoList.stream().forEach(complaintListSearchInfo -> {
                    List<StoreInfoGoOut> storeInfoGoOuts = storeInfoMap.get(complaintListSearchInfo.getOrgId());
                    if (!ObjectUtils.isEmpty(storeInfoGoOuts)) {
                        complaintListSearchInfo.setOrgName(storeInfoGoOuts.get(0).getOrgName());
                    }
                });
            }
        }
    }

    private static void fillBasicInfo(List<ComplaintListSearchInfo> searchInfoList) {
        searchInfoList.forEach((ComplaintListSearchInfo info) -> {
            fillEnumDescriptions(info);
            fillDecryptedFields(info);
            fillMidFields(info);
            fillTimeFields(info);
            fillContactName(info);
        });
    }

    /**
     * 填充枚举描述信息
     */
    private static void fillEnumDescriptions(ComplaintListSearchInfo info) {
        if (info.getComplaintType() != null) {
            info.setComplaintTypeName(ComplaintTypeEnum.getDescByCode(info.getComplaintType()));
        }
        if (info.getStatus() != null) {
            info.setStatusName(ComplaintStatusEnum.getDescByCode(info.getStatus()));
        }
        if (info.getResponsibility() != null) {
            info.setResponsibilityName(ResponsibilityEnum.getDescByCode(info.getResponsibility()));
        }
        if (info.getRiskLevel() != null) {
            info.setRiskLevelName(RiskLevelEnum.getDescByCode(info.getRiskLevel()));
        }
        if (info.getCreateSource() != null) {
            info.setCreateSourceDesc(CreateSourceEnum.getDescByCode(info.getCreateSource()));
        }
    }

    /**
     * 填充解密字段
     */
    private static void fillDecryptedFields(ComplaintListSearchInfo info) {
        if (!StringUtils.isEmpty(info.getContactPhoneC())) {
            info.setContactPhone(KeyCenterUtil.decrypt(info.getContactPhoneC()));
        }
        if (!StringUtils.isEmpty(info.getContactNameC())) {
            info.setContactName(KeyCenterUtil.decrypt(info.getContactNameC()));
        }
    }

    /**
     * 填充Mid字段（将0值转为null�?
     */
    private static void fillMidFields(ComplaintListSearchInfo info) {
        if (info.getOperatorMid() != null && info.getOperatorMid() == 0L) {
            info.setOperatorMid(null);
        }
        if (info.getCustomerServiceMid() != null && info.getCustomerServiceMid() == 0L) {
            info.setCustomerServiceMid(null);
        }
    }

    /**
     * 填充时间字段（清除默认时间）
     */
    private static void fillTimeFields(ComplaintListSearchInfo info) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        if (!StringUtils.isEmpty(info.getFirstResponseTime())) {
            try {
                Date firstResponseTime = fmt.parse(info.getFirstResponseTime());
                if (DateUtil.isDefaultTime(firstResponseTime)) {
                    info.setFirstResponseTime("");
                }
            } catch (Exception e) {
                log.info("fillTimeFields firstResponseTime parse error, time:{}", info.getFirstResponseTime(), e);
            }
        }
        
        if (!StringUtils.isEmpty(info.getFinishTime())) {
            try {
                Date finishTime = fmt.parse(info.getFinishTime());
                if (DateUtil.isDefaultTime(finishTime)) {
                    info.setFinishTime("");
                }
            } catch (Exception e) {
                log.info("fillTimeFields finishTime parse error, time:{}", info.getFinishTime(), e);
            }
        }
    }

    /**
     * 填充联系人姓名（脱敏处理�?
     */
    private static void fillContactName(ComplaintListSearchInfo info) {
        if (StringUtils.isEmpty(info.getContactName()) || info.getContactName().length() == 0) {
            return;
        }
        
        String firstChar = info.getContactName().substring(0, 1);
        String genderSuffix;
        
        if (GenderEnum.MALE.getCode().equals(info.getContactGender())) {
            genderSuffix = GenderEnum.MALE.getExtend();
        } else if (GenderEnum.FEMALE.getCode().equals(info.getContactGender())) {
            genderSuffix = GenderEnum.FEMALE.getExtend();
        } else {
            genderSuffix = GenderEnum.UNKNOWN.getExtend();
        }
        
        info.setContactName(firstChar + genderSuffix);
    }

    @Override
    public ComplaintOrderGoOut selectByComplaintNo(String complaintNo) {
        ComplaintOrderDO complaintOrderDO = complaintOrderMapper.selectByComplaintNo(complaintNo);
        log.info("call ComplaintGateway#selectByComplaintNo success, complaintOrderDO:{}", RetailJsonUtil.toJson(complaintOrderDO));
        return OrderConverter.INSTANCE.toGoOut(complaintOrderDO);
    }

    @Override
    public List<ComplaintOrderGoOut> selectFirstResponseToTimeoutList() {
        // 未达成首响状态的几个订单
        List<Integer> statusList = Arrays.asList(PENDING_ORDER.getCode(), ORG_REASSIGN_PENDING.getCode(),
                FIRST_RESPONSE_PENDING.getCode());
        List<ComplaintOrderDO> complaintList = complaintOrderMapper.selectFirstResponseToTimeoutList(statusList);
        log.info("complaintOrderMapper.selectFirstResponseToTimeoutList success, complaintList={}", RetailJsonUtil.toJson(complaintList));
        return OrderConverter.INSTANCE.toGoOutList(complaintList);
    }

    @Override
    public List<ComplaintOrderGoOut> selectFinishToTimeoutList() {
        List<ComplaintOrderDO> complaintList = complaintOrderMapper.selectFinishToTimeoutList();
        log.info("complaintOrderMapper.selectFinishToTimeoutList success, complaintList={}", RetailJsonUtil.toJson(complaintList));
        return OrderConverter.INSTANCE.toGoOutList(complaintList);
    }

    @Override
    public ComplaintListSearchSoOut getComplaintOrderList(ComplaintListSearchGoIn goIn) {
        goIn.setStart((goIn.getPageNum() - 1) * goIn.getPageSize());

        Stopwatch stopwatch = Stopwatch.createStarted();
        List<ComplaintOrderDO> complaintOrderBOList = complaintOrderMapper.selectPageByParam(goIn);
        String source = goIn.getSource();
        log.info("getComplaintOrderList dbSelectPage cost, source={}, costMs={}",
                source, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        log.info("ComplaintOrderMapper#selectPageByParam, res:{}", RetailJsonUtil.toJson(complaintOrderBOList));
        if (ObjectUtils.isEmpty(complaintOrderBOList)) {
            return ComplaintListSearchSoOut.builder().dataList(Collections.emptyList()).total(0).build();
        }
        List<String> vidList = complaintOrderBOList.stream().map(ComplaintOrderDO::getVid).collect(Collectors.toList());
        List<String> orgIdList = complaintOrderBOList.stream().map(ComplaintOrderDO::getOrgId).collect(Collectors.toList());
        List<ComplaintListSearchInfo> searchInfoList = OrderConverter.INSTANCE.toSearchInfoList(complaintOrderBOList);
        List<CompletableFuture<Void>> completableFutureList = Lists.newArrayList();
        ComplaintListFillDataSoOut complaintListFillDataSoOut = new ComplaintListFillDataSoOut();
        stopwatch.reset().start();
        completableFutureList.add(CompletableFuture.runAsync(() -> complaintListFillDataSoOut.setGetDynamicInfoResponseGoOut(carRemoteGateway.getDynamicInfo(vidList)), complaintOrderListExecutor));
        completableFutureList.add(CompletableFuture.runAsync(() -> complaintListFillDataSoOut.setStoreInfoGoOutList(storeRemoteGateway.getStoreListInfo(orgIdList)), complaintOrderListExecutor));
        List<Long> userMids = getUserMids(searchInfoList);
        completableFutureList.add(CompletableFuture.runAsync(() -> complaintListFillDataSoOut.setMidNameMap(eiamRemoteGateway.getNameByMid(userMids)), complaintOrderListExecutor));
        CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[0])).join();
        log.info("getComplaintOrderList rpcFillData cost, source={}, costMs={}",
                source, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        fillUserMidName(searchInfoList, complaintListFillDataSoOut.getMidNameMap());
        fillVinData(searchInfoList, complaintListFillDataSoOut.getGetDynamicInfoResponseGoOut());
        fillStoreInfoData(searchInfoList, complaintListFillDataSoOut.getStoreInfoGoOutList());
        fillBasicInfo(searchInfoList);
        stopwatch.reset().start();
        Integer total = complaintOrderMapper.countByParam(goIn);
        log.info("getComplaintOrderList dbCount cost, source={}, costMs={}",
                source, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        ComplaintListSearchSoOut result = new ComplaintListSearchSoOut();
        result.setTotal(total);
        result.setDataList(searchInfoList);
        if (CollectionUtils.isNotEmpty(complaintOrderBOList)) {
            List<String> complaintNoList = complaintOrderBOList.stream().map(ComplaintOrderDO::getComplaintNo).collect(Collectors.toList());
            stopwatch.reset().start();
            List<ComplaintTagDO> tagList = complaintTagMapper.selectByComplaintNoList(complaintNoList);
            log.info("getComplaintOrderList dbTag cost, source={}, costMs={}",
                    source, stopwatch.elapsed(TimeUnit.MILLISECONDS));
            if (CollectionUtils.isNotEmpty(tagList)) {
                // tagType 按照complaintNo分组
                Map<String, List<ComplaintTagDO>> tagTypeMap = tagList.stream().collect(Collectors.groupingBy(ComplaintTagDO::getComplaintNo));
                for (ComplaintListSearchInfo searchInfo : searchInfoList) {
                    List<ComplaintTagDO> tagListByComplaintNo = tagTypeMap.get(searchInfo.getComplaintNo());
                    if (CollectionUtils.isNotEmpty(tagListByComplaintNo)) {
                        List<TagSoOut> tagSoOutList = tagListByComplaintNo.stream().map(tag -> {
                            TagSoOut tagSoOut = new TagSoOut();
                            tagSoOut.setCode(tag.getTagType());
                            tagSoOut.setDesc(TagTypeEnum.getDescByCode(tag.getTagType()));
                            return tagSoOut;
                        }).collect(Collectors.toList());
                        searchInfo.setTagList(tagSoOutList);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Integer getComplaintOrderCount(ComplaintListSearchGoIn goIn) {
        Integer res = complaintOrderMapper.countByParam(goIn);
        log.info("success call getComplaintOrderCount,req:{}, res:{}", RetailJsonUtil.toJson(goIn), res);
        return res;
    }

    @Override
    public List<ComplaintOrderGoOut> selectUnFinishedToTimeoutList() {
        List<ComplaintOrderDO> complaintList = complaintOrderMapper.selectUnFinishedToTimeoutList();
        log.info("complaintOrderMapper.selectUnFinishedToTimeoutList success, complaintList={}", RetailJsonUtil.toJson(complaintList));
        return OrderConverter.INSTANCE.toGoOutList(complaintList);
    }

    @Override
    public List<ComplaintOrderGoOut> selectPageByParam(ComplaintListSearchGoIn goIn) {
        List<ComplaintOrderDO> complaintList = complaintOrderMapper.selectPageByParam(goIn);
        return OrderConverter.INSTANCE.toGoOutList(complaintList);
    }

}
