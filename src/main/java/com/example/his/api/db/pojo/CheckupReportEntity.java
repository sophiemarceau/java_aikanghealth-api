package com.example.his.api.db.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * 体检报告表
 * @TableName tb_checkup_report
 */
@Data
public class CheckupReportEntity implements Serializable {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 体检预约ID
     */
    private Integer appointmentId;

    /**
     * 体检结果ID（MongoDB）
     */
    private String resultId;

    /**
     * 体检报告状态。1未生成，2已生成，3已邮寄
     */
    private Integer status;

    /**
     * 提交报告存放在Minio服务器上的URL地址
     */
    private String filePath;

    /**
     * 快递运单号
     */
    private String waybillCode;

    /**
     * 快递发出日期
     */
    private String waybillDate;

    /**
     * 体检日期
     */
    private String date;

    /**
     * 创建时间
     */
    private String createTime;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        CheckupReportEntity other = (CheckupReportEntity) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getAppointmentId() == null ? other.getAppointmentId() == null : this.getAppointmentId().equals(other.getAppointmentId()))
            && (this.getResultId() == null ? other.getResultId() == null : this.getResultId().equals(other.getResultId()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getFilePath() == null ? other.getFilePath() == null : this.getFilePath().equals(other.getFilePath()))
            && (this.getWaybillCode() == null ? other.getWaybillCode() == null : this.getWaybillCode().equals(other.getWaybillCode()))
            && (this.getWaybillDate() == null ? other.getWaybillDate() == null : this.getWaybillDate().equals(other.getWaybillDate()))
            && (this.getDate() == null ? other.getDate() == null : this.getDate().equals(other.getDate()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getAppointmentId() == null) ? 0 : getAppointmentId().hashCode());
        result = prime * result + ((getResultId() == null) ? 0 : getResultId().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getFilePath() == null) ? 0 : getFilePath().hashCode());
        result = prime * result + ((getWaybillCode() == null) ? 0 : getWaybillCode().hashCode());
        result = prime * result + ((getWaybillDate() == null) ? 0 : getWaybillDate().hashCode());
        result = prime * result + ((getDate() == null) ? 0 : getDate().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", appointmentId=").append(appointmentId);
        sb.append(", resultId=").append(resultId);
        sb.append(", status=").append(status);
        sb.append(", filePath=").append(filePath);
        sb.append(", waybillCode=").append(waybillCode);
        sb.append(", waybillDate=").append(waybillDate);
        sb.append(", date=").append(date);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}