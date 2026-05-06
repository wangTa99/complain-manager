package com.wt.complaint.manage.infrastructure.gatewayimpl;

import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintTagGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintTagListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintTagGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintTagSoIn;
import com.wt.complaint.manage.infrastructure.converter.ComplaintTagConverter;
import com.wt.complaint.manage.infrastructure.mapper.ComplaintTagMapper;
import com.wt.complaint.manage.infrastructure.model.ComplaintTagDO;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangzheyang
 * @date 2024/12/25
 */
@Slf4j
@Service
public class ComplaintTagGatewayImpl implements ComplaintTagGateway {

    @Resource
    private ComplaintTagMapper complaintTagMapper;

    @Override
    public Boolean insertTag(ComplaintTagSoIn req) {
        if (StringUtils.isBlank(req.getComplaintNo()) || StringUtils.isBlank(req.getTagType())) {
            log.error("ComplaintTagGateway#insertTag error, req:{}", RetailJsonUtil.toJson(req));
            return false;
        }
        // 插入之前先查询一�?如果已经有了就不插入
        List<ComplaintTagDO> complaintTagDOS = complaintTagMapper.selectTag(req.getComplaintNo(), req.getTagType());
        if (CollectionUtils.isNotEmpty(complaintTagDOS)) {
            log.info("ComplaintTagGateway#insertTag tag already exists, complaintTagDOS:{}",
                    RetailJsonUtil.toJson(complaintTagDOS));
        } else {
            ComplaintTagDO tagDO = ComplaintTagConverter.INSTANCE.toDo(req);
            log.info("start complaintTagMapper.insertSelective, tagDO:{}", RetailJsonUtil.toJson(tagDO));
            complaintTagMapper.insertSelective(tagDO);
        }
        return true;
    }

    @Override
    public Boolean batchInsertTag(List<ComplaintTagSoIn> soInList) {
        List<ComplaintTagDO> tagDOList = ComplaintTagConverter.INSTANCE.toDo(soInList);
        int i = complaintTagMapper.batchInsert(tagDOList);
        return i>0;
    }

    @Override
    public List<ComplaintTagGoOut> getComplaintTagByComplaintNo(ComplaintTagListGoIn goIn) {
        if (CollectionUtils.isEmpty(goIn.getComplaintNoList())) {
            return new ArrayList<>();
        }
        List<ComplaintTagDO> complaintTagDOList = complaintTagMapper.selectByComplaintNoList(goIn.getComplaintNoList());
        return ComplaintTagConverter.INSTANCE.toGoOut(complaintTagDOList);
    }

    @Override
    public Boolean deleteTag(String complaintNo, String tagType) {
        if (StringUtils.isBlank(complaintNo) || StringUtils.isBlank(tagType)) {
            log.error("ComplaintTagGateway#deleteTag error, complaintNo:{}, tagType:{}", complaintNo, tagType);
            return false;
        }
        int updateCount = complaintTagMapper.deleteTag(complaintNo, tagType);
        log.info("ComplaintTagGateway#deleteTag success, complaintNo:{}, tagType:{}, updateCount:{}", complaintNo, tagType, updateCount);
        return updateCount > 0;
    }
}
