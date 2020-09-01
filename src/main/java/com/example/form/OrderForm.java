package com.example.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Data;

/**
 * 注文の際に使用するフォーム.
 * 
 * @author yumi takahashi
 *
 */
@Data
public class OrderForm {

	/** 名前 */
	@NotBlank(message = "名前を入力してください")
	private String name;

	/** メールアドレス */
	@NotBlank(message = "メールアドレスを入力して下さい")
	@Email(message = "メールアドレスの形式が不正です")
	private String email;

	/** 宛先郵便番号（上） */
	@Pattern(regexp = "[0-9]{3}", message = "郵便番号上：3桁で入力してください")
	private String zipcodeFirst;

	/** 宛先郵便番号（下） */
	@Pattern(regexp = "[0-9]{4}", message = "郵便番号下：4桁で入力してください")
	private String zipcodeLast;

	/** 宛先住所 */
	@NotBlank(message = "住所を入力して下さい")
	private String address;

	/** 電話番号 */
	@Pattern(regexp = "\\A0(\\d{1}[-(]?\\d{4}|\\d{2}[-(]?\\d{3}|\\d{3}[-(]?\\d{2}|\\d{4}[-(]?\\d{1})[-)]?\\d{4}\\z|\\A0[5789]0[-]?\\d{4}[-]?\\d{4}\\z", message = "電話番号はXXXX-XXXX-XXXXの形式で入力してください")
	private String telephone;

	/** 配達日時 */
	@NotBlank(message = "配達日時を入力して下さい")
	private String orderDate;

	/** 配達時間 */
	@NotBlank(message = "配達時間を選択してください")
	private String orderTime;

	/** 支払方法 */
	@NotNull(message = "支払方法を選択してください")
	private Integer paymentMethod;
}
