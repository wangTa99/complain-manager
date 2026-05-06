package com.wt.complaint.manage.infrastructure.gatewayimpl.rpc;

import cn.hutool.http.HttpUtil;
import com.google.gson.Gson;
import com.wt.commons.utils.JacksonUtil;
import com.wt.complaint.manage.api.model.constont.DubboConstant;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.FileRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.FileInfoGoOut;
import com.wt.complaint.manage.domain.bo.FileInfoBO;
import com.wt.complaint.manage.domain.param.PicCommitParam;
import com.wt.complaint.manage.api.model.resp.PicCommitRes;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.infrastructure.model.param.UploadFileBO;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.nr.upload.client.api.FileApi;
import com.xiaomi.nr.upload.client.dto.CommitReq;
import com.xiaomi.nr.upload.client.dto.CommitRes;
import com.xiaomi.nr.upload.client.dto.dataobject.file.FileAddrDTO;
import com.xiaomi.nr.upload.client.dto.dataobject.file.FileAddrQryReq;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.auth.utils.SignatureUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.*;

@Slf4j
@Service
public class FileRemoteGatewayImpl implements FileRemoteGateway {
    @DubboReference(group = "${dubbo.group.file}", check = false, interfaceClass = FileApi.class, timeout = DubboConstant.TIME_OUT)
    private FileApi fileApi;

    @Value("${dubbo.nr-upload-center.projectId}")
    private Long projectId;

    @Value("${dubbo.nr-upload-center.url}")
    private String url;

    @Value("${dubbo.nr-upload-center.appKey}")
    private String appKey;

    @Value("${dubbo.nr-upload-center.appId}")
    private String appId;

