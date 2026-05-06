package com.wt.complaint.manage.domain.api.service.converter;

import com.wt.complaint.manage.domain.api.gateway.parameter.AttachmentGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.RecordInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.UserComplaintOrderMainGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.AttachmentSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.FieldValueSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.TemplateFieldSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.TemplateStructSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.AttachmentSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.DetailFieldSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.DetailFieldSoOut.Value;
import com.wt.complaint.manage.domain.api.service.parameter.out.RecordInfoSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.SimpleComplaintDetailSoOut.ComplaintInfoGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.TemplateStructSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UcOrderBatchInfoSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintDetailSoOut;
import com.wt.complaint.manage.domain.model.UserComplaintOrderInfo;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-31T21:19:57+0800",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 1.8.0_462 (Amazon.com Inc.)"
)
public class OrderViewConverterImpl implements OrderViewConverter {

    @Override
    public ComplaintInfoGoOut toComplaintInfoGoOut(ComplaintOrderGoOut source) {
        if ( source == null ) {
            return null;
        }

        ComplaintInfoGoOut complaintInfoGoOut = new ComplaintInfoGoOut();

        if ( source.getCreateTime() != null ) {
            complaintInfoGoOut.setCreateTime( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).format( source.getCreateTime() ) );
        }
        complaintInfoGoOut.setOperatorId( source.getOperatorMid() );
        complaintInfoGoOut.setStNo( source.getSuperTicketNo() );
        complaintInfoGoOut.setComplaintNo( source.getComplaintNo() );
        complaintInfoGoOut.setSoNo( source.getSoNo() );
        complaintInfoGoOut.setCustomerServiceMid( source.getCustomerServiceMid() );
        complaintInfoGoOut.setOrgId( source.getOrgId() );
        complaintInfoGoOut.setProblemCategory( source.getProblemCategory() );
        complaintInfoGoOut.setProblemDesc( source.getProblemDesc() );
        complaintInfoGoOut.setUserDemand( source.getUserDemand() );
        complaintInfoGoOut.setRiskLevel( source.getRiskLevel() );

