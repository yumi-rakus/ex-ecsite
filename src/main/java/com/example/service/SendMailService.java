package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.domain.Order;
import com.example.domain.OrderItem;
import com.example.domain.OrderTopping;

/**
 * 注文完了時にユーザにメールを送信するサービス.
 * 
 * @author yumi takahashi
 *
 */
@Service
public class SendMailService {

	@Autowired
	private MailSender mailSender;

	@Async
	public void sendMail(Integer userId, Order userOrder) {

		List<OrderItem> orderItemList = userOrder.getOrderItemList();

		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom("ym.ouso414@gmail.com");
		msg.setTo(userOrder.getDestinationEmail());
		msg.setSubject("注文完了メール");

		StringBuilder content = new StringBuilder();
		content.append(userOrder.getDestinationName() + "様\n");
		content.append("\n");
		content.append("ラクラクカリーをご利用いただき、ありがとうございます。\n");
		content.append("ご注文を承りました。\n");
		content.append("\n");
		content.append("■ご注文内容■\n");

		for (OrderItem orderItem : orderItemList) {

			content.append("\n");
			content.append("商品 : " + orderItem.getItem().getName() + "\n");
			content.append("サイズ : " + orderItem.getSize() + "\n");
			content.append("数量 : " + orderItem.getQuantity() + "\n");
			content.append("トッピング : ");

			if (!orderItem.getOrderToppingList().isEmpty()) {

				for (OrderTopping orderTopping : orderItem.getOrderToppingList()) {
					content.append(orderTopping.getTopping().getName() + " ");
				}
			} else {
				content.append("---");
			}
			content.append("\n");
			content.append("[小計]　" + String.format("%,d", orderItem.getSubTotal()) + "円\n");
			content.append("\n\n");
		}

		content.append("\n");
		content.append("〖商品代金〗" + String.format("%,d", userOrder.getTotalPrice()) + "円\n");
		content.append("〖消費税〗" + String.format("%,d", userOrder.getTax()) + "円\n");
		Integer totalPriceInTax = userOrder.getTotalPrice() + userOrder.getTax();
		content.append("【合計金額】 " + String.format("%,d", totalPriceInTax) + "円\n");

		content.append("\n");

		if (userOrder.getStatus() == 1) {
			content.append("お支払い方法: 代金引換\n配達時にお支払いください。\n");
		} else if (userOrder.getStatus() == 2) {
			content.append("お支払い方法: クレジットカード\n決済済みです。\n");
		}

		content.append("\n");
		content.append("お届け先住所 : \n");
		content.append("〒" + userOrder.getDestinationZipcode() + "\n");
		content.append(userOrder.getDestinationAddress() + "\n");

		content.append("\n");
		content.append("またのご利用をお待ちしております。");

		msg.setText(content.toString());

		this.mailSender.send(msg);
	}
}
