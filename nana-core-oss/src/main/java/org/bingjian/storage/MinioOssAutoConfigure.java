package org.bingjian.storage;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import io.minio.*;
import io.minio.http.Method;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bingjian.config.properties.OssProperties;
import org.bingjian.enums.StorageClassEnum;
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
import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * MinioOSS 策略处理类
 *
 * @author fanglong
 */
@EnableConfigurationProperties(OssProperties.class)
@Configuration
@Slf4j
@ConditionalOnProperty(prefix = OssProperties.PREFIX, name = "type", havingValue = "minio", matchIfMissing = true)
public class MinioOssAutoConfigure {

    @Service
    public class MinioServiceImpl extends AbstractFileStrategy {

        private MinioClient minioClient = null;

        @PostConstruct
        public void init() {
            minioClient = MinioClient.builder()
                    .endpoint(fileProperties.getEndpoint())
                    .credentials(fileProperties.getAccessKeyId(), fileProperties.getAccessKeySecret())
                    .build();
            createHeadBucket(fileProperties.getBucketName());
        }

        @Override
        @SneakyThrows
        public boolean headBucket(String bucketName) {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        }

        @Override
        @SneakyThrows
        public void createBucket(String bucketName) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }

        @Override
        public void createHeadBucket(String bucketName) {
            if (!headBucket(bucketName)) {
                createBucket(bucketName);
            }
        }

        @Override
        @SneakyThrows
        public NaNaObjectMetadata getObjectMetadata(String bucketName, String objectKey) {
            StatObjectResponse statObjectResponse =
                    minioClient.statObject(
                            StatObjectArgs.builder().bucket(bucketName).object(objectKey).build());
            return NaNaObjectMetadata.builder()
                    .bucketName(statObjectResponse.bucket())
                    .objectName(statObjectResponse.object())
                    .contentLength(statObjectResponse.size())
                    .storageClass(StorageClassEnum.STANDARD)
                    .lastModified(Date.from(statObjectResponse.lastModified().toInstant()))
                    .contentType(statObjectResponse.contentType()).build();
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
        @SneakyThrows
        public NaNaPutObjectResult uploadFile(String bucketName, String objectKey, InputStream inputStream) {
            long size = inputStream.available();
            ObjectWriteResponse objectWriteResponse = minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectKey)
                    .stream(inputStream, size, -1).build());
            return NaNaPutObjectResult.builder()
                    .bucketName(objectWriteResponse.bucket())
                    .objectKey(objectWriteResponse.object())
                    .etag(objectWriteResponse.etag())
                    .versionId(objectWriteResponse.versionId())
                    .build();
        }

        @Override
        @SneakyThrows
        public void downloadFile(String bucketName, String objectKey, File file) {
            minioClient.downloadObject(DownloadObjectArgs.builder().bucket(bucketName).object(objectKey).filename(file.getAbsolutePath()).build());
        }

        @Override
        public void downloadBigFile(String bucketName, String objectKey, File file) {
            downloadFile(bucketName, objectKey, file);
        }

        @Override
        @SneakyThrows
        public NaNaPutObjectResult uploadFile(String bucketName, String objectKey, String fileUrl) {
            byte[] input = HttpUtil.downloadBytes(fileUrl);
            try (InputStream initialStream = new ByteArrayInputStream(input);) {
                return uploadFile(bucketName, objectKey, initialStream);
            }
        }

        @Override
        public void moveObjectKey(String bucketName, String objectKey, String destBucketName, String destObjectName) {
            copyObjectKey(bucketName, objectKey, destBucketName, destObjectName);
            deleteObjectKey(bucketName, objectKey);
        }

        @Override
        @SneakyThrows
        public NaNaCopyObjectResult copyObjectKey(String bucketName, String objectKey, String destBucketName, String destObjectName) {
            ObjectWriteResponse objectWriteResponse = minioClient.copyObject(CopyObjectArgs.builder()
                    .source(CopySource.builder().bucket(bucketName).object(objectKey).build())
                    .bucket(destBucketName).object(destObjectName).build());
            return NaNaCopyObjectResult.builder().build();
        }

        @Override
        @SneakyThrows
        public String getSignedUrl(String bucketName, String objectKey) {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectKey)
                            .expiry(2, TimeUnit.HOURS)
                            .build());
        }

        @Override
        @SneakyThrows
        public void deleteObjectKey(String bucketName, String objectKey) {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectKey).build());
        }

        @Override
        public String getSignedUrl(String bucketName, String objectKey, int width, int height, boolean isRotate, int rotateAngle) {
            return null;
        }

    }
}
