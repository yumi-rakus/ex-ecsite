package com.example.domain;

import java.util.List;

import lombok.Data;

/**
 * 商品情報を表すドメイン.
 * 
 * @author yumi takahashi
 *
 */
@Data
public class Item {

	/** 商品ID */
	private Integer id;

	/** 商品名 */
	private String name;

	/** 商品説明 */
	private String description;

	/** Mの価格 */
	private Integer priceM;

	/** Lの価格 */
	private Integer priceL;

	/** 画像パス */
	private String imagePath;

	/** 削除フラグ */
	private Boolean deleted;

	/** トッピングリスト */
	private List<Topping> toppingList;
}
