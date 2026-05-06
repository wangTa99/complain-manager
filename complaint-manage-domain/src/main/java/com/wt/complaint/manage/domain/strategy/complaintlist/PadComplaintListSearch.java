package com.wt.complaint.manage.domain.strategy.complaintlist;

import com.wt.complaint.manage.api.model.enums.ComplaintStatusEnum;
import com.wt.complaint.manage.api.model.enums.CreateSourceEnum;
import com.wt.complaint.manage.api.model.enums.OnlyViewEnum;
import com.wt.complaint.manage.api.model.enums.PadTabEnum;
import com.wt.complaint.manage.domain.api.enums.ComplaintTypeEnum;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintListSearchGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintListSearchSoOut;
import com.wt.complaint.manage.domain.constant.StrategyConstant;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.utils.DateUtil;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * 客诉单列表搜索策略类
 *
 * @author zhangzheyang
 * @date 2024/12/20
 */
@Service(StrategyConstant.PAD_COMPLAINT_LIST_SEARCH)
@Slf4j
public class PadComplaintListSearch extends AbstractComplaintListSearch {

    // 投诉单号前缀
    private static final String COMPLAINT_NO_PREFIX = "TS";

    @Override
    protected ComplaintListSearchGoIn preHandler(ComplaintListSearchGoIn goIn) {
        log.info("before PadComplaintListSearch#preHandler, goIn:{}", RetailJsonUtil.toJson(goIn));
        transformSearchKey(goIn);
        if (BooleanUtils.isTrue(goIn.getOnlyShowMyCompositeOrder())) {
            goIn.setOperatorMid(goIn.getMid());
        }
        PadTabEnum padTabEnum = PadTabEnum.getByCode(goIn.getTab());
        ComplaintListSearchGoIn newGoIn = genNewSearchGoIn(padTabEnum, goIn);
        log.info("PadComplaintListSearch#preHandler success, newGoIn:{}", RetailJsonUtil.toJson(newGoIn));
        return newGoIn;
    }

    @Override
    protected void postHandler(ComplaintListSearchGoIn goIn, ComplaintListSearchSoOut goOut) {


    }

    public void transformSearchKey(ComplaintListSearchGoIn goIn) {
        if (StringUtils.isNotBlank(goIn.getSearchKey())) {
            if (NumberUtils.isDigits(goIn.getSearchKey()) && goIn.getSearchKey().length() == 4) {
                // 手机号后四位搜索
                goIn.setContactPhoneSuffix(goIn.getSearchKey());
            } else if (NumberUtils.isDigits(goIn.getSearchKey()) && goIn.getSearchKey().length() == 6) {
                // vin�?�?
                goIn.setVinSuffix(goIn.getSearchKey());
            } else if (goIn.getSearchKey().startsWith(COMPLAINT_NO_PREFIX)) {
                // 投诉单号
                goIn.setComplaintNo(goIn.getSearchKey());
            } else {
                // 车牌�?
                goIn.setCarNo(goIn.getSearchKey());
            }
        }
    }

    public ComplaintListSearchGoIn genNewSearchGoIn(PadTabEnum padTabEnum, ComplaintListSearchGoIn goIn) {
        if (padTabEnum == null) {
            log.error("padTabEnum is null, goIn:{}", RetailJsonUtil.toJson(goIn));
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "tab is error");
        }

        // 深拷�?
        ComplaintListSearchGoIn newGoIn = RetailJsonUtil.fromJson(RetailJsonUtil.toJson(goIn),
                ComplaintListSearchGoIn.class);

        switch (padTabEnum) {
            case TOTAL:
                break;
            case PENDING_ORDER:
                newGoIn.setStatusList(Collections.singletonList(ComplaintStatusEnum.PENDING_ORDER.getCode()));
                break;
            case IN_PROGRESS:
                // 处理中：待首�?待申请结�?
                newGoIn.setStatusList(Arrays.asList(ComplaintStatusEnum.FIRST_RESPONSE_PENDING.getCode(),
                        ComplaintStatusEnum.APPLY_FINISH_PENDING.getCode()));
                break;
            case APPROACHING_TIMEOUT:
                // 首响剩余 4 个小时或者是结案剩余 12 个小时的
                // 首响超时：投诉单创建起始计时，风险等级为1级�?级的投诉单超24小时未完成首响；风险等级�?级�?级的投诉单超12小时未完成首响�?
                // 结案超时：投诉单创建起始计时，超72小时未结案完成，且不满足72小时结案免考核的投诉单�?
                List<ComplaintListSearchGoIn.ConditionGroup> conditionGroups = new ArrayList<>();

                ComplaintListSearchGoIn.ConditionGroup group1 = new ComplaintListSearchGoIn.ConditionGroup();
                group1.riskLevelList = Arrays.asList(1, 2);
                group1.createTimeStart = DateUtil.hoursAgo(20);
                group1.createTimeEnd = DateUtil.hoursAgo(24);
                group1.statusList = Arrays.asList(ComplaintStatusEnum.PENDING_ORDER.getCode(),
                        ComplaintStatusEnum.ORG_REASSIGN_PENDING.getCode(),
                        ComplaintStatusEnum.FIRST_RESPONSE_PENDING.getCode());

                ComplaintListSearchGoIn.ConditionGroup group2 = new ComplaintListSearchGoIn.ConditionGroup();
                group2.riskLevelList = Arrays.asList(3, 4);
                group2.createTimeStart = DateUtil.hoursAgo(8);
                group2.createTimeEnd = DateUtil.hoursAgo(12);
                group2.statusList = Arrays.asList(ComplaintStatusEnum.PENDING_ORDER.getCode(),
                        ComplaintStatusEnum.ORG_REASSIGN_PENDING.getCode(),
                        ComplaintStatusEnum.FIRST_RESPONSE_PENDING.getCode());

                ComplaintListSearchGoIn.ConditionGroup group3 = new ComplaintListSearchGoIn.ConditionGroup();
                group3.createTimeStart = DateUtil.hoursAgo(60);
                group3.createTimeEnd = DateUtil.hoursAgo(72);
                group3.statusList = Arrays.asList(ComplaintStatusEnum.APPLY_FINISH_PENDING.getCode(),
                        ComplaintStatusEnum.FINISH_EVALUATION_PENDING.getCode());

                conditionGroups.add(group1);
                conditionGroups.add(group2);
                conditionGroups.add(group3);

                newGoIn.setConditionGroups(conditionGroups);
                break;
            case FINISH_EVALUATION_PENDING:
                newGoIn.setStatusList(Collections.singletonList(ComplaintStatusEnum.FINISH_EVALUATION_PENDING.getCode()));
                break;
            case ONLY_VIEW:
                newGoIn.setOnlyView(OnlyViewEnum.YES.getCode());
                break;
            case PENDING_REVIEW:
                // 待复盘：1. 展示未复�?2. 仅来源于客服�?3. 要求是服务投诉类�?
                newGoIn.setReviewed(0);
                newGoIn.setCreateSource(CreateSourceEnum.ONLINE_CS.getCode());
                newGoIn.setComplaintType(ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
                break;
        }
        log.info("PadComplaintListSearch#genNewSearchGoIn, old goIn:{}, new goIn:{}",
                RetailJsonUtil.toJson(goIn), RetailJsonUtil.toJson(newGoIn));
        return newGoIn;
    }


}
