package org.bingjian.storage;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bingjian.config.properties.OssProperties;
import org.bingjian.model.NaNaCopyObjectResult;
import org.bingjian.model.NaNaObjectMetadata;
import org.bingjian.model.NaNaPutObjectResult;
import org.bingjian.strategy.impl.AbstractFileStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * HuaweiOSS 策略处理类
 *
 * @author fanglong
 */
@EnableConfigurationProperties(OssProperties.class)
@Configuration
@Slf4j
@ConditionalOnProperty(prefix = OssProperties.PREFIX, name = "type", havingValue = "huawei", matchIfMissing = true)
public class HuaweiOssAutoConfigure {

    @Service
    public class HuaweiServiceImpl extends AbstractFileStrategy {

        private ObsClient obsClient = null;

        @PostConstruct
        public void init() {
            obsClient = new ObsClient(fileProperties.getAccessKeyId(), fileProperties.getAccessKeySecret(), fileProperties.getEndpoint());
            if (StrUtil.isNotBlank(fileProperties.getBucketName())) {
                createHeadBucket(fileProperties.getBucketName());
            }
        }

        @Override
        public boolean headBucket(String bucketName) {
            return obsClient.headBucket(bucketName);
        }

        @Override
        public void createBucket(String bucketName) {
            obsClient.createBucket(bucketName);
        }

        @Override
        public void createHeadBucket(String bucketName) {
            if (!headBucket(bucketName)) {
                createBucket(bucketName);
            }
        }

//        /**
//         * 对象查询自定义包装
//         * @param objectMetadata
//         * @return
//         */
//        private OssFile getOssFile(ObjectMetadata objectMetadata){
//            OssFile ossFile = OssFile.builder()
//                    .size(objectMetadata.getContentLength())
//                    .lastModifiedDate(objectMetadata.getLastModified())
//                    .contentType(objectMetadata.getContentType()).build();
//            return ossFile;
//        }

        @Override
        public NaNaObjectMetadata getObjectMetadata(String bucketName, String objectKey) {
            ObjectMetadata objectMetadata = obsClient.getObjectMetadata(bucketName, objectKey);
            return NaNaObjectMetadata.builder()
                    .objectName(objectKey)
                    .contentLength(objectMetadata.getContentLength())
                    .lastModified(objectMetadata.getLastModified())
                    .contentType(objectMetadata.getContentType()).build();
        }

        @Override
        public NaNaPutObjectResult uploadFile(String objectKey, File file) {
            return uploadFile(fileProperties.getBucketName(), objectKey, file);
        }

        @Override
        public NaNaPutObjectResult uploadFile(String objectKey, byte[] bytes) {
            return uploadFile(fileProperties.getBucketName(), objectKey, bytes);
        }

        @Override
        public NaNaPutObjectResult uploadFile(String bucketName, String objectName, byte[] bytes) {
            InputStream inputStream = new ByteArrayInputStream(bytes);
            return uploadFile(bucketName, objectName, inputStream);
        }

        @Override
        @SneakyThrows
        public NaNaPutObjectResult uploadFile(String bucketName, String objectKey, File file) {
            try (InputStream inputStream = FileUtil.getInputStream(file)) {
                return uploadFile(bucketName, objectKey, inputStream);
            }
        }

        @Override
        public NaNaPutObjectResult uploadFile(String bucketName, String objectKey, InputStream inputStream) {
            PutObjectResult putObjectResult = obsClient.putObject(bucketName, objectKey, inputStream);
            String code = putObjectResult.getObjectStorageClass().getCode();
            return NaNaPutObjectResult.builder()
                    .bucketName(putObjectResult.getBucketName())
                    .objectKey(putObjectResult.getObjectKey())
                    .statusCode(putObjectResult.getStatusCode())
                    .storageClass(org.bingjian.enums.StorageClassEnum.getValueFromCode(code))
                    .build();
        }

