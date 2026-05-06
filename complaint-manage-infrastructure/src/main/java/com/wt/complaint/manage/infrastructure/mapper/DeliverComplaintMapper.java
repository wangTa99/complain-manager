package com.wt.complaint.manage.infrastructure.mapper;

import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.DeliverComplaintDetailGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.DeliverComplaintListGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.DeliverComplaintUpdateGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.DeliverStatisticsItemGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.PageGoIn;
import com.wt.complaint.manage.domain.bo.DeliverStatisticsItemBO;
import com.wt.complaint.manage.infrastructure.model.DeliverComplaintDO;
import com.wt.complaint.manage.infrastructure.model.param.OrderListParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author huxiankang
 * @date 2025/6/13
 */
@Repository
public interface DeliverComplaintMapper {

    List<DeliverComplaintDO> selectListByCondition(DeliverComplaintListGoIn deliverComplaintListGoIn);

    Long selectCountByCondition(DeliverComplaintListGoIn goIn);

    DeliverStatisticsItemBO selectStatisticsItems(DeliverStatisticsItemGoIn goIn);

    DeliverComplaintDO selectDetail(DeliverComplaintDetailGoIn goIn);

    DeliverComplaintDO selectByDrNo(@Param("drNo") String drNo);

    List<DeliverComplaintDO> selectByDrNoList(@Param("drNoList") List<String> drNoList);

    List<DeliverComplaintDO> selectByStNoList(@Param("stNoList") List<String> stNoList);

    void updateOrderStatus(@Param("drNo") String drNo, @Param("orderStatus") Integer orderStatus);

    void updateByDrNo(DeliverComplaintDO complaintDO);

    /**
     * 批量更新跟进客服mid，基于drNo
     *
     * @param updateParamList 批量更新参数列表
     * @return 更新的记录数
     */
    int batchUpdateByDrNo(@Param("updateParamList") List<DeliverComplaintUpdateGoIn> updateParamList);

    List<DeliverComplaintDO> selectFirstResponseToTimeoutList();

    List<DeliverComplaintDO> selectFinishToTimeoutList();

    List<String> selectHasFirstResponseTimeOutTagList(OrderListParam orderListParam);

    List<String> selectNotHasFirstResponseTimeOutTagList(OrderListParam orderListParam);

    List<String> selectHasFinishTimeOutTagList(OrderListParam orderListParam);

    List<String> selectNotHasFinishTimeOutTagList(OrderListParam orderListParam);

    List<DeliverComplaintDO> selectEmptyComplaintScene();

    /**
     * 批量更新投诉场景和末级投诉场景ID，基于drNo
     *
     * @param updateList 批量更新参数列表
     * @return 更新的记录数
     */
    int batchUpdateComplaintSceneByDrNo(@Param("updateList") List<DeliverComplaintDO> updateList);


    List<DeliverComplaintDO> selectByPageGoIn(PageGoIn pageGoIn);

    /**
     * 批量更新城市区域id，基于drNo
     * @param updateList 批量更新参数列表
     * @return 更新的记录数
     */
    int batchUpdateCityZoneIdByDrNo(@Param("updateList") List<DeliverComplaintDO> updateList);

}
