package com.example.form;

import lombok.Data;

/**
 * 商品検索の際に使用するフォーム.
 * 
 * @author yumi takahashi
 *
 */
@Data
public class ItemSearchForm {

	/** 検索キー */
	private String searchName;

	/** 並び替え */
	private Integer soting;

}
