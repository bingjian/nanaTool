package org.bingjian.mapper;

import org.bingjian.model.NaNaObjectMetadata;
import org.bingjian.model.OssFile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 *
 * @author fanglong
 */
@Mapper
public interface OssFileDTOMapper {

    OssFileDTOMapper INSTANCE = Mappers.getMapper(OssFileDTOMapper.class);

//    @Mappings({
//            @Mapping(target = "hospitalId", source = "hospitalId"),
//            @Mapping(source = "dicom.studyInstanceUID", target = "studyInstanceUID"),
//            @Mapping(source = "dicom.modality", target = "modality"),
//            @Mapping(source = "ossFile.bucketName", target = "bucketName")
//    })
//    OssFile dicomToOssFile(ObjectMetadata objectMetadata);

    @Mappings({
            @Mapping(source = "contentLength", target = "size"),
            @Mapping(source = "objectName", target = "objectName"),
            @Mapping(source = "bucketName", target = "bucketName"),
            @Mapping(source = "lastModified", target = "lastModifiedDate"),
            @Mapping(source = "contentType", target = "contentType"),
            @Mapping(source = "storageClass", target = "storageClassEnum")
    })
    OssFile naNaObjectMetadataResultToOssFile(NaNaObjectMetadata naNaObjectMetadata);
}
