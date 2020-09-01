package com.example.domain;

import lombok.Data;

/**
 * トッピング情報を表すドメイン.
 * 
 * @author yumi takahashi
 *
 */
@Data
public class Topping {

	/** トッピングID */
	private Integer id;

	/** トッピング名 */
	private String name;

	/** Mの価格 */
	private Integer priceM;

	/** Lの価格 */
	private Integer priceL;
}
