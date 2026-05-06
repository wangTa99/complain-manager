package com.wt.complaint.manage.domain.serviceimpl;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.wt.commons.utils.JacksonUtil;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.DeliverComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.RetailComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.RedisRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.service.converter.OrderOperationConverter;
import com.wt.complaint.manage.domain.api.service.interfaces.CronPushTaskService;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.RetailComplaintListGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.deliver.DeliverComplaintListGoOut;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.model.ComplaintBasicInfo;
import com.wt.complaint.manage.domain.strategy.deliver.message.NewComplaintMessageStrategy;
import com.wt.complaint.manage.domain.strategy.message.MessageInformedEventFactory;
import com.wt.complaint.manage.domain.strategy.message.MessageInformedStrategy;
import com.wt.complaint.manage.domain.strategy.deliver.message.NewMessageInformedEventFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangzheyang
 * @date 2025/1/5
 */
@Component
@Slf4j
public class CronPushTaskServiceImpl implements CronPushTaskService {

    /**
     * 首响即将超时 key
     */
    private static final String FIRST_RESPONSE_TO_TIMEOUT_KEY_FORMAT = "first_response_%s";
    /**
     * 结案超时key. 客诉单号
     */
    private static final String FINISH_TO_TIMEOUT_KEY_FORMAT = "finish_%s";

    /**
     * �?5天未结案超时key. 客诉单号
     */
    private static final String UN_FINISHED_TO_TIMEOUT_KEY_FORMAT = "unfinish_%s";

    /**
     * key 过期时间�?80�?
     */
    private static final long KEY_EXPIRE_TIME = 180;
    @Resource
    private ApplicationEventPublisher eventPublisher;
    @Resource
    private ComplaintGateway complaintGateway;

    @Resource
    private DeliverComplaintGateway deliverComplaintGateway;

    @Resource
    private RetailComplaintGateway retailComplaintGateway;

    @Resource
    private RedisRemoteGateway redisRemoteGateway;
    @Resource
    private MessageInformedEventFactory messageInformedEventFactory;

    @Resource
    private NewMessageInformedEventFactory newMessageInformedEventFactory;

    @NacosValue(value = "${repeatSendComplaintNoList}", autoRefreshed = true)
    private String repeatSendComplaintNoList;

    @Override
    public void cronPush() {
        // 服务客诉
        serviceTimeOutCron();
        // 交付客诉
        deliverTimeOutCron();
        // 零售客诉
        retailTimeOutCron();
    }

    /**
     * 服务客诉定时推�?
     */
    private void serviceTimeOutCron() {
        firstToTimeOutCron();
        finishToTimeoutCron();

        // 新增�?5天未结案扫描
        unFinishedToTimeoutCron();
    }

    /**
     * 交付客诉定时推�?
     */
    private void deliverTimeOutCron() {
        deliverFirstToTimeOutCron();
        deliverFinishToTimeoutCron();
    }

    /**
     * 零售客诉定时推�?
     */
    private void retailTimeOutCron() {
        retailFirstToTimeOutCron();
        retailFinishToTimeoutCron();
    }

    /**
     * 服务客诉即将首响超时的客诉单查询,并发送相关消�?
     */
    public void firstToTimeOutCron() {
        // 查询即将首响超时的投诉单
        List<ComplaintOrderGoOut> firstTimeoutComplaintList = complaintGateway.selectFirstResponseToTimeoutList();
        if (CollectionUtils.isEmpty(firstTimeoutComplaintList)) {
            return;
        }
        List<String> needRepeatSendComplaintNoList = new ArrayList<>();
        if (StringUtils.isNotBlank(repeatSendComplaintNoList)) {
            printRepeatSendComplaintNoListLog("firstToTimeOutCron");
            needRepeatSendComplaintNoList = JacksonUtil.parseArray(repeatSendComplaintNoList, String.class);
        }
        // 过滤出未发送过消息的投诉单
        List<ComplaintOrderGoOut> needSendFirstResponse = new ArrayList<>();
        for (ComplaintOrderGoOut complaintOrderGoOut : firstTimeoutComplaintList) {
            if (needRepeatSendComplaintNoList.contains(complaintOrderGoOut.getComplaintNo())) {
                log.info("repeat send complaintNo: {}", complaintOrderGoOut.getComplaintNo());
                needSendFirstResponse.add(complaintOrderGoOut);
                continue;
            }
            String key = String.format(FIRST_RESPONSE_TO_TIMEOUT_KEY_FORMAT, complaintOrderGoOut.getComplaintNo());
            String value = redisRemoteGateway.get(key);
            log.info("FIRST_RESPONSE_TO_TIMEOUT_KEY_FORMAT key: {} , value:{}", key, value);
            if (StringUtils.isBlank(value) || !"1".equals(value)) {
                needSendFirstResponse.add(complaintOrderGoOut);
                // 设置key，防止重复推�?
                redisRemoteGateway.set(key, "1", KEY_EXPIRE_TIME, TimeUnit.DAYS);
            } else {
                log.info("first key:{} has already send message, skip it", key);
            }
        }
        MessageInformedStrategy messageStrategy =
                messageInformedEventFactory.getStrategy(PushConstant.FIRST_RESPONSE_TO_TIMEOUT);
        for (ComplaintOrderGoOut complaintOrderGoOut : needSendFirstResponse) {
            MessageInformedEvent messageInformedEvent = messageStrategy.createMessageInformedEvent(complaintOrderGoOut,
                    new HashMap<>());
            log.info("firstToTimeOutCron start publishEvent, messageInformedEvent={}",
                    JacksonUtil.toStr(messageInformedEvent));
            eventPublisher.publishEvent(messageInformedEvent);
        }
    }

