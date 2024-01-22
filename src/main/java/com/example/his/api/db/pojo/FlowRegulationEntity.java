package com.example.his.api.db.pojo;

import java.io.Serializable;
import lombok.Data;

/**
 * 人员调流表
 * @TableName tb_flow_regulation
 */
@Data
public class FlowRegulationEntity implements Serializable {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 科室名称
     */
    private String place;

    /**
     * 排队人数
     */
    private Integer realNum;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 权重（自动调流使用）
     */
    private Integer weight;

    /**
     * 优先级（手动调流使用）
     */
    private Integer priority;

    /**
     * 蓝牙信标ID
     */
    private String blueUuid;

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
        FlowRegulationEntity other = (FlowRegulationEntity) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getPlace() == null ? other.getPlace() == null : this.getPlace().equals(other.getPlace()))
            && (this.getRealNum() == null ? other.getRealNum() == null : this.getRealNum().equals(other.getRealNum()))
            && (this.getMaxNum() == null ? other.getMaxNum() == null : this.getMaxNum().equals(other.getMaxNum()))
            && (this.getWeight() == null ? other.getWeight() == null : this.getWeight().equals(other.getWeight()))
            && (this.getPriority() == null ? other.getPriority() == null : this.getPriority().equals(other.getPriority()))
            && (this.getBlueUuid() == null ? other.getBlueUuid() == null : this.getBlueUuid().equals(other.getBlueUuid()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getPlace() == null) ? 0 : getPlace().hashCode());
        result = prime * result + ((getRealNum() == null) ? 0 : getRealNum().hashCode());
        result = prime * result + ((getMaxNum() == null) ? 0 : getMaxNum().hashCode());
        result = prime * result + ((getWeight() == null) ? 0 : getWeight().hashCode());
        result = prime * result + ((getPriority() == null) ? 0 : getPriority().hashCode());
        result = prime * result + ((getBlueUuid() == null) ? 0 : getBlueUuid().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", place=").append(place);
        sb.append(", realNum=").append(realNum);
        sb.append(", maxNum=").append(maxNum);
        sb.append(", weight=").append(weight);
        sb.append(", priority=").append(priority);
        sb.append(", blueUuid=").append(blueUuid);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}