    @Override
    public FileInfoGoOut uploadFile(File file) {
        FileInfoGoOut fileInfoBO = FileInfoGoOut.builder().build();
        try {
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("file", file);
            paramsMap.put("fileBizType", "car");
            paramsMap.put("publicIdentify", 0);
            paramsMap.put("networkIdentify", 1);
            paramsMap.put("projectId", projectId);
            paramsMap.put("timestamp", System.currentTimeMillis() / 1000);
            log.info("uploadFile req{}", GsonUtil.toJson(paramsMap));
            String res = HttpUtil.post(url + "/file/upload", paramsMap);
            log.info("uploadFile req{}, resp{}", GsonUtil.toJson(paramsMap), GsonUtil.toJson(res));
            UploadFileBO uploadFileBO = GsonUtil.fromJson(res, UploadFileBO.class);
            if (uploadFileBO != null && uploadFileBO.getData() != null) {
                fileInfoBO.setFileId(Long.valueOf(uploadFileBO.getData().getId()));
                fileInfoBO.setFileUrl(uploadFileBO.getData().getTmpUri());
                return fileInfoBO;
            } else {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "文件上传异常");
            }
        } catch (Exception e) {
            log.error("文件上传异常�?, e);
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "文件上传异常");
        }
    }

    @Override
    public void fileCommit(List<Long> fileIdList) {
        CommitReq commitReq = new CommitReq();
        try {
            if (CollectionUtils.isEmpty(fileIdList)) {
                return;
            }
            commitReq.setIds(fileIdList);
            commitReq.setProjectId(projectId);
            log.info("commitFile req{}", GsonUtil.toJson(commitReq));
            //验签
            String currentTime = String.valueOf(System.currentTimeMillis() / 1000L);
            String s = sign(new Object[] {commitReq}, currentTime);
            createSign(s, currentTime);
            Result<CommitRes> result = fileApi.commit(commitReq);
            if (result.getCode() != GeneralCodes.OK.getCode() || result.getData() == null) {
                log.error("文件提交失败,req:{},res:{}", GsonUtil.toJson(commitReq),
                    GsonUtil.toJson(result));
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "文件上传commit异常");
            }
            if (!CollectionUtils.isEmpty(result.getData().getIds())) {
                log.error("文件过期,req:{},res:{}", GsonUtil.toJson(commitReq), GsonUtil.toJson(result));
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "文件无效");
            }
            log.info("commitFile resp{}", GsonUtil.toJson(result));
        } catch (Exception e) {
            log.error("文件提交失败,req:{},e:", GsonUtil.toJson(commitReq), e);
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "文件上传commit异常");
        }

    }

    @Override
    public List<FileInfoGoOut> getFileList(List<Long> fileIdList, Integer expireTime) {
        if (CollectionUtils.isEmpty(fileIdList)) {
            return Collections.emptyList();
        }
        FileAddrQryReq request = new FileAddrQryReq();
        if (Objects.nonNull(expireTime)) {
            request.setExpireTime(expireTime);
        }
        request.setProjectId(projectId);
        request.setIds(fileIdList);
        String currentTime = String.valueOf(System.currentTimeMillis() / 1000L);
        String s = sign(new Object[] {request}, currentTime);
        createSign(s, currentTime);
        try {
            List<FileInfoGoOut> list = new ArrayList<>();
            Result<List<FileAddrDTO>> result = fileApi.getDownLoadAddr(request);
            log.info("文件查询成功,req:{},res:{}", GsonUtil.toJson(request), GsonUtil.toJson(result));
            List<FileAddrDTO> fileAddrs = Optional.ofNullable(result.getData()).orElse(new ArrayList<>());
            for (FileAddrDTO fileAddr : fileAddrs) {
                FileInfoGoOut fileInfo = FileInfoGoOut.builder().fileId(fileAddr.getId()).fileUrl(fileAddr.getUrl())
                    .fileName(fileAddr.getFileName()).build();
                list.add(fileInfo);
            }
            return list;
        } catch (Exception e) {
            log.error("文件查询失败,req:{},e", GsonUtil.toJson(request), e);
            return new ArrayList<>();
        }
    }


    @Override
    public FileInfoBO uploadPublicFile(String filePath) {
        FileInfoBO fileInfoBO = new FileInfoBO();
        try {
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("file", new File(filePath));
            paramsMap.put("fileBizType", "car");
            paramsMap.put("publicIdentify", 1);
            paramsMap.put("networkIdentify", 0);
            paramsMap.put("projectId", projectId);
            paramsMap.put("timestamp", System.currentTimeMillis() / 1000);
            log.info("uploadFile req{}", JacksonUtil.toStr(paramsMap));
            String res = HttpUtil.post(url + "/file/upload", paramsMap);
            log.info("uploadFile req{}, resp{}", JacksonUtil.toStr(paramsMap), JacksonUtil.toStr(res));
            UploadFileBO uploadFileBO = GsonUtil.fromJson(res, UploadFileBO.class);
            if (uploadFileBO != null && uploadFileBO.getData() != null) {
                fileInfoBO.setFileId(Long.valueOf(uploadFileBO.getData().getId()));
                fileInfoBO.setFileUrl(uploadFileBO.getData().getUri());
                return fileInfoBO;
            } else {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "文件上传异常");
            }
        } catch (Exception e) {
            log.error("文件上传异常�?, e);
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "文件上传异常");
        }
    }

    @Override
    public PicCommitRes fileCommit(PicCommitParam param) {
        CommitReq commitReq = new CommitReq();
        try {
            if (Objects.isNull(param) || CollectionUtils.isEmpty(param.getIds())) {
                return new PicCommitRes();
            }
            commitReq.setIds(param.getIds());
            commitReq.setProjectId(projectId);
            log.info("commitFile req{}", GsonUtil.toJson(commitReq));
            //验签
            String currentTime = String.valueOf(System.currentTimeMillis() / 1000L);
            String s = sign(new Object[] {commitReq}, currentTime);
            createSign(s, currentTime);
            Result<CommitRes> result = fileApi.commit(commitReq);
            if (result.getCode() != GeneralCodes.OK.getCode() || result.getData() == null) {
                log.error("文件提交失败,req:{},res:{}", GsonUtil.toJson(param), GsonUtil.toJson(result));
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "文件上传commit异常");
            }
            if (!CollectionUtils.isEmpty(result.getData().getIds())) {
                log.error("文件过期,req:{},res:{}", GsonUtil.toJson(param), GsonUtil.toJson(result));
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "文件无效");
            }
            log.info("commitFile resp{}", GsonUtil.toJson(result));
            PicCommitRes commitRes = new PicCommitRes();
            commitRes.setIds(result.getData().getIds());
            commitRes.setProjectId(result.getData().getProjectId());
            return commitRes;
        } catch (Exception e) {
            log.error("文件提交失败,req:{},e:{}", GsonUtil.toJson(param), e);
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "文件上传commit异常");
        }
    }

    public String sign(Object[] args, String currentTime) {
        return getSignature(null, args, appKey, currentTime);
    }

    String getSignature(String bodyMd5, Object[] args, String secretKey, String time) {
        String requestString = String.format("%s#%s", secretKey, time);
        //String bodyMd5 = invocation.getAttachment("$bodyMd5");
        Gson gson = new Gson();
        String s = gson.toJson(args);
        Object[] objects = new Object[] {s};
        String signature;
        if (StringUtils.isEmpty(bodyMd5)) {
            signature = SignatureUtils.sign(objects, requestString, secretKey);
        } else {
            signature = SignatureUtils.sign(requestString + "#" + bodyMd5, secretKey);
        }
        return signature;
    }

    public void createSign(String s, String currentTime) {
        RpcContext.getContext().setAttachment("$signature", s);
        RpcContext.getContext().setAttachment("$timestamp", currentTime);
        RpcContext.getContext().setAttachment("$appId", appId);
        RpcContext.getContext().setAttachment("$consumer", "nr-upload-center");
    }
}