    /**
     * 服务客诉即将超时的客诉单查询,并发送相关消�?
     */
    public void finishToTimeoutCron() {
        List<ComplaintOrderGoOut> finishTimeoutComplaintList = complaintGateway.selectFinishToTimeoutList();
        if (CollectionUtils.isEmpty(finishTimeoutComplaintList)) {
            return;
        }
        List<String> needRepeatComplaintNoList = new ArrayList<>();
        if (StringUtils.isNotBlank(repeatSendComplaintNoList)) {
            printRepeatSendComplaintNoListLog("finishToTimeoutCron");
            needRepeatComplaintNoList = JacksonUtil.parseArray(repeatSendComplaintNoList, String.class);
        }
        List<ComplaintOrderGoOut> needSendFinishResponse = new ArrayList<>();
        for (ComplaintOrderGoOut complaintOrderGoOut : finishTimeoutComplaintList) {
            if (needRepeatComplaintNoList.contains(complaintOrderGoOut.getComplaintNo())) {
                log.info("finishToTimeoutCron repeat send complaintNo: {}", complaintOrderGoOut.getComplaintNo());
                needSendFinishResponse.add(complaintOrderGoOut);
                continue;
            }
            String finishKey = String.format(FINISH_TO_TIMEOUT_KEY_FORMAT, complaintOrderGoOut.getComplaintNo());
            String finishValue = redisRemoteGateway.get(finishKey);
            log.info("FINISH_TO_TIMEOUT_KEY_FORMAT key:{}, value:{}", finishKey, finishValue);
            if (StringUtils.isBlank(finishValue) || !"1".equals(finishValue)) {
                needSendFinishResponse.add(complaintOrderGoOut);
                // 设置key，防止重复推�?
                redisRemoteGateway.set(finishKey, "1", KEY_EXPIRE_TIME, TimeUnit.DAYS);
            } else {
                log.info("finish key:{} has already send message, skip it", finishKey);
            }
        }
        MessageInformedStrategy finishMessageStrategy =
                messageInformedEventFactory.getStrategy(PushConstant.FINISH_TO_TIMEOUT);
        for (ComplaintOrderGoOut complaintOrderGoOut : needSendFinishResponse) {
            MessageInformedEvent messageInformedEvent = finishMessageStrategy.createMessageInformedEvent(
                    complaintOrderGoOut,
                    new HashMap<>());
            log.info("finishToTimeoutCron start publishEvent, messageInformedEvent={}",
                    JacksonUtil.toStr(messageInformedEvent));
            eventPublisher.publishEvent(messageInformedEvent);
        }
    }