        @Override
        public void downloadFile(String bucketName, String objectKey, File file) {
            DownloadFileRequest request = new DownloadFileRequest(bucketName, objectKey);
            request.setDownloadFile(file.getAbsolutePath());
            obsClient.downloadFile(request);
        }

        @Override
        public void downloadBigFile(String bucketName, String objectKey, File file) {
            DownloadFileRequest request = new DownloadFileRequest(bucketName, objectKey);
            // 设置下载对象的本地文件路径
            request.setDownloadFile(file.getAbsolutePath());
            // 设置分段下载时的最大并发数
            request.setTaskNum(5);
            // 设置分段大小为10MB
            request.setPartSize(10 * 1024 * 1024);
            // 开启断点续传模式
            request.setEnableCheckpoint(true);
            try {
                // 进行断点续传下载
                DownloadFileResult result = obsClient.downloadFile(request);
            } catch (ObsException e) {
                // 发生异常时可再次调用断点续传下载接口进行重新下载

            }
        }

        @Override
        @SneakyThrows
        public NaNaPutObjectResult uploadFile(String bucketName, String objectKey, String fileUrl) {
            byte[] input = HttpUtil.downloadBytes(fileUrl);
            try (InputStream initialStream = new ByteArrayInputStream(input)) {
                return uploadFile(bucketName, objectKey, initialStream);
            }
        }

        @Override
        public void moveObjectKey(String bucketName, String objectKey, String destBucketName, String destObjectName) {
            copyObjectKey(bucketName, objectKey, destBucketName, destObjectName);
            deleteObjectKey(bucketName, objectKey);
        }

        @Override
        public NaNaCopyObjectResult copyObjectKey(String bucketName, String objectKey, String destBucketName, String destObjectName) {
            CopyObjectResult copyObjectResult = obsClient.copyObject(bucketName, objectKey, destBucketName, destObjectName);
            return NaNaCopyObjectResult.builder()
                    .lastModified(copyObjectResult.getLastModified())
                    .storageClass(org.bingjian.enums.StorageClassEnum.getValueFromCode(copyObjectResult.getObjectStorageClass().getCode()))
                    .build();
        }

        @Override
        public String getSignedUrl(String bucketName, String objectKey) {
            return getSignedUrl(getGetTemporarySignatureRequest(bucketName, objectKey));
        }

        @Override
        public void deleteObjectKey(String bucketName, String objectKey) {
            obsClient.deleteObject(bucketName, objectKey);
        }

        @Override
        public String getSignedUrl(String bucketName, String objectKey, int width, int height, boolean isRotate, int rotateAngle) {
            TemporarySignatureRequest request = getGetTemporarySignatureRequest(bucketName, objectKey);
            // 设置图片处理参数，对图片依次进行缩放、旋转
            Map<String, Object> queryParams = new HashMap<String, Object>(1);
            String imageProcess = "image/resize,m_fixed," + "w_" + width + "," + "h_" + height + ",";
            if (isRotate) {
                imageProcess += "/rotate," + rotateAngle;
            }
            queryParams.put("x-image-process", imageProcess);
            request.setQueryParams(queryParams);
            return getSignedUrl(request);
        }

        /**
         * 获取临时授权请求对象
         *
         * @param bucketName
         * @param objectKey
         * @return
         */
        private TemporarySignatureRequest getGetTemporarySignatureRequest(String bucketName, String objectKey) {
            TemporarySignatureRequest request = new TemporarySignatureRequest(HttpMethodEnum.GET, 3600L);
            request.setBucketName(bucketName);
            request.setObjectKey(objectKey);
            return request;
        }

        /**
         * 生成临时授权URL
         *
         * @param request
         * @return
         */
        private String getSignedUrl(TemporarySignatureRequest request) {
            TemporarySignatureResponse response = obsClient.createTemporarySignature(request);
            return response.getSignedUrl();
        }
    }
}
