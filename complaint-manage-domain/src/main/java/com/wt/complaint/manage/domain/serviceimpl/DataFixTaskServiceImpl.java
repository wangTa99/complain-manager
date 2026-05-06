package com.wt.complaint.manage.domain.serviceimpl;

import cn.hutool.cache.CacheUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.Converter;
import cn.hutool.core.util.NumberUtil;
import com.wt.commons.utils.StringUtils;
import com.wt.complaint.manage.api.model.enums.TagTypeEnum;
import com.wt.complaint.manage.domain.api.enums.DeliverPositionEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintTagGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.DeliverComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintFollowProcessGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintTagListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintFollowProcessGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintTagGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.api.service.interfaces.DataFixTaskService;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintListSearchGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintTagSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.DeliverComplaintListGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.PageGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.RecordInfoSoOut;
import com.wt.complaint.manage.domain.bo.DeliverComplaintBO;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.utils.ParseComplaintContentUtil;
import com.wt.nr.common.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;

@Slf4j
@Service
public class DataFixTaskServiceImpl implements DataFixTaskService {
    @Resource
    private DeliverComplaintGateway deliverComplaintGateway;
    @Resource
    private ComplaintFollowProcessRepositoryGateway followProcessRepositoryGateway;
    @Resource
    private StoreRemoteGateway storeRemoteGateway;
    @Resource
    private ComplaintGateway complaintGateway;
    @Resource
    private ComplaintTagGateway complaintTagGateway;

    @Override
    public void fillComplaintSceneTask(String req) {
        // 查询投诉场景为空的数�?
        List<DeliverComplaintBO> list = deliverComplaintGateway.selectEmptyComplaintScene();
        // 解析投诉场景字段
        parseComplaintContent(list);
        // 更新入库
        deliverComplaintGateway.updateComplaintSceneByDrNo(list);
    }

    /**
     * 解析complaintContent中的投诉场景字段并填充到DeliverComplaintBO�?
     *
     * @param list BOList
     */
    private void parseComplaintContent(List<DeliverComplaintBO> list) {
        for (DeliverComplaintBO deliverComplaintBO : list) {
            String complaintScene = ParseComplaintContentUtil.parseComplaintScene(deliverComplaintBO.getComplaintContent());
            deliverComplaintBO.setComplaintScene(complaintScene);
            String complaintSceneCode =
                    ParseComplaintContentUtil.parseComplaintSceneCode(deliverComplaintBO.getComplaintContent());
            if (StringUtils.isEmpty(complaintSceneCode)) {
                deliverComplaintBO.setLastComplaintSceneId(0);
            } else {
                deliverComplaintBO.setLastComplaintSceneId(Integer.parseInt(complaintSceneCode));
            }
        }
    }

    @Override
    public void fixOperatorPosition(String req) {
        // 1.查询所有交付客诉跟进记�?
        List<ComplaintFollowProcessGoOut> processList = followProcessRepositoryGateway.selectNeedFixDeliverProcessList();
        // 2.查找岗位名为交付邀约主管的的记录改为区域邀约经�? 岗位id�?6的改�?66
        List<ComplaintFollowProcessGoIn> updateProcessList = new ArrayList<>();

        for (ComplaintFollowProcessGoOut goOut : processList) {
            RecordInfoSoOut recordInfoSoOut = GsonUtil.fromJson(goOut.getProcessContent(), RecordInfoSoOut.class);
            boolean needFix = false;

            // �?交付邀约主�?改为'区域邀约经�?
            needFix |= this.fixRegionalInviteManager(recordInfoSoOut);
            // 将岗位id�?6的改�?66
            needFix |= this.fixPositionAId(recordInfoSoOut);

            if (needFix) {
                goOut.setProcessContent(GsonUtil.toJson(recordInfoSoOut));
                ComplaintFollowProcessGoIn goIn = Convert.convert(ComplaintFollowProcessGoIn.class, goOut);
                updateProcessList.add(goIn);
            }
        }
        // 3.更新数据�?
        followProcessRepositoryGateway.batchUpdateProcessContentById(updateProcessList);
    }