    /**
     * 交付客诉即将首响超时的客诉单查询,并发送相关消�?
     */
    public void deliverFirstToTimeOutCron() {
        // 查询即将首响超时的交付投诉单
        List<DeliverComplaintListGoOut> firstTimeoutComplaintList = deliverComplaintGateway.selectFirstResponseToTimeoutList();
        if (CollectionUtils.isEmpty(firstTimeoutComplaintList)) {
            return;
        }
        List<String> needRepeatSendComplaintNoList = new ArrayList<>();
        if (StringUtils.isNotBlank(repeatSendComplaintNoList)) {
            printRepeatSendComplaintNoListLog("deliverFirstToTimeOutCron");
            needRepeatSendComplaintNoList = JacksonUtil.parseArray(repeatSendComplaintNoList, String.class);
        }
        // 过滤出未发送过消息的投诉单
        List<DeliverComplaintListGoOut> needSendFirstResponse = new ArrayList<>();
        for (DeliverComplaintListGoOut complaintOrderGoOut : firstTimeoutComplaintList) {
            if (needRepeatSendComplaintNoList.contains(complaintOrderGoOut.getDrNo())) {
                log.info("deliverFirstToTimeOutCron repeat send drNo: {}", complaintOrderGoOut.getDrNo());
                needSendFirstResponse.add(complaintOrderGoOut);
                continue;
            }
            String key = String.format(FIRST_RESPONSE_TO_TIMEOUT_KEY_FORMAT, complaintOrderGoOut.getDrNo());
            String value = redisRemoteGateway.get(key);
            log.info("deliverFirstToTimeOutCron FIRST_RESPONSE_TO_TIMEOUT_KEY_FORMAT key: {} , value:{}", key, value);
            if (StringUtils.isBlank(value) || !"1".equals(value)) {
                needSendFirstResponse.add(complaintOrderGoOut);
                // 设置key，防止重复推�?
                redisRemoteGateway.set(key, "1", KEY_EXPIRE_TIME, TimeUnit.DAYS);
            } else {
                log.info("deliver first key:{} has already send message, skip it", key);
            }
        }

        NewComplaintMessageStrategy newMessageStrategy = newMessageInformedEventFactory.getStrategy(PushConstant.DELIVER_FIRST_RESPONSE_TO_TIMEOUT);
        for (DeliverComplaintListGoOut complaintOrderGoOut : needSendFirstResponse) {
            ComplaintBasicInfo complaintBasicInfo = OrderOperationConverter.INSTANCE.toBasicInfo(complaintOrderGoOut);
            Map<String, String> extraParam = new HashMap<>();
            MessageInformedEvent newMessageInformedEvent = newMessageStrategy.createMessageInformedEvent(complaintBasicInfo, extraParam);
            log.info("deliverFirstToTimeOutCron start publishEvent, newMessageInformedEvent={}",
                    JacksonUtil.toStr(newMessageInformedEvent));
            eventPublisher.publishEvent(newMessageInformedEvent);
        }
    }

    /**
     * 交付客诉结案即将超时的客诉单查询,并发送相关消�?
     */
    public void deliverFinishToTimeoutCron() {
        List<DeliverComplaintListGoOut> finishTimeoutComplaintList = deliverComplaintGateway.selectFinishToTimeoutList();
        if (CollectionUtils.isEmpty(finishTimeoutComplaintList)) {
            return;
        }
        List<String> needRepeatComplaintNoList = new ArrayList<>();
        if (StringUtils.isNotBlank(repeatSendComplaintNoList)) {
            printRepeatSendComplaintNoListLog("deliverFinishToTimeoutCron");
            needRepeatComplaintNoList = JacksonUtil.parseArray(repeatSendComplaintNoList, String.class);
        }
        List<DeliverComplaintListGoOut> needSendFinishResponse = new ArrayList<>();
        for (DeliverComplaintListGoOut complaintOrderGoOut : finishTimeoutComplaintList) {
            if (needRepeatComplaintNoList.contains(complaintOrderGoOut.getDrNo())) {
                log.info("deliverFinishToTimeoutCron repeat send drNo: {}", complaintOrderGoOut.getDrNo());
                needSendFinishResponse.add(complaintOrderGoOut);
                continue;
            }
            String finishKey = String.format(FINISH_TO_TIMEOUT_KEY_FORMAT, complaintOrderGoOut.getDrNo());
            String finishValue = redisRemoteGateway.get(finishKey);
            log.info("deliverFinishToTimeoutCron FINISH_TO_TIMEOUT_KEY_FORMAT key:{}, value:{}", finishKey, finishValue);
            if (StringUtils.isBlank(finishValue) || !"1".equals(finishValue)) {
                needSendFinishResponse.add(complaintOrderGoOut);
                // 设置key，防止重复推�?
                redisRemoteGateway.set(finishKey, "1", KEY_EXPIRE_TIME, TimeUnit.DAYS);
            } else {
                log.info("deliver finish key:{} has already send message, skip it", finishKey);
            }
        }

        NewComplaintMessageStrategy newMessageStrategy = newMessageInformedEventFactory.getStrategy(PushConstant.DELIVER_FINISH_TO_TIMEOUT);
        for (DeliverComplaintListGoOut complaintOrderGoOut : needSendFinishResponse) {
            ComplaintBasicInfo complaintBasicInfo = OrderOperationConverter.INSTANCE.toBasicInfo(complaintOrderGoOut);
            Map<String, String> extraParam = new HashMap<>();
            MessageInformedEvent newMessageInformedEvent = newMessageStrategy.createMessageInformedEvent(complaintBasicInfo, extraParam);

            log.info("deliverFinishToTimeoutCron start publishEvent, newMessageInformedEvent={}",
                    JacksonUtil.toStr(newMessageInformedEvent));
            eventPublisher.publishEvent(newMessageInformedEvent);
        }
    }

