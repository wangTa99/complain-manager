package com.wt.complaint.manage.domain.api.gateway.interfaces;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.ConsultListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.UcConsultOrderGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.UcConsultOrderUpdateGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.UserConsultOrderMainGoOut;
import com.wt.complaint.manage.domain.model.ConsultStatusCountInfo;
import com.wt.complaint.manage.domain.model.UserConsultOrderInfo;

import java.util.List;

/**
 * 咨询类单据网�?
 * @author linjiehong
 * @date 2025/5/21 15:39
 */
public interface UserConsultOrderGateway {
    /**
     * 创建咨询主表数据
     * @param param 建单入参
     * @return 主表 id
     */
    int createUserConsultOrder(UcConsultOrderGoIn param);

    /**
     * 更新咨询主表数据
     * @param param 更新入参
     * @return 更新结果
     */
    int updateOrderSelective(UcConsultOrderUpdateGoIn param);

    /**
     * 查询咨询主表数据
     * @param goIn 查询入参
     * @return 查询结果
     */
    UserConsultOrderMainGoOut searchUserConsultMainData(UcConsultOrderGoIn goIn);

    UserConsultOrderInfo searchUserConsultOrderInfo(UcConsultOrderGoIn goIn);

    /**
     * 按门店id列表统计各状态数�?
     * @param orgId  门店id
     * @return Map<orderStatus, count>
     */
    List<ConsultStatusCountInfo> countConsultStatistics(String orgId,Long mid);

    /**
     * PAD零售通：分页查询咨询单列表总数（key 模糊匹配，无 consultNo 精准匹配�?
     */
    long countPadConsultPage(ConsultListGoIn goIn);

    /**
     * PAD零售通：分页查询咨询单列表（key 模糊匹配，无 consultNo 精准匹配�?
     */
    List<UserConsultOrderInfo> pagePadConsultOrders(ConsultListGoIn goIn);

    /**
     * 售后工作台：分页查询咨询单列表总数（支�?consultNo 精准匹配�?
     */
    long countWebConsultPage(ConsultListGoIn goIn);

    /**
     * 售后工作台：分页查询咨询单列表（支持 consultNo 精准匹配�?
     */
    List<UserConsultOrderInfo> pageWebConsultOrders(ConsultListGoIn goIn);

    List<UserConsultOrderInfo> findList(ConsultListGoIn build);
}