    /**
     * 交付邀约专员id刷数
     */
    private boolean fixPositionAId(RecordInfoSoOut recordInfoSoOut) {
        boolean needFix = false;
        // 旧交付邀约专员id
        final String oldPositionAId = "86";
        // 新交付邀约专员id
        final String newPositionAId = String.valueOf(DeliverPositionEnum.POSITION_A.getPositionId());

        // 流程操作人岗�?
        if (oldPositionAId.equals(recordInfoSoOut.getOperatePositionId())) {
            recordInfoSoOut.setOperatePositionId(newPositionAId);
            needFix = true;
        }
        // 改派岗位
        if (oldPositionAId.equals(String.valueOf(recordInfoSoOut.getReassignOperatorPositionId()))) {
            recordInfoSoOut.setReassignOperatorPositionId(Integer.valueOf(newPositionAId));
            needFix = true;
        }
        // 交付客诉单跟进人岗位
        if (oldPositionAId.equals(String.valueOf(recordInfoSoOut.getOperatorPositionId()))) {
            recordInfoSoOut.setOperatorPositionId(Integer.valueOf(newPositionAId));
            needFix = true;
        }
        return needFix;
    }

    /**
     * 区域邀约经理刷�?
     */
    private boolean fixRegionalInviteManager(RecordInfoSoOut recordInfoSoOut) {
        boolean needFix = false;

        final String positionId = String.valueOf(DeliverPositionEnum.REGIONAL_INVITE_MANAGER.getPositionId());
        // 新的岗位�?
        String newPositionName = DeliverPositionEnum.REGIONAL_INVITE_MANAGER.getSystemPositionName();
        // 流程操作人岗�?
        if (positionId.equals(recordInfoSoOut.getOperatePositionId())) {
            recordInfoSoOut.setOperatePositionName(newPositionName);
            needFix = true;
        }
        // 改派岗位
        if (positionId.equals(String.valueOf(recordInfoSoOut.getReassignOperatorPositionId()))) {
            recordInfoSoOut.setReassignOperatorPositionName(newPositionName);
            needFix = true;
        }
        // 交付客诉单跟进人岗位
        if (positionId.equals(String.valueOf(recordInfoSoOut.getOperatorPositionId()))) {
            recordInfoSoOut.setOperatorPositionName(newPositionName);
            needFix = true;
        }
        return needFix;
    }

    /**
     * 交付大区小区城市全量刷数
     *
     * @param req 请求参数
     */
    public void updateZoneData(String req){
        // 查询所有交付客诉单数据
        List<DeliverComplaintBO> allDataList = getDeliverComplaintBOS();

        // 查询门店信息
        Map<String, StoreInfoGoOut> storeInfoMap = getStringStoreInfoGoOutMap(allDataList);

        // 对比并筛选出需要修改的数据�?
        List<DeliverComplaintBO> oldList = new ArrayList<>();
        List<DeliverComplaintBO> needUpdateList = new ArrayList<>();

        for (DeliverComplaintBO oldBO : allDataList) {
            StoreInfoGoOut storeInfoGoOut = storeInfoMap.get(oldBO.getOrgId());
            if (storeInfoGoOut != null) {
                if (storeInfoGoOut.getZoneId() == null || storeInfoGoOut.getLittleZoneId() == null ||
                        storeInfoGoOut.getCityZoneId() == null || storeInfoGoOut.getCityId() == null ||
                        !NumberUtil.isInteger(storeInfoGoOut.getCityId())
                ) {
                    log.warn("DataFixTaskServiceImpl#updateZoneData 门店信息不完�?storeInfoGoOut:{}", storeInfoGoOut);
                    continue;
                }

                DeliverComplaintBO newBO = new DeliverComplaintBO();
                BeanUtil.copyProperties(oldBO, newBO);
                newBO.setZoneId(storeInfoGoOut.getZoneId());
                newBO.setLittleZoneId(storeInfoGoOut.getLittleZoneId());
                newBO.setCityZoneId(storeInfoGoOut.getCityZoneId());
                newBO.setCityId(Integer.parseInt(storeInfoGoOut.getCityId()));

                if (!newBO.equals(oldBO)) {
                    oldList.add(oldBO);
                    needUpdateList.add(newBO);
                }
            }
        }
        log.info("DataFixTaskServiceImpl#updateZoneData oldList:{}", GsonUtil.toJson(oldList));
        log.info("DataFixTaskServiceImpl#updateZoneData needUpdateList:{}", GsonUtil.toJson(needUpdateList));
        if (StringUtils.isNotEmpty(req)) {
            needUpdateList = needUpdateList.stream()
                    .filter(t -> req.equals(t.getDrNo())).collect(Collectors.toList());
            log.info("DataFixTaskServiceImpl#updateZoneData filterNeedUpdateList:{}", GsonUtil.toJson(needUpdateList));
        }
        // 3.更新数据�?
        deliverComplaintGateway.updateCityZoneIdByDrNo(needUpdateList);
    }

