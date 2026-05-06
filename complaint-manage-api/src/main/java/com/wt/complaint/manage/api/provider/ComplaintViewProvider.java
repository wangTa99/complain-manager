package com.wt.complaint.manage.api.provider;

import com.wt.complaint.manage.api.model.req.*;
import com.wt.complaint.manage.api.model.resp.*;
import com.xiaomi.youpin.infra.rpc.Result;

public interface ComplaintViewProvider {
     Result<ComplaintDetailFrameResp> getComplaintFrame(ComplaintDetailFrameReq req);

     Result<ComplaintDetailFrameResp> getComplaintAuth(ComplaintDetailFrameReq req);

     Result<String> refreshCacheTtl();

     Result<ComplaintDetailResp> getComplaintDetail(ComplaintDetailReq req);

     Result<ComplaintDetailBatchResp> batchGetComplaintDetail(ComplaintDetailBatchReq req);

     Result<ComplaintFollowUpRecordsResp> getFollowUpRecords(ComplaintFollowUpRecordsReq req);

     Result<SimpleComplaintDetailResp> getSimpleComplaintDetail(SimpleComplaintDetailReq req);

     /**
      * 获取投诉单详�?前端调用，mid脱敏)
      * @param req
      * @return
      */
     Result<SimpleComplaintDetailV2Resp> getSimpleComplaintDetailV2(SimpleComplaintDetailReq req);

     Result<ComplaintListSearchResp> searchComplaintList(ComplaintListSearchReq req);

     Result<CountComplaintListTabResp> countComplaintListTab(ComplaintListSearchReq req);

     Result<ComplaintHandlerListResp> getComplaintHandlerList(ComplaintHandlerListReq req);

     Result<ComplaintListExportRes> exportComplaintList(ComplaintListSearchReq request);

    Result<ComplaintEditDetailResp> getComplaintEditDetail(ComplaintDetailReq req);
}
