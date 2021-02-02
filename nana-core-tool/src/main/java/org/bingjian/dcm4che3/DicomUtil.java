package org.bingjian.dcm4che3;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.imageio.codec.Transcoder;
import org.dcm4che3.io.DicomInputStream;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * dicom工具类
 *
 * @author fanglong
 */
@Slf4j
@Component
public class DicomUtil {

    private static final String DICOM = "DICM";

    /**
     * 是否DICOM文件
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static boolean isDICOM(InputStream inputStream) throws IOException {
        byte[] bytes = new byte[132];
        byte[] dicomMagicNum = new byte[4];
        IOUtils.read(inputStream, bytes, 0, 132);
        for (int i = 128; i < bytes.length; i++) {
            dicomMagicNum[i - 128] = bytes[i];
        }
        String hexStr = IoUtil.readUtf8(new ByteArrayInputStream(dicomMagicNum));
        return hexStr.equals(DICOM);
    }

    /**
     * 是否DICOM文件
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static boolean isDICOM(File file) throws IOException {
        return isDICOM(FileUtil.getInputStream(file));
    }

    /**
     * 文件流读取
     *
     * @param inputStream
     * @return
     */
    public static DICOM DicomRader(InputStream inputStream) {
        Map<String, Object> map = new HashMap<>();
        try (DicomInputStream dis = new DicomInputStream(inputStream)) {
            dis.setIncludeBulkData(DicomInputStream.IncludeBulkData.URI);
            Attributes attr = null;
            //属性对象
            attr = dis.readDataset(-1, -1);
            //attr.setString(Tag.SpecificCharacterSet, VR.CS, "GB18030");//解决中文乱码问题
            String patientId = attr.getString(Tag.PatientID);
            String patientName = getString(attr, Tag.PatientName);
            String accessionNumber = attr.getString(Tag.AccessionNumber);
            String patientAge = attr.getString(Tag.PatientAge);
            String patientSex = attr.getString(Tag.PatientSex);
            Date patientBirthDate = attr.getDate(Tag.PatientBirthDate);
            String modality = attr.getString(Tag.Modality);
            String institutionName = attr.getString(Tag.InstitutionName);
            String studyInstanceUID = attr.getString(Tag.StudyInstanceUID);
            String seriesInstanceUID = attr.getString(Tag.SeriesInstanceUID);
            String sopInstanceUID = attr.getString(Tag.SOPInstanceUID);
            Date studyDateTime = attr.getDate(Tag.StudyDateAndTime);
            int seriesNumber = attr.getInt(Tag.SeriesNumber, 0);
            int instanceNumber = attr.getInt(Tag.InstanceNumber, 0);
            String lossyImageCompression = attr.getString(Tag.LossyImageCompression);

            DICOM dicom = new DICOM();
            dicom.setPatientBirthday(patientBirthDate);
            dicom.setLossyImageCompression(lossyImageCompression);
            dicom.setPatientId(patientId);
            dicom.setAccessionNumber(accessionNumber);
            dicom.setPatientName(patientName);
            dicom.setPatientAge(patientAge);
            dicom.setPatientSex(patientSex);
            dicom.setStudyDatetime(studyDateTime);
            dicom.setModality(modality);
            dicom.setStudyInstanceUID(studyInstanceUID);
            dicom.setInstitutionName(institutionName);
            dicom.setInstanceNumber(instanceNumber);
            dicom.setSeriesNumber(seriesNumber);
            dicom.setSeriesInstanceUID(seriesInstanceUID);
            dicom.setSopInstanceUID(sopInstanceUID);
            if (StrUtil.isBlank(patientId) || StrUtil.isBlank(studyInstanceUID) ||
                    StrUtil.isBlank(seriesInstanceUID) || StrUtil.isBlank(sopInstanceUID)) {
                return null;
            }
            return dicom;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 文件读取
     *
     * @param file
     * @return
     */
    public static DICOM DicomRader(File file) {
        return DicomRader(FileUtil.getInputStream(file));
    }

    /**
     * 中文乱码读取
     *
     * @param attr
     * @param tag
     * @return
     */
    private static String getString(Attributes attr, int tag) throws IOException {
        String specificCharacterSet = attr.getString(Tag.SpecificCharacterSet);
        if (specificCharacterSet == null) {
            return null;
        }
        // 是否支持中文
        if (specificCharacterSet.equals("GB18030") || specificCharacterSet.equals("ISO_IR 192")) {
            return attr.getString(tag);
        } else {
            attr.setSpecificCharacterSet("ISO-8859-1");
            byte[] bytes = attr.getBytes(tag);
            return Base64.decodeStrGbk(Base64.encode(bytes));
        }
    }

    /**
     * 无损压缩
     *
     * @param src
     * @param dest
     * @throws IOException
     */
    public static void transcodeWithTranscoder(File src, final File dest) throws IOException {
        transcodeWithTranscoder(FileUtil.getInputStream(src), dest);
    }

    /**
     * 无损压缩
     *
     * @param fileUrl
     * @param dest
     * @throws IOException
     */
    public static void transcodeWithTranscoder(String fileUrl, final File dest) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(HttpUtil.downloadBytes(fileUrl));
        transcodeWithTranscoder(inputStream, dest);
    }

    /**
     * 无损压缩
     *
     * @param inputStream
     * @param dest
     * @throws IOException
     */
    public static void transcodeWithTranscoder(InputStream inputStream, final File dest) throws IOException {
        try (Transcoder transcoder = new Transcoder(inputStream)) {
            transcoder.setDestinationTransferSyntax(UID.JPEG2000Lossless);
            transcoder.setIncludeFileMetaInformation(true);
            transcoder.transcode(new Transcoder.Handler() {
                @Override
                public OutputStream newOutputStream(Transcoder transcoder, Attributes dataset) throws IOException {
                    return new FileOutputStream(dest);
                }
            });
        } catch (Exception e) {
            Files.deleteIfExists(dest.toPath());
            throw e;
        }
    }
}
