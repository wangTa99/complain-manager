package com.wt.complaint.manage.domain.api.gateway.interfaces.rpc;

public interface NoGeneratorRemoteGateway {
    public String generateComplaintNo();

    public String generateUcNoWithPrefix(String prefix);

    /**
     * 生成咨询单号
     * @return 咨询单号
     */
    public String generateConsultNo();
}