    @NotNull
    private Map<String, StoreInfoGoOut> getStringStoreInfoGoOutMap(List<DeliverComplaintBO> allDataList) {
        Map<String, StoreInfoGoOut> storeInfoMap = new HashMap<>();

        List<String> orgIdList = allDataList.stream().map(DeliverComplaintBO::getOrgId)
                .distinct().collect(Collectors.toList());
        log.info("DataFixTaskServiceImpl#getStringStoreInfoGoOutMap orgIdList:{}", orgIdList);
        // 分批查询门店信息
        List<List<String>> split = CollUtil.split(orgIdList, 200);

        for (List<String> splitOrgIdList : split) {
            List<StoreInfoGoOut> storeInfoList = storeRemoteGateway.getStoreListInfo(splitOrgIdList);
            // 转换为map
            for (StoreInfoGoOut storeInfoGoOut : storeInfoList) {
                storeInfoMap.put(storeInfoGoOut.getOrgId(), storeInfoGoOut);
            }
        }
        log.info("DataFixTaskServiceImpl#getStringStoreInfoGoOutMap storeInfoMap:{}", storeInfoMap);
        return storeInfoMap;
    }

    @NotNull
    private List<DeliverComplaintBO> getDeliverComplaintBOS() {
        // 限制本方法的适用范围:数据量不大于两万
        Long total = deliverComplaintGateway.selectCountByCondition(new DeliverComplaintListGoIn());
        if (total > 20000) {
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "数据量大�?�?不支持刷�?);
        }

        // 分页查询所有数�?
        int pageSize = 500; // 每页大小
        List<DeliverComplaintBO> allDataList = new ArrayList<>();

        for (int offset = 0; offset < total; offset += pageSize) {
            PageGoIn pageGoIn = PageGoIn.builder()
                    .offset(offset)
                    .pageSize(pageSize)
                    .build();
            List<DeliverComplaintBO> pageDataList = deliverComplaintGateway.selectByPageGoIn(pageGoIn);
            allDataList.addAll(pageDataList);
        }
        log.info("DataFixTaskServiceImpl#updateZoneData allDataList size:{}", allDataList.size());
        return allDataList;
    }

    /**
     * 将有责投诉单打上投诉率免考核标签
     * 指定complaintNo投诉单号（可选，为空则处理所有有责投诉单�?
     * @param complaintNo 请求参数
     */
    @Override
    public int convertResponsibilityToTag(String complaintNo) {
        log.info("DataFixTaskServiceImpl#convertResponsibilityToTag start, complaintNo={}", complaintNo);
        
        int pageSize = 100;
        int maxIterations = 10000;
        int totalProcessed = 0;
        int totalTagged = 0;
        boolean shouldContinue = true;
        
        for (int pageNum = 1; pageNum <= maxIterations && shouldContinue; pageNum++) {
            List<ComplaintOrderGoOut> orderList = queryResponsibleComplaintOrders(complaintNo, pageNum, pageSize);
            
            if (CollUtil.isEmpty(orderList)) {
                log.info("DataFixTaskServiceImpl#convertResponsibilityToTag 没有更多数据, pageNum={}", pageNum);
                shouldContinue = false;
            } else {
                List<String> complaintNoList = orderList.stream()
                        .map(ComplaintOrderGoOut::getComplaintNo)
                        .collect(Collectors.toList());
                log.info("DataFixTaskServiceImpl#convertResponsibilityToTag 查询到有责投诉单, pageNum={}, size={}, complaintNoList={}", 
                        pageNum, complaintNoList.size(), complaintNoList);
                
                int taggedCount = processComplaintTagging(complaintNoList, pageNum);
                totalTagged += taggedCount;
                totalProcessed += complaintNoList.size();
                
                // 判断是否需要继续循�?
                if (StringUtils.isNotEmpty(complaintNo)) {
                    // 如果指定了complaintNo，处理完就退�?
                    log.info("DataFixTaskServiceImpl#convertResponsibilityToTag 指定投诉单处理完�? complaintNo={}", complaintNo);
                    shouldContinue = false;
                } else if (orderList.size() < pageSize) {
                    // 如果当前页数据量小于pageSize，说明已经是最后一�?
                    log.info("DataFixTaskServiceImpl#convertResponsibilityToTag 已处理完所有数�? pageNum={}", pageNum);
                    shouldContinue = false;
                }
            }
        }
        
        log.info("DataFixTaskServiceImpl#convertResponsibilityToTag end, totalProcessed={}, totalTagged={}", 
                totalProcessed, totalTagged);

        return totalTagged;
    }