    /**
     * 零售客诉即将首响超时的客诉单查询,并发送相关消�?
     */
    public void retailFirstToTimeOutCron() {
        // 查询即将首响超时的交付投诉单
        List<RetailComplaintListGoOut> firstTimeoutComplaintList = retailComplaintGateway.selectFirstResponseToTimeoutList();
        if (CollectionUtils.isEmpty(firstTimeoutComplaintList)) {
            return;
        }
        List<String> needRepeatSendComplaintNoList = new ArrayList<>();
        if (StringUtils.isNotBlank(repeatSendComplaintNoList)) {
            printRepeatSendComplaintNoListLog("retailFirstToTimeOutCron");
            needRepeatSendComplaintNoList = JacksonUtil.parseArray(repeatSendComplaintNoList, String.class);
        }
        // 过滤出未发送过消息的投诉单
        List<RetailComplaintListGoOut> needSendFirstResponse = new ArrayList<>();
        for (RetailComplaintListGoOut complaintOrderGoOut : firstTimeoutComplaintList) {
            if (needRepeatSendComplaintNoList.contains(complaintOrderGoOut.getDrNo())) {
                log.info("retailFirstToTimeOutCron repeat send drNo: {}", complaintOrderGoOut.getDrNo());
                needSendFirstResponse.add(complaintOrderGoOut);
                continue;
            }
            String key = String.format(FIRST_RESPONSE_TO_TIMEOUT_KEY_FORMAT, complaintOrderGoOut.getDrNo());
            String value = redisRemoteGateway.get(key);
            log.info("retailFirstToTimeOutCron FIRST_RESPONSE_TO_TIMEOUT_KEY_FORMAT key: {} , value:{}", key, value);
            if (StringUtils.isBlank(value) || !"1".equals(value)) {
                needSendFirstResponse.add(complaintOrderGoOut);
                // 设置key，防止重复推�?
                redisRemoteGateway.set(key, "1", KEY_EXPIRE_TIME, TimeUnit.DAYS);
            } else {
                log.info("retail first key:{} has already send message, skip it", key);
            }
        }

        NewComplaintMessageStrategy newMessageStrategy = newMessageInformedEventFactory.getStrategy(PushConstant.DELIVER_FIRST_RESPONSE_TO_TIMEOUT);
        for (RetailComplaintListGoOut complaintOrderGoOut : needSendFirstResponse) {
            ComplaintBasicInfo complaintBasicInfo = OrderOperationConverter.INSTANCE.toBasicInfo(complaintOrderGoOut);
            Map<String, String> extraParam = new HashMap<>();
            MessageInformedEvent newMessageInformedEvent = newMessageStrategy.createMessageInformedEvent(complaintBasicInfo, extraParam);

            log.info("retailFirstToTimeOutCron start publishEvent, newMessageInformedEvent={}",
                    JacksonUtil.toStr(newMessageInformedEvent));
            eventPublisher.publishEvent(newMessageInformedEvent);
        }
    }

