package com.example.service;

import org.springframework.transaction.annotation.Transactional;

import com.example.domain.Order;
import com.example.domain.OrderItem;
import com.example.domain.OrderTopping;
import com.example.form.ItemForm;
import com.example.repository.OrderItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 注文商品情報を操作するサービス.
 * 
 * @author yumi takahashi
 *
 */
@Service
@Transactional
public class OrderItemService {

	@Autowired
	private OrderItemRepository orderItemRepository;
	@Autowired
	private OrderToppingService orderToppingService;
	@Autowired
	private OrderService orderService;

	/**
	 * ショッピングカートに商品を追加する.
	 * 
	 * @param form   フォーム
	 * @param userId ユーザID
	 */
	public void cartIn(ItemForm form, Integer userId) {

		Integer orderId;

		// 注文IDを取得する処理
		if (orderService.existsByStatus0ByUserId(userId)) {
			orderId = orderService.getOrderIdByUserIdByStatus0(userId);
		} else {
			Order order = new Order();
			order.setUserId(userId);
			order.setStatus(0);
			order.setTotalPrice(0);

			order = orderService.insertOrder(order);
			orderId = order.getId();
		}

		// 注文商品情報をinsert
		OrderItem orderItem = new OrderItem();
		orderItem.setItemId(form.getItemId());
		orderItem.setOrderId(orderId);
		orderItem.setQuantity(form.getQuantity());
		orderItem.setSize(form.getSize().charAt(0));
		orderItem = orderItemRepository.insertOrderItem(orderItem);

		// 注文トッピング情報をinsert
		for (Integer toppingId : form.getToppingIdList()) {

			OrderTopping orderTopping = new OrderTopping();
			orderTopping.setToppingId(toppingId);
			orderTopping.setOrderItemId(orderItem.getId());
			orderToppingService.insertOrderTopping(orderTopping);
		}

		// 合計金額を更新
		orderService.updateTotalPrice(userId);
	}

	/**
	 * 注文商品をショッピングカートから削除する.
	 * 
	 * @param orderItemId
	 * @param userId
	 */
	public void deleteOrderItem(Integer orderItemId, Integer userId) {

		orderItemRepository.deleteByOrderItemId(orderItemId);
		orderService.updateTotalPrice(userId);
	}

}
