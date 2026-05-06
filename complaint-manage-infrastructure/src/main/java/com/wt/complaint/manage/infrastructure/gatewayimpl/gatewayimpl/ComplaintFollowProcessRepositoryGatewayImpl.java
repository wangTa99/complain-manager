package com.wt.complaint.manage.infrastructure.gatewayimpl;

import cn.hutool.core.collection.CollUtil;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintFollowProcessGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintProcessApplyFinishListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintProcessLastGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintProcessListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintFollowProcessGoOut;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.infrastructure.converter.ProcessConverter;
import com.wt.complaint.manage.infrastructure.mapper.ComplaintFollowProcessMapper;
import com.wt.complaint.manage.infrastructure.model.ComplaintFollowProcessDO;
import com.wt.nr.common.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ComplaintFollowProcessRepositoryGatewayImpl implements ComplaintFollowProcessRepositoryGateway {
    @Resource
    private ComplaintFollowProcessMapper followProcessMapper;

    @Override
    public Boolean saveComplaintFollowProcess(ComplaintFollowProcessGoIn complaintFollowProcess) {
        log.info("saveComplaintFollowProcess goIn:{}", GsonUtil.toJson(complaintFollowProcess));
        int i = followProcessMapper.insertSelective(ProcessConverter.INSTANCE.toDO(complaintFollowProcess));
        return i > 0;
    }

    @Override
    public List<ComplaintFollowProcessGoOut> getProcessListByNo(String complaintNo) {
        List<ComplaintFollowProcessDO> complaintFollowProcessDOS = followProcessMapper.selectByComplaintNo(complaintNo);
        return ProcessConverter.INSTANCE.toGoOut(complaintFollowProcessDOS);
    }

    @Override
    public List<ComplaintFollowProcessGoOut> getProcessListByProcessInstanceId(String processInstanceId) {
        List<ComplaintFollowProcessDO> complaintFollowProcessDOS = followProcessMapper.selectByProcessInstanceId(processInstanceId);
        return ProcessConverter.INSTANCE.toGoOut(complaintFollowProcessDOS);
    }

    @Override
    public List<ComplaintFollowProcessGoOut> getProcessList(ComplaintProcessListGoIn listGoIn) {
        try {
            listGoIn.checkGoIn();
            log.info("getProcessList goIn:{}", GsonUtil.toJson(listGoIn));
            List<ComplaintFollowProcessDO> complaintFollowProcessDOS = followProcessMapper.selectListByParam(ProcessConverter.INSTANCE.toParam(listGoIn));
            log.info("getProcessList data:{}", GsonUtil.toJson(complaintFollowProcessDOS));
            return ProcessConverter.INSTANCE.toGoOut(complaintFollowProcessDOS);
        } catch (Exception e) {
            log.warn("查询投诉跟进列表异常", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<ComplaintFollowProcessGoOut> getLastApplyFinishRecordByParam(ComplaintProcessApplyFinishListGoIn complaintProcessApplyFinishListGoIn) {
        List<ComplaintFollowProcessDO> complaintFollowProcessDOS = followProcessMapper.selectLastApplyFinishRecordByParam(ProcessConverter.INSTANCE.toParam(complaintProcessApplyFinishListGoIn));
        return ProcessConverter.INSTANCE.toGoOut(complaintFollowProcessDOS);
    }

    @Override
    public List<ComplaintFollowProcessGoOut> getLastSubmitReviewRecordByParam(ComplaintProcessApplyFinishListGoIn goIn) {
        List<ComplaintFollowProcessDO> list = followProcessMapper.selectLastSubmitReviewRecordByParam(ProcessConverter.INSTANCE.toParam(goIn));
        return ProcessConverter.INSTANCE.toGoOut(list);
    }

    @Override
    public Map<String, ComplaintFollowProcessGoOut> getLastProcess(ComplaintProcessLastGoIn goIn) {
        if (CollUtil.isEmpty(goIn.getComplaintNoList())) {
            log.warn("ComplaintProcessListGoIn.checkGoIn: complaintNo is empty, complaintNo={}", goIn.getComplaintNoList());
            return Collections.emptyMap();
        }
        List<ComplaintFollowProcessDO> complaintFollowProcessDOS = followProcessMapper.selectLastProcessByParam(goIn);
        List<ComplaintFollowProcessGoOut> goOut = ProcessConverter.INSTANCE.toGoOut(complaintFollowProcessDOS);
        return goOut.stream().collect(Collectors.toMap(ComplaintFollowProcessGoOut::getComplaintNo,
                Function.identity(), (o1, o2) -> o1));
    }

    @Override
    public List<ComplaintFollowProcessGoOut> selectNeedFixDeliverProcessList() {
        List<ComplaintFollowProcessDO> complaintFollowProcessDOS = followProcessMapper.selectNeedFixDeliverProcessList();
        return ProcessConverter.INSTANCE.toGoOut(complaintFollowProcessDOS);
    }

    @Override
    public void batchUpdateProcessContentById(List<ComplaintFollowProcessGoIn> updateProcessList) {
        log.info("batchUpdateProcessContentById, updateProcessList: {}", updateProcessList);
        if (CollUtil.isEmpty(updateProcessList)) {
            log.info("batchUpdateProcessContentById list is empty");
            return;
        }

        // 过滤掉id为空的记�?
        List<ComplaintFollowProcessGoIn> validList = updateProcessList.stream()
                .filter(item -> item != null && item.getId() != null)
                .collect(Collectors.toList());

        if (validList.isEmpty()) {
            log.warn("batchUpdateProcessContentById no valid records to update");
            return;
        }

        // 转换为DO对象
        List<ComplaintFollowProcessDO> updateList = ProcessConverter.INSTANCE.toDO(validList);

        try {
            int updateCount = 0;
            // 分批处理，每�?0条记�?
            List<List<ComplaintFollowProcessDO>> batches = CollUtil.split(updateList, 50);
            for (List<ComplaintFollowProcessDO> batch : batches) {
                updateCount += followProcessMapper.batchUpdateProcessContentById(batch);
            }
            log.info("batchUpdateProcessContentById success, updateCount: {}", updateCount);
        } catch (Exception e) {
            log.error("batchUpdateProcessContentById failed, list: {}", GsonUtil.toJson(validList), e);
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "批量更新跟进记录失败");
        }
    }
}
