package com.example.domain;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * 注文情報を表すドメイン.
 * 
 * @author yumi takahashi
 *
 */
@Data
public class Order {

	/** 注文ID */
	private Integer id;

	/** ユーザID */
	private Integer userId;

	/** 状態 */
	private Integer status;

	/** 合計金額 */
	private Integer totalPrice;

	/** 注文日 */
	private Date orderDate;

	/** 宛先氏名 */
	private String destinationName;

	/** 宛先Eメール */
	private String destinationEmail;

	/** 宛先郵便番号 */
	private String destinationZipcode;

	/** 宛先住所 */
	private String destinationAddress;

	/** 宛先TEL */
	private String destinationTel;

	/** 配達時間 */
	private Timestamp deliveryTime;

	/** 支払方法 */
	private Integer paymentMethod;

	/** ユーザ情報 */
	private User user;

	/** 注文商品リスト */
	private List<OrderItem> orderItemList;

	// method
	public int getTax() {
		return (int) (this.totalPrice * 0.10);
	}

	public int getCalcTotalPrice() {

		int totalPrice = 0;

		for (OrderItem orderItem : this.orderItemList) {
			totalPrice += orderItem.getSubTotal();
		}

		return totalPrice;
	}
}
