package org.bingjian.storage;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
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
import java.io.*;
import java.net.URL;


/**
 * TianyiOSS 策略处理类
 *
 * @author fanglong
 */
@EnableConfigurationProperties(OssProperties.class)
@Configuration
@Slf4j
@ConditionalOnProperty(prefix = OssProperties.PREFIX, name = "type", havingValue = "tianyi", matchIfMissing = true)
public class TianyiOssAutoConfigure {

    @Service
    public class TianyiServiceImpl extends AbstractFileStrategy {

        private AmazonS3 s3 = null;

        @PostConstruct
        public void init() {
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            //设置client的最大HTTP连接数
            clientConfiguration.setMaxConnections(50);
            //设置Socket层超时时间
            clientConfiguration.setSocketTimeout(50000);
            //设置建立连接的超时时间
            clientConfiguration.setConnectionTimeout(50000);
            BasicAWSCredentials credentials = new BasicAWSCredentials(fileProperties.getAccessKeyId(), fileProperties.getAccessKeySecret());
            AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(
                    fileProperties.getEndpoint(), Regions.DEFAULT_REGION.getName());
            AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                    //客户端设置
                    .withClientConfiguration(clientConfiguration)
                    //凭证设置
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    //endpoint设置
                    .withEndpointConfiguration(endpointConfiguration)
                    .build();
        }

        @Override
        public boolean headBucket(String bucketName) {
            return s3.doesBucketExistV2(bucketName);
        }

        @Override
        public void createBucket(String bucketName) {
            s3.createBucket(bucketName);
        }

        @Override
        public void createHeadBucket(String bucketName) {
            if (!headBucket(bucketName)) {
                createBucket(bucketName);
            }
        }

        @Override
        public NaNaObjectMetadata getObjectMetadata(String bucketName, String objectKey) {
            ObjectMetadata objectMetadata = s3.getObjectMetadata(bucketName, objectKey);
            return NaNaObjectMetadata.builder()
                    .bucketName(bucketName)
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
            PutObjectResult putObjectResult = s3.putObject(bucketName, objectKey, inputStream, null);
            return NaNaPutObjectResult.builder()
                    .bucketName(bucketName)
                    .objectKey(objectKey)
                    .statusCode(200)
                    .build();
        }

        @Override
        public void downloadFile(String bucketName, String objectKey, File file) {
            try {
                GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, objectKey);
                S3Object o = s3.getObject(getObjectRequest);
                S3ObjectInputStream s3is = o.getObjectContent();
                FileOutputStream fos = new FileOutputStream(new File(objectKey));
                byte[] read_buf = new byte[1024];
                int read_len = 0;
                while ((read_len = s3is.read(read_buf)) > 0) {
                    fos.write(read_buf, 0, read_len);
                }
                s3is.close();
                fos.close();
            } catch (AmazonServiceException e) {
                System.err.println(e.getErrorMessage());
            } catch (FileNotFoundException e) {
                System.err.println(e.getMessage());
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }

        @Override
        public void downloadBigFile(String bucketName, String objectKey, File file) {

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
            CopyObjectResult copyObjectResult = s3.copyObject(bucketName, objectKey, destBucketName, destObjectName);
            return NaNaCopyObjectResult.builder()
                    .lastModified(copyObjectResult.getLastModifiedDate()).build();
        }

        @Override
        public String getSignedUrl(String bucketName, String objectKey) {
            try {
                GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, objectKey)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(DateUtil.offsetHour(DateUtil.date(), 2));
                URL url = s3.generatePresignedUrl(generatePresignedUrlRequest);
                return url.getPath();
            } catch (AmazonServiceException e) {
                System.err.println(e.getErrorMessage());
            }
            return null;
        }

        @Override
        public void deleteObjectKey(String bucketName, String objectKey) {
            s3.deleteObject(bucketName, objectKey);
        }

        @Override
        public String getSignedUrl(String bucketName, String objectKey, int width, int height, boolean isRotate, int rotateAngle) {
            return getSignedUrl(bucketName, objectKey);
        }
    }
}