    /**
     * 查询有责的投诉单
     */
    private List<ComplaintOrderGoOut> queryResponsibleComplaintOrders(String complaintNo, int pageNum, int pageSize) {
        ComplaintListSearchGoIn searchGoIn = ComplaintListSearchGoIn.builder()
                .responsibility(1)
                .start((pageNum - 1) * pageSize)
                .pageSize(pageSize)
                .build();
        
        if (StringUtils.isNotEmpty(complaintNo)) {
            searchGoIn.setComplaintNo(complaintNo);
        }
        
        return complaintGateway.selectPageByParam(searchGoIn);
    }

    /**
     * 处理投诉单门店是否有责打标逻辑
     * @return 成功打标的数�?
     */
    private int processComplaintTagging(List<String> complaintNoList, int pageNum) {
        // 查询已存在的标签
        ComplaintTagListGoIn tagListGoIn = ComplaintTagListGoIn.builder()
                .complaintNoList(complaintNoList)
                .build();
        List<ComplaintTagGoOut> existingTags = complaintTagGateway.getComplaintTagByComplaintNo(tagListGoIn);
        
        // 构建已存在标签的投诉单号集合
        final Set<String> taggedComplaintNoSet;
        if (CollUtil.isNotEmpty(existingTags)) {
            taggedComplaintNoSet = existingTags.stream()
                    .filter(tag -> TagTypeEnum.STORE_RESPONSIBLE.getCode().equals(tag.getTagType()))
                    .map(ComplaintTagGoOut::getComplaintNo)
                    .collect(Collectors.toSet());
        } else {
            taggedComplaintNoSet = new HashSet<>();
        }
        
        log.info("DataFixTaskServiceImpl#processComplaintTagging 已存在标签的投诉�? taggedComplaintNoSet={}", 
                taggedComplaintNoSet);
        
        // 筛选需要打标的投诉�?
        List<String> needTagComplaintNoList = complaintNoList.stream()
                .filter(no -> !taggedComplaintNoSet.contains(no))
                .collect(Collectors.toList());
        
        if (CollUtil.isEmpty(needTagComplaintNoList)) {
            log.info("DataFixTaskServiceImpl#processComplaintTagging 当前页没有需要打标的投诉�? pageNum={}", pageNum);
            return 0;
        }
        
        log.info("DataFixTaskServiceImpl#processComplaintTagging 需要打标的投诉�? needTagComplaintNoList={}", 
                needTagComplaintNoList);
        
        // 批量插入标签
        List<ComplaintTagSoIn> tagSoInList = needTagComplaintNoList.stream()
                .map(no -> ComplaintTagSoIn.builder()
                        .complaintNo(no)
                        .tagType(TagTypeEnum.STORE_RESPONSIBLE.getCode())
                        .isDeleted(0)
                        .build())
                .collect(Collectors.toList());
        
        int taggedCount = 0;
        try {
            Boolean insertResult = complaintTagGateway.batchInsertTag(tagSoInList);
            if (Boolean.TRUE.equals(insertResult)) {
                taggedCount = needTagComplaintNoList.size();
                log.info("DataFixTaskServiceImpl#processComplaintTagging 批量打标成功, size={}", taggedCount);
            } else {
                log.warn("DataFixTaskServiceImpl#processComplaintTagging 批量打标失败, needTagComplaintNoList={}",
                        needTagComplaintNoList);
            }
        } catch (Exception e) {
            log.error("DataFixTaskServiceImpl#processComplaintTagging 批量打标异常, needTagComplaintNoList={}", 
                    needTagComplaintNoList, e);
        }
        
        return taggedCount;
    }

}
