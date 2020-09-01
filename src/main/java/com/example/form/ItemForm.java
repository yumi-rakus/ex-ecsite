package com.example.form;

import java.util.List;

import lombok.Data;

/**
 * 商品をカートに追加する際に使用するフォーム.
 * 
 * @author yumi takahashi
 *
 */
@Data
public class ItemForm {

	/** 商品ID */
	private Integer itemId;

	/** サイズ */
	private String size;

	/** 数量 */
	private Integer quantity;

	/** トッピングIDリスト */
	private List<Integer> toppingIdList;
}
