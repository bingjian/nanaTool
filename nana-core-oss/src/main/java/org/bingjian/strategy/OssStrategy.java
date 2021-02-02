package org.bingjian.strategy;


import org.bingjian.model.NaNaCopyObjectResult;
import org.bingjian.model.NaNaObjectMetadata;
import org.bingjian.model.NaNaPutObjectResult;

import java.io.File;
import java.io.InputStream;

/**
 * 对象存储策略接口
 *
 * @author fanglong
 */
public interface OssStrategy {

    /**
     * 判断桶是否存在
     *
     * @param bucketName 桶名称
     * @return boolean
     */
    boolean headBucket(String bucketName);

    /**
     * 创建桶
     *
     * @param bucketName 桶名称
     */
    void createBucket(String bucketName);

    /**
     * 创建桶,如果桶不存在自动创建
     *
     * @param bucketName 桶名称
     */
    void createHeadBucket(String bucketName);

    /**
     * 获取对象属性
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     * @return NaNaPutObjectResult
     */
    NaNaObjectMetadata getObjectMetadata(String bucketName, String objectName);

    /**
     * 默认对象存储桶文件上传
     *
     * @param objectName 对象名称
     * @param file       上传文件
     * @return NaNaPutObjectResult
     */
    NaNaPutObjectResult uploadFile(String objectName, File file);

    /**
     * 默认对象存储桶字节流上传
     *
     * @param objectName 对象名称
     * @param bytes      字节流
     * @return NaNaPutObjectResult
     */
    NaNaPutObjectResult uploadFile(String objectName, byte[] bytes);

    /**
     * 文件流上传
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     * @param bytes      上传字节流
     * @return NaNaPutObjectResult
     */
    NaNaPutObjectResult uploadFile(String bucketName, String objectName, byte[] bytes);

    /**
     * 文件上传
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     * @param file       上传文件
     * @return NaNaPutObjectResult
     */
    NaNaPutObjectResult uploadFile(String bucketName, String objectName, File file);

    /**
     * 文件流上传
     *
     * @param bucketName  桶名称
     * @param objectName  对象名称
     * @param inputStream 上传文件流
     * @return NaNaPutObjectResult
     */
    NaNaPutObjectResult uploadFile(String bucketName, String objectName, InputStream inputStream);

    /**
     * 上传网络文件
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     * @param fileUrl    网络文件地址
     * @return NaNaPutObjectResult
     */
    NaNaPutObjectResult uploadFile(String bucketName, String objectName, String fileUrl);

    /**
     * 下载文件
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     * @param file       下载文件
     */
    void downloadFile(String bucketName, String objectName, File file);

    /**
     * 下载大文件
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     * @param file       下载文件
     */
    void downloadBigFile(String bucketName, String objectName, File file);

    /**
     * 移动对象
     *
     * @param bucketName     源桶名称
     * @param objectName     源对象名称
     * @param destBucketName 目标桶名称
     * @param destObjectName 目标对象名称
     */
    void moveObjectKey(String bucketName, String objectName, String destBucketName, String destObjectName);


    /**
     * 复制对象
     *
     * @param bucketName     源桶名称
     * @param objectName     源对象名称
     * @param destBucketName 目标桶名称
     * @param destObjectName 目标对象名称
     * @return NaNaCopyObjectResult
     */
    NaNaCopyObjectResult copyObjectKey(String bucketName, String objectName, String destBucketName, String destObjectName);


    /**
     * 获取默认2小时的授权链接
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     * @return 授权链接地址
     */
    String getSignedUrl(String bucketName, String objectName);

    /**
     * 删除对象
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     */
    void deleteObjectKey(String bucketName, String objectName);

    /**
     * 授权方式实现图片处理，生成临时授权URL
     *
     * @param bucketName  桶名称
     * @param objectName  对象名称
     * @param width       固定宽度
     * @param height      固定高度
     * @param isRotate    是否旋转
     * @param rotateAngle 旋转角度
     * @return 授权链接地址
     */
    String getSignedUrl(String bucketName, String objectName, int width, int height, boolean isRotate, int rotateAngle);
}
