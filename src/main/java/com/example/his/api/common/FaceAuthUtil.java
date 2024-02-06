package com.example.his.api.common;

import cn.hutool.core.util.StrUtil;
import com.example.his.api.exception.HisException;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.iai.v20180301.IaiClient;
import com.tencentcloudapi.iai.v20180301.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FaceAuthUtil {
    @Value("${tencent.cloud.secretId}")
    private String secretId;

    @Value("${tencent.cloud.secretKey}")
    private String secretKey;

    @Value("${tencent.cloud.face.groupId}")
    private String groupdId;

    @Value("${tencent.cloud.face.region}")
    private String region;

    /**
     * 人脸识别+活体验证
     *
     * @param name
     * @param pid
     * @param sex
     * @param photo_1
     * @param photo_2
     * @return
     */
    public boolean verifyFaceModel(String name, String pid, String sex, String photo_1, String photo_2) {
        boolean result;
        Credential cred = new Credential(secretId, secretKey);
        IaiClient client = new IaiClient(cred, region);

        //执行人脸比对
        CompareFaceRequest compareFaceRequest = new CompareFaceRequest();
        compareFaceRequest.setImageA(photo_1);
        compareFaceRequest.setImageB(photo_2);
        CompareFaceResponse compareFaceResponse = null;
        try {
            compareFaceResponse = client.CompareFace(compareFaceRequest);
        } catch (TencentCloudSDKException e) {
            log.error("人脸比对失败", e);
            throw new HisException("人脸比对失败！");
        }
        Float score = compareFaceResponse.getScore();
        if (score >= 50) {
            //执行静态活体验证
            DetectLiveFaceRequest detectLiveFaceRequest = new DetectLiveFaceRequest();
            detectLiveFaceRequest.setImage(photo_2);
            DetectLiveFaceResponse detectLiveFaceResponse = null;
            try {
                detectLiveFaceResponse = client.DetectLiveFace(detectLiveFaceRequest);
            } catch (TencentCloudSDKException e) {
                log.error("静态活体识别失败", e);
                throw new HisException("静态活体识别失败！");
            }
            result = detectLiveFaceResponse.getIsLiveness();
        } else {
            result = false;
            return result;
        }
        //判断人员库是否有该体检人
        //判断体检人添加到人员库
//        if (result) {
//            //查询人员库中是否有该体检人
//            GetPersonBaseInfoRequest getPersonBaseInfoRequest = new GetPersonBaseInfoRequest();
//            getPersonBaseInfoRequest.setPersonId(pid);
//            GetPersonBaseInfoResponse getPersonBaseInfoResponse = null;
//            try {
//                getPersonBaseInfoResponse = client.GetPersonBaseInfo(getPersonBaseInfoRequest);
//            } catch (TencentCloudSDKException e) {
//
//                log.error("查询人员库失败", e.getMessage().toString());
//                log.error("查询人员库失败", e.getCause());
//                log.error("查询人员库失败", e.getErrorCode().toString());
//                log.error("查询人员库失败", e.getLocalizedMessage());
//                throw new HisException("查询人员库失败");
//            }
//            String personName = getPersonBaseInfoResponse.getPersonName();
//            if (personName == null) {
//                CreatePersonRequest createPersonRequest = new CreatePersonRequest();
//                createPersonRequest.setGroupId(groupdId);
//                createPersonRequest.setPersonId(pid);
//                long gender = sex.equals("男") ? 1L : 2L;
//                createPersonRequest.setGender(gender);
//                createPersonRequest.setPersonId(pid);
//                createPersonRequest.setQualityControl(4L);
//                createPersonRequest.setUniquePersonControl(4L);
//                createPersonRequest.setPersonName(name);
//                createPersonRequest.setImage(photo_1);
//                CreatePersonResponse createPersonResponse = null;
//                try {
//                    createPersonResponse = client.CreatePerson(createPersonRequest);
//                } catch (TencentCloudSDKException e) {
//                    log.error("添加体检人到人员库失败", e);
//                    throw new HisException("添加体检人到人员库失败");
//                }
//                if (StrUtil.isNotBlank(createPersonResponse.getFaceId())) {
//                    log.debug("体检人成功添加到人员库");
//                } else {
//                    log.error("添加体检人到人员库失败");
//                    throw new HisException("添加体检人到人员库失败");
//                }
//            }
//        }
        return result;
    }


    public boolean selectFaceModelByPid(String pid) {
        boolean result = false;
        Credential cred = new Credential(secretId, secretKey);
        IaiClient client = new IaiClient(cred, region);
        GetPersonBaseInfoRequest getPersonBaseInfoRequest = new GetPersonBaseInfoRequest();
        getPersonBaseInfoRequest.setPersonId(pid);
        GetPersonBaseInfoResponse getPersonBaseInfoResponse = null;
        try {
            getPersonBaseInfoResponse = client.GetPersonBaseInfo(getPersonBaseInfoRequest);
            if (StrUtil.isNotBlank(getPersonBaseInfoResponse.getPersonName())) {
                result = true;
            }
        } catch (TencentCloudSDKException e) {
            log.error("查询人员库失败", e);
            throw new HisException("查询人员库失败");
        } finally {
            return result;
        }
    }

    public boolean createFaceModel(String name, String pid, String sex, String photo_1, String photo_2) {
        boolean result = false;
        Credential cred = new Credential(secretId, secretKey);
        IaiClient client = new IaiClient(cred, region);
        CreatePersonRequest createPersonRequest = new CreatePersonRequest();
        createPersonRequest.setGroupId(groupdId);
        createPersonRequest.setPersonId(pid);
        long gender = sex.equals("男") ? 1L : 2L;
        createPersonRequest.setGender(gender);
        createPersonRequest.setPersonId(pid);
        createPersonRequest.setQualityControl(4L);
        createPersonRequest.setUniquePersonControl(4L);
        createPersonRequest.setPersonName(name);
        createPersonRequest.setImage(photo_1);
        CreatePersonResponse createPersonResponse = null;
        try {
            createPersonResponse = client.CreatePerson(createPersonRequest);
            System.out.println(CreatePersonResponse.toJsonString(createPersonResponse));
            if (StrUtil.isNotBlank(createPersonResponse.getFaceId())) {
                log.debug("体检人成功添加到人员库");
                result = true;
            }
        } catch (TencentCloudSDKException e) {
            log.error("添加体检人到人员库失败", e);
            throw new HisException("添加体检人到人员库失败");
        } finally {
            return result;
        }

    }
}
