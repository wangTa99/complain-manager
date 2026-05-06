package com.wt.complaint.manage.infrastructure.gatewayimpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintAuditGateway;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.ComplaintAuditListSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.ComplaintPreNextSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.ComplaintRelationClosingTagSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.SubmitForApprovalSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintAuditListSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintAuditSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintPreNextSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintRelationClosingTagSoOut;
import com.wt.complaint.manage.domain.utils.GsonUtil;
import com.wt.complaint.manage.infrastructure.converter.ComplaintAuditConverter;
import com.wt.complaint.manage.infrastructure.mapper.ComplaintAuditMapper;
import com.wt.complaint.manage.infrastructure.mapper.ComplaintRelationClosingTagMapper;
import com.wt.complaint.manage.infrastructure.model.ComplaintAuditDO;
import com.wt.complaint.manage.infrastructure.model.ComplaintRelationClosingTagDO;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhangzheyang
 * @date 2024/12/24
 */
@Slf4j
@Service
public class ComplaintAuditGatewayImpl implements ComplaintAuditGateway {

    @Resource
    private ComplaintAuditMapper complaintAuditMapper;

    @Resource
    private ComplaintRelationClosingTagMapper complaintRelationClosingTagMapper;

    @Override
    public ComplaintAuditListSoOut searchComplaintAuditList(ComplaintAuditListSoIn req) {
        log.info("ComplaintAuditGatewayImpl#searchComplaintAuditList start, req:{}", GsonUtil.toJson(req));
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<ComplaintAuditDO> auditDOList = complaintAuditMapper.selectPageByParam(req);
        PageInfo<ComplaintAuditDO> pageInfo = new PageInfo<>(auditDOList);
        ComplaintAuditListSoOut result = new ComplaintAuditListSoOut();
        result.setTotal(pageInfo.getTotal());
        result.setDataList(ComplaintAuditConverter.INSTANCE.toSoOutList(auditDOList));
        return result;
    }

    @Override
    public ComplaintPreNextSoOut selectPreAndAfter(ComplaintPreNextSoIn req) {
        List<Long> idList = complaintAuditMapper.selectPreAndAfter(req);
        ComplaintPreNextSoOut result = new ComplaintPreNextSoOut();
        if (CollectionUtils.isNotEmpty(idList)) {
            for (Long id : idList) {
                if (id > req.getId()) {
                    result.setPreAuditId(id);
                } else if (id < req.getId()) {
                    result.setNextAuditId(id);
                }
            }
        }
        return result;
    }

    @Override
    public ComplaintAuditSoOut selectById(Long id) {
        ComplaintAuditDO complaintAuditDO = complaintAuditMapper.selectById(id);
        log.info("call success,ComplaintAuditGatewayImpl#selectById, complaintAuditDO={}",
                RetailJsonUtil.toJson(complaintAuditDO));
        return ComplaintAuditConverter.INSTANCE.toSoOut(complaintAuditDO);
    }

    @Override
    public Boolean updateAuditById(SubmitForApprovalSoIn req) {
        ComplaintAuditDO auditDO = ComplaintAuditConverter.INSTANCE.toDO(req);
        complaintAuditMapper.updateById(auditDO);
        return true;
    }

    @Override
    public ComplaintAuditSoOut getRecentAuditByComplaintNo(String complaintNo, Integer auditType) {
        ComplaintAuditDO complaintAuditDO = complaintAuditMapper.selectByComplaintNo(complaintNo, auditType);
        return ComplaintAuditConverter.INSTANCE.toSoOut(complaintAuditDO);
    }

    @Override
    public List<ComplaintRelationClosingTagSoOut> getClosingTagListByComplaintNo(String complaintNo) {
        List<ComplaintRelationClosingTagDO> closingTagList = complaintRelationClosingTagMapper.selectByComplaintNo(complaintNo);
        return ComplaintAuditConverter.INSTANCE.toClosingTagList(closingTagList);
    }

    @Override
    public void deleteClosingTagByComplaintNo(String complaintNo) {
        log.info("ComplaintAuditGatewayImpl#deleteClosingTagByComplaintNo start, complaintNo:{}", complaintNo);
        complaintRelationClosingTagMapper.deleteByComplaintNo(complaintNo);
    }

    @Override
    public void insertClosingTag(ComplaintRelationClosingTagSoIn req) {
        ComplaintRelationClosingTagDO closingTagDO = ComplaintAuditConverter.INSTANCE.toTagDO(req);
        log.info("start ComplaintAuditGateway#insertClosingTag, closingTagDO:{}", RetailJsonUtil.toJson(closingTagDO));
        complaintRelationClosingTagMapper.insertSelective(closingTagDO);
    }

    @Override
    public Boolean batchInsertClosingTag(List<ComplaintRelationClosingTagSoIn> req) {
        List<ComplaintRelationClosingTagDO> tagDO = ComplaintAuditConverter.INSTANCE.toTagDO(req);
        int i = complaintRelationClosingTagMapper.batchInsertSelective(tagDO);
        return i == req.size();
    }
}
