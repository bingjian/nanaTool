package org.bingjian.dcm4che3;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * DICOM信息
 * @author fanglong
 */
@Data
@Accessors(chain = true)
public class DICOM {
    private static final long serialVersionUID = 7529298732281418616L;

    /**
     * 患者ID
     */
    private String patientId;

    /**
     * 检查号
     */
    private String accessionNumber;

    /**
     * 患者名称
     */
    private String patientName;

    /**
     * 患者年龄
     */
    private String patientAge;

    /**
     * 患者出生日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date patientBirthday;

    /**
     * 患者性别
     */
    private String patientSex;

    /**
     * 检查时间
     */
    private Date studyDatetime;

    /**
     * 影像类型
     */
    private String modality;

    /**
     * 检查UID唯一标识符
     */
    private String studyInstanceUID;

    /**
     * 机构名称
     */
    private String institutionName;

    /**
     * 实例号
     */
    private Integer instanceNumber;

    /**
     * 系列号
     */
    private Integer seriesNumber;

    /**
     * 系列UID唯一标识符
     */
    private String seriesInstanceUID;

    /**
     * 唯一标识SOP实例UID
     */
    private String sopInstanceUID;

    /**
     * 有损图像值
     */
    private String lossyImageCompression;

    /**
     * 判断是否有损
     * @return
     */
    public boolean IsLossyImageStatus(){
        return StrUtil.isNotBlank(lossyImageCompression) && lossyImageCompression.equals("01");
    }
}
