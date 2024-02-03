package com.example.his.api.db.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 体检预约表
 * @TableName tb_appointment
 */
@Data
public class AppointmentEntity implements Serializable {
    /**
     * 主键
     */
    private Integer id;

    /**
     * UUID
     */
    private String uuid;

    /**
     * 订单编号
     */
    private Integer orderId;

    /**
     * 预约日期
     */
    private String date;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别
     */
    private String sex;

    /**
     * 身份证号
     */
    private String pid;

    /**
     * 出生日期
     */
    private String birthday;

    /**
     * 电话号码
     */
    private String tel;

    /**
     * 体检报告邮寄地址
     */
    private String mailingAddress;

    /**
     * 企业名称
     */
    private String company;

    /**
     * 状态。1未签到，2已签到，3已结束，4已关闭
     */
    private Integer status;

    /**
     * 签到时间
     */
    private Date checkinTime;

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
        AppointmentEntity other = (AppointmentEntity) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUuid() == null ? other.getUuid() == null : this.getUuid().equals(other.getUuid()))
            && (this.getOrderId() == null ? other.getOrderId() == null : this.getOrderId().equals(other.getOrderId()))
            && (this.getDate() == null ? other.getDate() == null : this.getDate().equals(other.getDate()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getSex() == null ? other.getSex() == null : this.getSex().equals(other.getSex()))
            && (this.getPid() == null ? other.getPid() == null : this.getPid().equals(other.getPid()))
            && (this.getBirthday() == null ? other.getBirthday() == null : this.getBirthday().equals(other.getBirthday()))
            && (this.getTel() == null ? other.getTel() == null : this.getTel().equals(other.getTel()))
            && (this.getMailingAddress() == null ? other.getMailingAddress() == null : this.getMailingAddress().equals(other.getMailingAddress()))
            && (this.getCompany() == null ? other.getCompany() == null : this.getCompany().equals(other.getCompany()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getCheckinTime() == null ? other.getCheckinTime() == null : this.getCheckinTime().equals(other.getCheckinTime()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUuid() == null) ? 0 : getUuid().hashCode());
        result = prime * result + ((getOrderId() == null) ? 0 : getOrderId().hashCode());
        result = prime * result + ((getDate() == null) ? 0 : getDate().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getSex() == null) ? 0 : getSex().hashCode());
        result = prime * result + ((getPid() == null) ? 0 : getPid().hashCode());
        result = prime * result + ((getBirthday() == null) ? 0 : getBirthday().hashCode());
        result = prime * result + ((getTel() == null) ? 0 : getTel().hashCode());
        result = prime * result + ((getMailingAddress() == null) ? 0 : getMailingAddress().hashCode());
        result = prime * result + ((getCompany() == null) ? 0 : getCompany().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getCheckinTime() == null) ? 0 : getCheckinTime().hashCode());
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
        sb.append(", uuid=").append(uuid);
        sb.append(", orderId=").append(orderId);
        sb.append(", date=").append(date);
        sb.append(", name=").append(name);
        sb.append(", sex=").append(sex);
        sb.append(", pid=").append(pid);
        sb.append(", birthday=").append(birthday);
        sb.append(", tel=").append(tel);
        sb.append(", mailingAddress=").append(mailingAddress);
        sb.append(", company=").append(company);
        sb.append(", status=").append(status);
        sb.append(", checkinTime=").append(checkinTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}