    /**
     * 零售客诉结案即将超时的客诉单查询,并发送相关消�?
     */
    public void retailFinishToTimeoutCron() {
        List<RetailComplaintListGoOut> finishTimeoutComplaintList = retailComplaintGateway.selectFinishToTimeoutList();
        if (CollectionUtils.isEmpty(finishTimeoutComplaintList)) {
            return;
        }
        List<String> needRepeatComplaintNoList = new ArrayList<>();
        if (StringUtils.isNotBlank(repeatSendComplaintNoList)) {
            printRepeatSendComplaintNoListLog("retailFinishToTimeoutCron");
            needRepeatComplaintNoList = JacksonUtil.parseArray(repeatSendComplaintNoList, String.class);
        }
        List<RetailComplaintListGoOut> needSendFinishResponse = new ArrayList<>();
        for (RetailComplaintListGoOut complaintOrderGoOut : finishTimeoutComplaintList) {
            if (needRepeatComplaintNoList.contains(complaintOrderGoOut.getDrNo())) {
                log.info("retailFinishToTimeoutCron repeat send drNo: {}", complaintOrderGoOut.getDrNo());
                needSendFinishResponse.add(complaintOrderGoOut);
                continue;
            }
            String finishKey = String.format(FINISH_TO_TIMEOUT_KEY_FORMAT, complaintOrderGoOut.getDrNo());
            String finishValue = redisRemoteGateway.get(finishKey);
            log.info("retailFinishToTimeoutCron FINISH_TO_TIMEOUT_KEY_FORMAT key:{}, value:{}", finishKey, finishValue);
            if (StringUtils.isBlank(finishValue) || !"1".equals(finishValue)) {
                needSendFinishResponse.add(complaintOrderGoOut);
                // 设置key，防止重复推�?
                redisRemoteGateway.set(finishKey, "1", KEY_EXPIRE_TIME, TimeUnit.DAYS);
            } else {
                log.info("retail finish key:{} has already send message, skip it", finishKey);
            }
        }

        NewComplaintMessageStrategy newMessageStrategy = newMessageInformedEventFactory.getStrategy(PushConstant.DELIVER_FINISH_TO_TIMEOUT);
        for (RetailComplaintListGoOut complaintOrderGoOut : needSendFinishResponse) {
            ComplaintBasicInfo complaintBasicInfo = OrderOperationConverter.INSTANCE.toBasicInfo(complaintOrderGoOut);

            Map<String, String> extraParam = new HashMap<>();
            MessageInformedEvent newMessageInformedEvent = newMessageStrategy.createMessageInformedEvent(complaintBasicInfo, extraParam);
            log.info("retailFinishToTimeoutCron start publishEvent, newMessageInformedEvent={}",
                    JacksonUtil.toStr(newMessageInformedEvent));
            eventPublisher.publishEvent(newMessageInformedEvent);
        }
    }

    /**
     * 打印日志
     * @param method 方法�?
     */
    public void printRepeatSendComplaintNoListLog(String method) {
        log.info("CronPushTaskService#{} repeatSendComplaintNoList is not empty, " +
                        "repeatSendComplaintNoList: {}", method, repeatSendComplaintNoList);
    }


    /**
     * 服务客诉�?5天未结案客诉单查�?并发送相关消�?
     */
    public void unFinishedToTimeoutCron() {
        List<ComplaintOrderGoOut> unFinishedTimeoutComplaintList = complaintGateway.selectUnFinishedToTimeoutList();
        if (CollectionUtils.isEmpty(unFinishedTimeoutComplaintList)) {
            return;
        }
        List<String> needRepeatComplaintNoList = new ArrayList<>();
        if (StringUtils.isNotBlank(repeatSendComplaintNoList)) {
            printRepeatSendComplaintNoListLog("unFinishedToTimeoutCron");
            needRepeatComplaintNoList = JacksonUtil.parseArray(repeatSendComplaintNoList, String.class);
        }
        List<ComplaintOrderGoOut> needSendFinishResponse = new ArrayList<>();
        for (ComplaintOrderGoOut complaintOrderGoOut : unFinishedTimeoutComplaintList) {
            if (needRepeatComplaintNoList.contains(complaintOrderGoOut.getComplaintNo())) {
                log.info("unFinishedToTimeoutCron repeat send complaintNo: {}", complaintOrderGoOut.getComplaintNo());
                needSendFinishResponse.add(complaintOrderGoOut);
                continue;
            }
            String finishKey = String.format(UN_FINISHED_TO_TIMEOUT_KEY_FORMAT, complaintOrderGoOut.getComplaintNo());
            String finishValue = redisRemoteGateway.get(finishKey);
            log.info("UN_FINISHED_TO_TIMEOUT_KEY_FORMAT key:{}, value:{}", finishKey, finishValue);
            if (StringUtils.isBlank(finishValue) || !"1".equals(finishValue)) {
                needSendFinishResponse.add(complaintOrderGoOut);
                // 设置key，防止重复推�?
                redisRemoteGateway.set(finishKey, "1", KEY_EXPIRE_TIME, TimeUnit.DAYS);
            } else {
                log.info("unFinished key:{} has already send message, skip it", finishKey);
            }
        }
        MessageInformedStrategy finishMessageStrategy =
                messageInformedEventFactory.getStrategy(PushConstant.UN_FINISHED_TO_TIMEOUT);
        for (ComplaintOrderGoOut complaintOrderGoOut : needSendFinishResponse) {
            MessageInformedEvent messageInformedEvent = finishMessageStrategy.createMessageInformedEvent(
                    complaintOrderGoOut,
                    new HashMap<>());
            log.info("unFinishedToTimeoutCron start publishEvent, messageInformedEvent={}",
                    JacksonUtil.toStr(messageInformedEvent));
            eventPublisher.publishEvent(messageInformedEvent);
        }
    }


}
