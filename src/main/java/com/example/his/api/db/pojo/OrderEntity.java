package com.example.his.api.db.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

/**
 * 订单表
 * @TableName tb_order
 */
@Data
public class OrderEntity implements Serializable {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 客户ID
     */
    private Integer customerId;

    /**
     * 商品ID
     */
    private Integer goodsId;

    /**
     * 商品快照ID
     */
    private String snapshotId;

    /**
     * 商品标题
     */
    private String goodsTitle;

    /**
     * 商品价格
     */
    private BigDecimal goodsPrice;

    /**
     * 购买数量
     */
    private Integer number;

    /**
     * 付款金额
     */
    private BigDecimal amount;

    /**
     * 商品封面
     */
    private String goodsImage;

    /**
     * 商品描述
     */
    private String goodsDescription;

    /**
     * 订单流水号
     */
    private String outTradeNo;

    /**
     * 付款单ID
     */
    private String transactionId;

    /**
     * 退款单流水号
     */
    private String outRefundNo;

    /**
     * 订单状态。1未付款，2已关闭，3已付款，4已退款，5已预约，6已结束
     */
    private Integer status;

    /**
     * 下单日期
     */
    private String createDate;

    /**
     * 下单日期时间
     */
    private String createTime;

    /**
     * 退款日期
     */
    private String refundDate;

    /**
     * 退款日期时间
     */
    private String refundTime;

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
        OrderEntity other = (OrderEntity) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getCustomerId() == null ? other.getCustomerId() == null : this.getCustomerId().equals(other.getCustomerId()))
            && (this.getGoodsId() == null ? other.getGoodsId() == null : this.getGoodsId().equals(other.getGoodsId()))
            && (this.getSnapshotId() == null ? other.getSnapshotId() == null : this.getSnapshotId().equals(other.getSnapshotId()))
            && (this.getGoodsTitle() == null ? other.getGoodsTitle() == null : this.getGoodsTitle().equals(other.getGoodsTitle()))
            && (this.getGoodsPrice() == null ? other.getGoodsPrice() == null : this.getGoodsPrice().equals(other.getGoodsPrice()))
            && (this.getNumber() == null ? other.getNumber() == null : this.getNumber().equals(other.getNumber()))
            && (this.getAmount() == null ? other.getAmount() == null : this.getAmount().equals(other.getAmount()))
            && (this.getGoodsImage() == null ? other.getGoodsImage() == null : this.getGoodsImage().equals(other.getGoodsImage()))
            && (this.getGoodsDescription() == null ? other.getGoodsDescription() == null : this.getGoodsDescription().equals(other.getGoodsDescription()))
            && (this.getOutTradeNo() == null ? other.getOutTradeNo() == null : this.getOutTradeNo().equals(other.getOutTradeNo()))
            && (this.getTransactionId() == null ? other.getTransactionId() == null : this.getTransactionId().equals(other.getTransactionId()))
            && (this.getOutRefundNo() == null ? other.getOutRefundNo() == null : this.getOutRefundNo().equals(other.getOutRefundNo()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getCreateDate() == null ? other.getCreateDate() == null : this.getCreateDate().equals(other.getCreateDate()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getRefundDate() == null ? other.getRefundDate() == null : this.getRefundDate().equals(other.getRefundDate()))
            && (this.getRefundTime() == null ? other.getRefundTime() == null : this.getRefundTime().equals(other.getRefundTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getCustomerId() == null) ? 0 : getCustomerId().hashCode());
        result = prime * result + ((getGoodsId() == null) ? 0 : getGoodsId().hashCode());
        result = prime * result + ((getSnapshotId() == null) ? 0 : getSnapshotId().hashCode());
        result = prime * result + ((getGoodsTitle() == null) ? 0 : getGoodsTitle().hashCode());
        result = prime * result + ((getGoodsPrice() == null) ? 0 : getGoodsPrice().hashCode());
        result = prime * result + ((getNumber() == null) ? 0 : getNumber().hashCode());
        result = prime * result + ((getAmount() == null) ? 0 : getAmount().hashCode());
        result = prime * result + ((getGoodsImage() == null) ? 0 : getGoodsImage().hashCode());
        result = prime * result + ((getGoodsDescription() == null) ? 0 : getGoodsDescription().hashCode());
        result = prime * result + ((getOutTradeNo() == null) ? 0 : getOutTradeNo().hashCode());
        result = prime * result + ((getTransactionId() == null) ? 0 : getTransactionId().hashCode());
        result = prime * result + ((getOutRefundNo() == null) ? 0 : getOutRefundNo().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getCreateDate() == null) ? 0 : getCreateDate().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getRefundDate() == null) ? 0 : getRefundDate().hashCode());
        result = prime * result + ((getRefundTime() == null) ? 0 : getRefundTime().hashCode());
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
        sb.append(", goodsId=").append(goodsId);
        sb.append(", snapshotId=").append(snapshotId);
        sb.append(", goodsTitle=").append(goodsTitle);
        sb.append(", goodsPrice=").append(goodsPrice);
        sb.append(", number=").append(number);
        sb.append(", amount=").append(amount);
        sb.append(", goodsImage=").append(goodsImage);
        sb.append(", goodsDescription=").append(goodsDescription);
        sb.append(", outTradeNo=").append(outTradeNo);
        sb.append(", transactionId=").append(transactionId);
        sb.append(", outRefundNo=").append(outRefundNo);
        sb.append(", status=").append(status);
        sb.append(", createDate=").append(createDate);
        sb.append(", createTime=").append(createTime);
        sb.append(", refundDate=").append(refundDate);
        sb.append(", refundTime=").append(refundTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}