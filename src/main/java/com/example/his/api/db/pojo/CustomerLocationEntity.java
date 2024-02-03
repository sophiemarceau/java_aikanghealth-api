package com.example.his.api.db.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * 客户位置表
 * @TableName tb_customer_location
 */
@Data
public class CustomerLocationEntity implements Serializable {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 客户ID
     */
    private Integer customerId;

    /**
     * 蓝牙信标UUID
     */
    private String blueUuid;

    /**
     * 科室ID
     */
    private Integer placeId;

    /**
     * 日期时间
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
        CustomerLocationEntity other = (CustomerLocationEntity) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getCustomerId() == null ? other.getCustomerId() == null : this.getCustomerId().equals(other.getCustomerId()))
            && (this.getBlueUuid() == null ? other.getBlueUuid() == null : this.getBlueUuid().equals(other.getBlueUuid()))
            && (this.getPlaceId() == null ? other.getPlaceId() == null : this.getPlaceId().equals(other.getPlaceId()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getCustomerId() == null) ? 0 : getCustomerId().hashCode());
        result = prime * result + ((getBlueUuid() == null) ? 0 : getBlueUuid().hashCode());
        result = prime * result + ((getPlaceId() == null) ? 0 : getPlaceId().hashCode());
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
        sb.append(", customerId=").append(customerId);
        sb.append(", blueUuid=").append(blueUuid);
        sb.append(", placeId=").append(placeId);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}