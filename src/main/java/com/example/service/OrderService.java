package com.example.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.Order;
import com.example.domain.OrderItem;
import com.example.domain.OrderTopping;
import com.example.repository.OrderRepository;

/**
 * 注文情報を操作するサービス.
 * 
 * @author yumi takahashi
 *
 */
@Service
@Transactional
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	/**
	 * 未注文（status=0）のレコードが存在するかどうかを返す.
	 * 
	 * @param userId ユーザID
	 * @return status = 0 (未注文)のレコードが存在していればtrue、存在していなければfalseを返す
	 */
	public boolean existsByStatus0ByUserId(Integer userId) {
		return orderRepository.existsByStatus0ByUserId(userId);
	}

	/**
	 * 未注文（status=0）の注文IDを取得する.
	 * 
	 * @param userId ユーザID
	 * @return 注文ID
	 */
	public Integer getOrderIdByUserIdByStatus0(Integer userId) {
		return orderRepository.findOrderIdByUserIdByStatus0(userId);
	}

	/**
	 * 未注文（status=0）の注文情報を挿入する.
	 * 
	 * @param order 注文情報
	 * @return 注文情報
	 */
	public Order insertOrder(Order order) {
		return orderRepository.insertOrder(order);
	}

	/**
	 * ショッピングカートリストを取得する.
	 * 
	 * @param userId ユーザID
	 * @return カートの商品一覧
	 */
	public List<Order> getOrderListByUserIdByStatus0(Integer userId) {

		List<Order> orderList = orderRepository.findByUserIdByStatus0(userId);

		// Map1 … キー：order_item_id、バリュー:注文トッピングリストのマップ
		Map<Integer, List<OrderTopping>> orderToppingMap = new LinkedHashMap<>();

		// Map2 … キー：order_item_id、バリュー:注文商品のマップ
		Map<Integer, OrderItem> orderItemMap = new LinkedHashMap<>();

		// Map3 … キー：order_id、バリュー:注文のマップ
		Map<Integer, Order> orderMap = new LinkedHashMap<>();

		// Map1を完成させる
		for (Order order : orderList) {
			orderToppingMap.put(order.getOrderItemList().get(0).getId(), new ArrayList<>());
		}

		for (Order order : orderList) {
			List<OrderTopping> orderToppingList = orderToppingMap.get(order.getOrderItemList().get(0).getId());
			OrderTopping orderTopping = order.getOrderItemList().get(0).getOrderToppingList().get(0);
			// オブジェクトが空ではない場合のみトッピングリストに追加
			if (Objects.nonNull(orderTopping.getTopping().getName())) {
				orderToppingList.add(order.getOrderItemList().get(0).getOrderToppingList().get(0));
			}
		}
		// Map1完成 （order_item_id１つに対して１つの注文トッピングリストorderToppingList(中身は0~複数)）

		// Map2を完成させる
		for (Order order : orderList) {
			orderItemMap.put(order.getOrderItemList().get(0).getId(), order.getOrderItemList().get(0));
		}

		for (Order order : orderList) {
			OrderItem orderItem = orderItemMap.get(order.getOrderItemList().get(0).getId());

			List<OrderTopping> orderToppingList = orderToppingMap.get(order.getOrderItemList().get(0).getId());
			orderItem.setOrderToppingList(orderToppingList);

			orderItemMap.put(order.getOrderItemList().get(0).getId(), orderItem);
		}
		// Map2完成 （order_item_id１つに対して１つの注文商品orderItem(先程作成したorderToppingListがセットされたもの)）

		// 注文商品リストのリストを作る（サイズは注文商品の数と一致）
		List<OrderItem> orderItemList = new ArrayList<>(orderItemMap.values());

		// Map3を完成させる
		for (Order order : orderList) {
			order.setOrderItemList(orderItemList);
			orderMap.put(order.getId(), order);
		}
		// Map3完成 （注文商品リスト(サイズは注文商品の数と一致)を注文にセット）（Map3の中身は１件になるはず）

		// サイズ１件の注文
		List<Order> distinctOrderList = new ArrayList<>(orderMap.values());

		return distinctOrderList;
	}

	/**
	 * 合計金額を更新する.
	 * 
	 * @param userId ユーザID
	 */
	public void updateTotalPrice(Integer userId) {
		List<Order> order = getOrderListByUserIdByStatus0(userId);
		Integer totalPrice = order.get(0).getCalcTotalPrice();
		orderRepository.updateTotalPriceByUserIdByStatus0(totalPrice, userId);
	}

	/**
	 * 注文情報を未注文から注文済みに更新する.
	 * 
	 * @param order 注文情報
	 */
	public void updateOrderByUserIdByStatus0(Order order) {
		orderRepository.updateOrderByUserIdByStatus0(order);
	}

	/**
	 * 注文履歴を取得する.
	 * 
	 * @param userId ユーザID
	 * @return カートの商品一覧
	 */
	public List<Order> getOrderHistoryListByUserIdByStatusNon0(Integer userId) {

		List<Order> orderList = orderRepository.findByUserIdByStatusNot0(userId);

		// Map1 … キー：order_item_id、バリュー:注文トッピングリストのマップ
		Map<Integer, List<OrderTopping>> orderToppingMap = new LinkedHashMap<>();

		// Map2 … キー：order_item_id、バリュー:注文商品のマップ
		Map<Integer, OrderItem> orderItemMap = new LinkedHashMap<>();

		// Map3 … キー：order_id、バリュー:注文商品リストのマップ
		Map<Integer, List<OrderItem>> orderItemListMap = new LinkedHashMap<>();

		// Map4 … キー：order_id、バリュー:注文のマップ
		Map<Integer, Order> orderMap = new LinkedHashMap<>();

		// Map1を完成させる
		for (Order order : orderList) {
			orderToppingMap.put(order.getOrderItemList().get(0).getId(), new ArrayList<>());
		}

		for (Order order : orderList) {
			List<OrderTopping> orderToppingList = orderToppingMap.get(order.getOrderItemList().get(0).getId());
			OrderTopping orderTopping = order.getOrderItemList().get(0).getOrderToppingList().get(0);
			// オブジェクトが空ではない場合のみトッピングリストに追加
			if (Objects.nonNull(orderTopping.getTopping().getName())) {
				orderToppingList.add(order.getOrderItemList().get(0).getOrderToppingList().get(0));
			}
		}
		// Map1完成 （order_item_id１つに対して１つの注文トッピングリストorderToppingList(中身は0~複数)）

		// Map2を完成させる
		for (Order order : orderList) {
			orderItemMap.put(order.getOrderItemList().get(0).getId(), order.getOrderItemList().get(0));
		}

		for (Order order : orderList) {
			OrderItem orderItem = orderItemMap.get(order.getOrderItemList().get(0).getId());

			List<OrderTopping> orderToppingList = orderToppingMap.get(order.getOrderItemList().get(0).getId());
			orderItem.setOrderToppingList(orderToppingList);

			orderItemMap.put(order.getOrderItemList().get(0).getId(), orderItem);
		}
		// Map2完成 （order_item_id１つに対して１つの注文商品orderItem(先程作成したorderToppingListがセットされたもの)）

		// Map3を完成させる
		for (Order order : orderList) {
			orderItemListMap.put(order.getId(), new ArrayList<>());
		}

		for (OrderItem orderItem : orderItemMap.values()) {
			List<OrderItem> orderItemList = orderItemListMap.get(orderItem.getOrderId());
			orderItemList.add(orderItem);
		}
		// Map3完成 （order_id１つに対して１つの注文商品リストorderItemList）

		// Map4を完成させる
		for (Order order : orderList) {
			orderMap.put(order.getId(), order);
		}

		for (Order order : orderMap.values()) {
			orderMap.get(order.getId()).setOrderItemList(orderItemListMap.get(order.getId()));
		}
		// Map4完成 （order_id１つに対して１つの注文order）

		// サイズ数件の注文
		List<Order> distinctOrderList = new ArrayList<>(orderMap.values());
		Collections.reverse(distinctOrderList);

		return distinctOrderList;
	}

	/**
	 * order_itemsのorder_idをログインユーザのstatus = 0のものに変更する.
	 * 
	 * @param uuidOrderId UUIDのorder_id
	 * @param userOrderId ログインユーザのorder_id
	 */
	public void updateOrderId(Integer uuidOrderId, Integer userOrderId) {
		orderRepository.updateOrderId(uuidOrderId, userOrderId);
	}

	/**
	 * ログイン前に作成した仮のレコードを削除.
	 * 
	 * @param uuid UUID
	 */
	public void deleteUuidRecordByUuid(Integer uuid) {
		orderRepository.deleteUuidRecordByUuid(uuid);
	}

	/**
	 * UUID(仮UserId)をログイン後のUserIdに更新する.
	 * 
	 * @param userId ユーザID
	 * @param uuid   UUID
	 */
	public void updateUserId(Integer userId, Integer uuid) {
		orderRepository.updateUserId(userId, uuid);
	}
}
