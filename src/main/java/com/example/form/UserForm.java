package com.example.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * ユーザ登録の際に使用するフォーム.
 * 
 * @author yumi takahashi
 *
 */
@Data
public class UserForm {

	/** ユーザ名 */
	@NotBlank(message = "名前を入力してください")
	@Size(min = 0, max = 50, message = "名前は50文字以内で入力してください")
	private String name;

	/** メールアドレス */
	@NotBlank(message = "メールアドレスを入力してください")
	@Email(message = "メールアドレスの形式が不正です")
	private String email;

	/** パスワード */
	@Size(min = 8, max = 16, message = "パスワードは8文字以上16文字以内で入力してください")
	private String password;

	/** 確認用パスワード */
	private String confirmPassword;

	/** 郵便番号 (上) */
	@Pattern(regexp = "[0-9]{3}", message = "郵便番号上：3桁で入力してください")
	private String zipcodeFirst;

	/** 郵便番号 (上) */
	@Pattern(regexp = "[0-9]{4}", message = "郵便番号下：4桁で入力してください")
	private String zipcodeLast;

	/** 住所 */
	@NotBlank(message = "住所を入力してください")
	private String address;

	/** 電話番号 */
	@Pattern(regexp = "\\A0(\\d{1}[-(]?\\d{4}|\\d{2}[-(]?\\d{3}|\\d{3}[-(]?\\d{2}|\\d{4}[-(]?\\d{1})[-)]?\\d{4}\\z|\\A0[5789]0[-]?\\d{4}[-]?\\d{4}\\z", message = "電話番号はXXXX-XXXX-XXXXの形式で入力してください")
	private String telephone;
}
