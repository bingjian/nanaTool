//import cn.hutool.core.io.FileUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.bingjian.ImageCloudApplication;
//import org.bingjian.mapper.OssFileDTOMapper;
//import org.bingjian.model.NaNaObjectMetadata;
//import org.bingjian.model.NaNaPutObjectResult;
//import org.bingjian.model.OssFile;
//import org.bingjian.strategy.OssStrategy;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import javax.annotation.Resource;
//import java.io.File;
//
//@RunWith(SpringRunner.class)
////webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
////@ContextConfiguration(classes = {
////        OssProperties.class
////})
//@Slf4j
//@SpringBootTest(classes = ImageCloudApplication.class)
//public class OSSTest {
//
////    @Resource
////    private StringEncryptor encryptor;
//
//    @Resource
//    private OssStrategy ossStrategy;
//
////    @Test
////    public void getPass() {
////        String url = encryptor.encrypt("jdbc:mysql://localhost:3306/mydb?autoReconnect=true&serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf-8");
////        String name = encryptor.encrypt("root");
////        String password = encryptor.encrypt("123456");
////        System.out.println("database url: " + url);
////        System.out.println("database name: " + name);
////        System.out.println("database password: " + password);
////        Assert.assertTrue(url.length() > 0);
////        Assert.assertTrue(name.length() > 0);
////        Assert.assertTrue(password.length() > 0);
////        String testUtl = fileStrategy.getSignedUrl("", "", 100, 100, true, 90);
////        System.out.println(HttpUtil.downloadFile(testUtl, new File("D:/123456.jpg")));
////    }
//
//    @Test
//    public void uploadFile() {
//        String bucketName = "zky-test-2020";
//        String objectName = "aaa.dcm";
//        File file = FileUtil.file("D:\\I000001.dcm");
//        ossStrategy.createHeadBucket(bucketName);
//        NaNaPutObjectResult putObjectResult = ossStrategy.uploadFile(bucketName, objectName, file);
//        NaNaObjectMetadata naNaObjectMetadata = ossStrategy.getObjectMetadata(putObjectResult.getBucketName(), putObjectResult.getObjectKey());
//        System.out.println(naNaObjectMetadata.getStorageClass());
//        OssFile ossFile = OssFileDTOMapper.INSTANCE.naNaObjectMetadataResultToOssFile(naNaObjectMetadata);
//        System.out.println(ossFile);
//    }
//}