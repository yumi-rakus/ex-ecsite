package com.example.domain;

import lombok.Data;

/**
 * 注文トッピング情報を表すドメイン.
 * 
 * @author yumi takahashi
 *
 */
@Data
public class OrderTopping {

	/** 注文トッピングID */
	private Integer id;

	/** トッピングID */
	private Integer toppingId;

	/** 注文商品ID */
	private Integer orderItemId;

	/** トッピング情報 */
	private Topping topping;
}
