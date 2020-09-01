package com.example.domain;

import java.util.List;
import java.util.Objects;

import lombok.Data;

/**
 * 注文商品を表すドメイン.
 * 
 * @author yumi takahashi
 *
 */
@Data
public class OrderItem {

	/** 注文商品ID */
	private Integer id;

	/** 商品ID */
	private Integer itemId;

	/** 注文ID */
	private Integer orderId;

	/** 数量 */
	private Integer quantity;

	/** サイズ */
	private Character size;

	/** 商品情報 */
	private Item item;

	/** 注文トッピングリスト */
	private List<OrderTopping> orderToppingList;

	// method
	public int getSubTotal() {

		// 商品価格
		int itemPrice = 0;

		if (("M").equals(String.valueOf(this.size))) {
			itemPrice = this.item.getPriceM();
		} else if (("L").equals(String.valueOf(this.size))) {
			itemPrice = this.item.getPriceL();
		}

		// トッピング価格
		int toppingPrice = 0;

		if (Objects.nonNull(this.orderToppingList)) {

			for (OrderTopping orderTopping : this.orderToppingList) {

				if (("M").equals(String.valueOf(this.size))) {
					toppingPrice += orderTopping.getTopping().getPriceM();
				} else if (("L").equals(String.valueOf(this.size))) {
					toppingPrice += orderTopping.getTopping().getPriceL();
				}
			}
		}

		// 小計
		int subTotal = (itemPrice + toppingPrice) * this.quantity;

		return subTotal;
	}
}
