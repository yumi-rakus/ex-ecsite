package com.example.domain;

import lombok.Data;

/**
 * ユーザ情報を表すドメイン.
 * 
 * @author yumi takahashi
 *
 */
@Data
public class User {

	/** ユーザID */
	private Integer id;

	/** ユーザ名 */
	private String name;

	/** メールアドレス */
	private String email;

	/** パスワード */
	private String password;

	/** 郵便番号 */
	private String zipcode;

	/** 住所 */
	private String address;

	/** 電話番号 */
	private String telephone;
}