        return complaintInfoGoOut;
    }

    @Override
    public List<TemplateStructSoOut> toTemplateStructSoOut(List<TemplateStructSoIn> source) {
        if ( source == null ) {
            return null;
        }

        List<TemplateStructSoOut> list = new ArrayList<TemplateStructSoOut>( source.size() );
        for ( TemplateStructSoIn templateStructSoIn : source ) {
            list.add( toTemplateStructSoOut( templateStructSoIn ) );
        }

        return list;
    }

    @Override
    public TemplateStructSoOut toTemplateStructSoOut(TemplateStructSoIn source) {
        if ( source == null ) {
            return null;
        }

        TemplateStructSoOut templateStructSoOut = new TemplateStructSoOut();

        templateStructSoOut.setGroupName( source.getGroupName() );
        templateStructSoOut.setGroupOrder( source.getGroupOrder() );
        templateStructSoOut.setFields( templateFieldSoInListToDetailFieldSoOutList( source.getFields() ) );

        return templateStructSoOut;
    }

    @Override
    public DetailFieldSoOut toDetailFieldSoOut(TemplateFieldSoIn source) {
        if ( source == null ) {
            return null;
        }

        DetailFieldSoOut detailFieldSoOut = new DetailFieldSoOut();

        detailFieldSoOut.setAttachments( attachmentSoInListToAttachmentSoOutList( source.getAttachmentList() ) );
        detailFieldSoOut.setId( source.getId() );
        detailFieldSoOut.setOrder( source.getOrder() );
        detailFieldSoOut.setRequired( source.getRequired() );
        detailFieldSoOut.setFieldType( source.getFieldType() );
        detailFieldSoOut.setFieldName( source.getFieldName() );
        detailFieldSoOut.setValue( fieldValueSoInListToValueList( source.getValue() ) );

        return detailFieldSoOut;
    }

    @Override
    public AttachmentSoOut toAttachmentSoOut(AttachmentSoIn source) {
        if ( source == null ) {
            return null;
        }

        AttachmentSoOut attachmentSoOut = new AttachmentSoOut();

        attachmentSoOut.setId( source.getId() );
        attachmentSoOut.setFileName( source.getFileName() );
        attachmentSoOut.setUrl( source.getUrl() );
        attachmentSoOut.setType( source.getType() );

        return attachmentSoOut;
    }

    @Override
    public RecordInfoSoOut toRecordInfoSoOut(RecordInfoGoIn source) {
        if ( source == null ) {
            return null;
        }

        RecordInfoSoOut recordInfoSoOut = new RecordInfoSoOut();

        recordInfoSoOut.setApplyId( source.getApplyId() );
        recordInfoSoOut.setApplyType( source.getApplyType() );
        recordInfoSoOut.setApplyTime( source.getApplyTime() );
        recordInfoSoOut.setApplyMid( source.getApplyMid() );
        recordInfoSoOut.setApplyName( source.getApplyName() );
        recordInfoSoOut.setDeliveryTime( source.getDeliveryTime() );
        recordInfoSoOut.setMileage( source.getMileage() );
        recordInfoSoOut.setApplyReason( source.getApplyReason() );
        recordInfoSoOut.setApplyOrgId( source.getApplyOrgId() );
        recordInfoSoOut.setApplyOrgName( source.getApplyOrgName() );
        recordInfoSoOut.setReassignOrgId( source.getReassignOrgId() );
        recordInfoSoOut.setReassignOrgName( source.getReassignOrgName() );
        recordInfoSoOut.setApplyOrgDisplayName( source.getApplyOrgDisplayName() );
        recordInfoSoOut.setReassignOrgDisplayName( source.getReassignOrgDisplayName() );
        recordInfoSoOut.setAuditTime( source.getAuditTime() );
        recordInfoSoOut.setAuditMid( source.getAuditMid() );
        recordInfoSoOut.setAuditName( source.getAuditName() );
        recordInfoSoOut.setAuditResult( source.getAuditResult() );
        recordInfoSoOut.setSolutionDesc( source.getSolutionDesc() );
        List<String> list = source.getFinishTabList();
        if ( list != null ) {
            recordInfoSoOut.setFinishTabList( new ArrayList<String>( list ) );
        }
        recordInfoSoOut.setAuditReason( source.getAuditReason() );
        recordInfoSoOut.setPickUpTime( source.getPickUpTime() );
        recordInfoSoOut.setOrderReceiverMid( source.getOrderReceiverMid() );
        recordInfoSoOut.setOrderReceiverName( source.getOrderReceiverName() );
        recordInfoSoOut.setDispatchTime( source.getDispatchTime() );
        recordInfoSoOut.setDispatcherMid( source.getDispatcherMid() );
        recordInfoSoOut.setDispatcherName( source.getDispatcherName() );
        recordInfoSoOut.setFollowUpTime( source.getFollowUpTime() );
        recordInfoSoOut.setFollowUpMid( source.getFollowUpMid() );
        recordInfoSoOut.setFollowUpName( source.getFollowUpName() );
        recordInfoSoOut.setFollowUpContent( source.getFollowUpContent() );
        recordInfoSoOut.setRemindOrderTime( source.getRemindOrderTime() );
        recordInfoSoOut.setOrderReminderMid( source.getOrderReminderMid() );
        recordInfoSoOut.setOrderReminderName( source.getOrderReminderName() );
        recordInfoSoOut.setOrderRemindInfo( source.getOrderRemindInfo() );
        recordInfoSoOut.setStNo( source.getStNo() );
        recordInfoSoOut.setMrNo( source.getMrNo() );
        recordInfoSoOut.setMrStatus( source.getMrStatus() );
        recordInfoSoOut.setMrStatusName( source.getMrStatusName() );
        recordInfoSoOut.setCreateTime( source.getCreateTime() );
        recordInfoSoOut.setCreateMid( source.getCreateMid() );
        recordInfoSoOut.setCreateName( source.getCreateName() );
        recordInfoSoOut.setContactMid( source.getContactMid() );
        recordInfoSoOut.setContactName( source.getContactName() );
        recordInfoSoOut.setContactPhoneNumber( source.getContactPhoneNumber() );
        recordInfoSoOut.setAppointTime( source.getAppointTime() );
        recordInfoSoOut.setEstimatedDeliveryTime( source.getEstimatedDeliveryTime() );
        recordInfoSoOut.setServiceReceiverMid( source.getServiceReceiverMid() );
        recordInfoSoOut.setServiceReceiverName( source.getServiceReceiverName() );
        recordInfoSoOut.setQuestionDescription( source.getQuestionDescription() );
        recordInfoSoOut.setDistributionId( source.getDistributionId() );
        recordInfoSoOut.setDistributionTime( source.getDistributionTime() );
        recordInfoSoOut.setPointsBatch( source.getPointsBatch() );
        recordInfoSoOut.setDistributionMid( source.getDistributionMid() );
        recordInfoSoOut.setDistributionName( source.getDistributionName() );
        recordInfoSoOut.setPointsQuantity( source.getPointsQuantity() );
        recordInfoSoOut.setPointsAmount( source.getPointsAmount() );
        if ( source.getPointsAuditStatus() != null ) {
            recordInfoSoOut.setPointsAuditStatus( String.valueOf( source.getPointsAuditStatus() ) );
        }
        recordInfoSoOut.setPointsAuditStatusName( source.getPointsAuditStatusName() );
        if ( source.getPointsDistributionStatus() != null ) {
            recordInfoSoOut.setPointsDistributionStatus( String.valueOf( source.getPointsDistributionStatus() ) );
        }
        recordInfoSoOut.setPointsDistributionStatusName( source.getPointsDistributionStatusName() );
        recordInfoSoOut.setAttachments( attachmentGoInListToAttachmentSoOutList( source.getAttachments() ) );
        recordInfoSoOut.setOperateMid( source.getOperateMid() );
        recordInfoSoOut.setOperateName( source.getOperateName() );
        recordInfoSoOut.setOperateDesc( source.getOperateDesc() );
        recordInfoSoOut.setOperateTime( source.getOperateTime() );
        recordInfoSoOut.setJudgeResult( source.getJudgeResult() );
        recordInfoSoOut.setJudgeResultDesc( source.getJudgeResultDesc() );
        recordInfoSoOut.setOperatePositionId( source.getOperatePositionId() );
        recordInfoSoOut.setOperatePositionName( source.getOperatePositionName() );
        recordInfoSoOut.setFollowDesc( source.getFollowDesc() );
        recordInfoSoOut.setReassignOperatorPositionId( source.getReassignOperatorPositionId() );
        recordInfoSoOut.setReassignOperatorPositionName( source.getReassignOperatorPositionName() );
        recordInfoSoOut.setReassignOperatorMid( source.getReassignOperatorMid() );
        recordInfoSoOut.setReassignOperatorName( source.getReassignOperatorName() );
        recordInfoSoOut.setReassignDesc( source.getReassignDesc() );
        recordInfoSoOut.setReconciled( source.getReconciled() );
        recordInfoSoOut.setRevisited( source.getRevisited() );
        recordInfoSoOut.setFinishDesc( source.getFinishDesc() );
        recordInfoSoOut.setResponsible( source.getResponsible() );
        recordInfoSoOut.setResponsibleJudgeDesc( source.getResponsibleJudgeDesc() );
        recordInfoSoOut.setProblemCategory( source.getProblemCategory() );
        recordInfoSoOut.setRiskLevel( source.getRiskLevel() );
        recordInfoSoOut.setOrgId( source.getOrgId() );
        recordInfoSoOut.setOrgName( source.getOrgName() );
        recordInfoSoOut.setOperatorPositionId( source.getOperatorPositionId() );
        recordInfoSoOut.setOperatorPositionName( source.getOperatorPositionName() );
        recordInfoSoOut.setUpgradeTime( source.getUpgradeTime() );
        recordInfoSoOut.setUpgraderName( source.getUpgraderName() );
        recordInfoSoOut.setUpgradeReason( source.getUpgradeReason() );
        recordInfoSoOut.setOriginalTypeDesc( source.getOriginalTypeDesc() );
        recordInfoSoOut.setTargetTypeDesc( source.getTargetTypeDesc() );
        recordInfoSoOut.setUpdateTime( source.getUpdateTime() );
        recordInfoSoOut.setUpdaterMid( source.getUpdaterMid() );
        recordInfoSoOut.setUpdaterName( source.getUpdaterName() );
        recordInfoSoOut.setComplaintTypeChange( source.getComplaintTypeChange() );
        recordInfoSoOut.setRiskLevelChange( source.getRiskLevelChange() );
        recordInfoSoOut.setMediaInvolvedChange( source.getMediaInvolvedChange() );
        recordInfoSoOut.setMediaLinkChange( source.getMediaLinkChange() );
        recordInfoSoOut.setUserAgreementDesc( source.getUserAgreementDesc() );
        recordInfoSoOut.setVehicleRepairedDesc( source.getVehicleRepairedDesc() );
        recordInfoSoOut.setMediaInfoDesc( source.getMediaInfoDesc() );
        recordInfoSoOut.setReviewMaterialUrl( source.getReviewMaterialUrl() );
        recordInfoSoOut.setCurrentNode( source.getCurrentNode() );
        recordInfoSoOut.setHandleType( source.getHandleType() );
        recordInfoSoOut.setBeforeUpdate( source.getBeforeUpdate() );
        recordInfoSoOut.setAfterUpdate( source.getAfterUpdate() );

        return recordInfoSoOut;
    }

    @Override
    public UcOrderBatchInfoSoOut toUcOrderBatchInfoSoOut(UserComplaintOrderMainGoOut source) {
        if ( source == null ) {
            return null;
        }

        UcOrderBatchInfoSoOut ucOrderBatchInfoSoOut = new UcOrderBatchInfoSoOut();

        ucOrderBatchInfoSoOut.setUcOrderViewInfoList( userComplaintOrderInfoListToUserComplaintDetailSoOutList( source.getUserComplaintOrderInfoList() ) );

        return ucOrderBatchInfoSoOut;
    }

    @Override
    public UserComplaintDetailSoOut toUserComplaintOrderInfo(UserComplaintOrderInfo source) {
        if ( source == null ) {
            return null;
        }

        UserComplaintDetailSoOut userComplaintDetailSoOut = new UserComplaintDetailSoOut();

        userComplaintDetailSoOut.setHandleMid( source.getOperatorMid() );
        if ( source.getCreateTime() != null ) {
            userComplaintDetailSoOut.setCreateTime( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).format( source.getCreateTime() ) );
        }
        if ( source.getFinishTime() != null ) {
            userComplaintDetailSoOut.setFinishTime( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).format( source.getFinishTime() ) );
        }
        userComplaintDetailSoOut.setServiceScene( toServiceScene( source.getComplaintContent() ) );
        userComplaintDetailSoOut.setUserComplaintDetailInfos( toUserComplaintDetailInfo( source.getComplaintContent() ) );
        userComplaintDetailSoOut.setUcNo( source.getUcNo() );
        userComplaintDetailSoOut.setSoNo( source.getSoNo() );
        userComplaintDetailSoOut.setSuperTicketNo( source.getSuperTicketNo() );
        userComplaintDetailSoOut.setOrderStatus( source.getOrderStatus() );
        userComplaintDetailSoOut.setOrgId( source.getOrgId() );
        userComplaintDetailSoOut.setCreateMid( source.getCreateMid() );

        return userComplaintDetailSoOut;
    }

    protected List<DetailFieldSoOut> templateFieldSoInListToDetailFieldSoOutList(List<TemplateFieldSoIn> list) {
        if ( list == null ) {
            return null;
        }

        List<DetailFieldSoOut> list1 = new ArrayList<DetailFieldSoOut>( list.size() );
        for ( TemplateFieldSoIn templateFieldSoIn : list ) {
            list1.add( toDetailFieldSoOut( templateFieldSoIn ) );
        }

        return list1;
    }

    protected List<AttachmentSoOut> attachmentSoInListToAttachmentSoOutList(List<AttachmentSoIn> list) {
        if ( list == null ) {
            return null;
        }

        List<AttachmentSoOut> list1 = new ArrayList<AttachmentSoOut>( list.size() );
        for ( AttachmentSoIn attachmentSoIn : list ) {
            list1.add( toAttachmentSoOut( attachmentSoIn ) );
        }

        return list1;
    }

    protected Value fieldValueSoInToValue(FieldValueSoIn fieldValueSoIn) {
        if ( fieldValueSoIn == null ) {
            return null;
        }

        Value value = new Value();

        value.setCode( fieldValueSoIn.getCode() );
        value.setDesc( fieldValueSoIn.getDesc() );
        value.setPathId( fieldValueSoIn.getPathId() );
        value.setPathName( fieldValueSoIn.getPathName() );

        return value;
    }

    protected List<Value> fieldValueSoInListToValueList(List<FieldValueSoIn> list) {
        if ( list == null ) {
            return null;
        }

        List<Value> list1 = new ArrayList<Value>( list.size() );
        for ( FieldValueSoIn fieldValueSoIn : list ) {
            list1.add( fieldValueSoInToValue( fieldValueSoIn ) );
        }

        return list1;
    }

    protected AttachmentSoOut attachmentGoInToAttachmentSoOut(AttachmentGoIn attachmentGoIn) {
        if ( attachmentGoIn == null ) {
            return null;
        }

        AttachmentSoOut attachmentSoOut = new AttachmentSoOut();

        attachmentSoOut.setId( attachmentGoIn.getId() );
        attachmentSoOut.setFileName( attachmentGoIn.getFileName() );
        attachmentSoOut.setUrl( attachmentGoIn.getUrl() );
        attachmentSoOut.setType( attachmentGoIn.getType() );

        return attachmentSoOut;
    }

    protected List<AttachmentSoOut> attachmentGoInListToAttachmentSoOutList(List<AttachmentGoIn> list) {
        if ( list == null ) {
            return null;
        }

        List<AttachmentSoOut> list1 = new ArrayList<AttachmentSoOut>( list.size() );
        for ( AttachmentGoIn attachmentGoIn : list ) {
            list1.add( attachmentGoInToAttachmentSoOut( attachmentGoIn ) );
        }

        return list1;
    }

    protected List<UserComplaintDetailSoOut> userComplaintOrderInfoListToUserComplaintDetailSoOutList(List<UserComplaintOrderInfo> list) {
        if ( list == null ) {
            return null;
        }

        List<UserComplaintDetailSoOut> list1 = new ArrayList<UserComplaintDetailSoOut>( list.size() );
        for ( UserComplaintOrderInfo userComplaintOrderInfo : list ) {
            list1.add( toUserComplaintOrderInfo( userComplaintOrderInfo ) );
        }

        return list1;
    }
}
