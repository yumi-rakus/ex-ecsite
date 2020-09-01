package com.example.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import com.example.domain.Item;
import com.example.domain.Order;
import com.example.domain.OrderItem;
import com.example.domain.OrderTopping;
import com.example.domain.Topping;

/**
 * ordersテーブルを操作するリポジトリ.
 * 
 * @author yumi takahashi
 *
 */
@Repository
public class OrderRepository {

	/**
	 * Orderオブジェクトを生成するローマッパー.
	 */
	private static final RowMapper<Order> ORDER_ROW_MAPPER = (rs, i) -> {
		Order order = new Order();
		order.setId(rs.getInt("id"));
		order.setUserId(rs.getInt("user_id"));
		order.setStatus(rs.getInt("status"));
		order.setTotalPrice(rs.getInt("total_price"));
		order.setOrderDate(rs.getDate("order_date"));
		order.setDestinationName(rs.getString("destination_name"));
		order.setDestinationEmail(rs.getString("destination_email"));
		order.setDestinationZipcode(rs.getString("destination_zipcode"));
		order.setDestinationAddress(rs.getString("destination_address"));
		order.setDestinationTel(rs.getString("destination_tel"));
		order.setDeliveryTime(rs.getTimestamp("delivery_time"));
		order.setPaymentMethod(rs.getInt("payment_method"));

		OrderItem orderItem = new OrderItem();
		orderItem.setId(rs.getInt("order_item_id"));
		orderItem.setItemId(rs.getInt("item_id"));
		orderItem.setOrderId(rs.getInt("order_id"));
		orderItem.setQuantity(rs.getInt("quantity"));

		if (Objects.nonNull(rs.getString("size"))) {
			orderItem.setSize(rs.getString("size").charAt(0));
		}

		Item item = new Item();
		item.setId(rs.getInt("item_id"));
		item.setName(rs.getString("item_name"));
		item.setDescription(rs.getString("item_description"));
		item.setPriceM(rs.getInt("item_pricem"));
		item.setPriceL(rs.getInt("item_pricel"));
		item.setImagePath(rs.getString("item_imagepath"));
		orderItem.setItem(item);

		OrderTopping orderTopping = new OrderTopping();
		orderTopping.setId(rs.getInt("order_topping_id"));
		orderTopping.setToppingId(rs.getInt("topping_id"));
		orderTopping.setOrderItemId(rs.getInt("order_item_id"));

		Topping topping = new Topping();
		topping.setId(rs.getInt("topping_id"));
		topping.setName(rs.getString("topping_name"));
		topping.setPriceM(rs.getInt("topping_pricem"));
		topping.setPriceL(rs.getInt("topping_pricel"));
		orderTopping.setTopping(topping);

		List<OrderTopping> orderToppingList = new ArrayList<>();

		orderToppingList.add(orderTopping);

		orderItem.setOrderToppingList(orderToppingList);

		List<OrderItem> orderItemList = new ArrayList<>();

		orderItemList.add(orderItem);

		order.setOrderItemList(orderItemList);

		return order;
	};

	@Autowired
	private NamedParameterJdbcTemplate template;

	private SimpleJdbcInsert insert;

	@PostConstruct
	public void init() {

		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert((JdbcTemplate) template.getJdbcOperations());

		SimpleJdbcInsert withTableName = simpleJdbcInsert.withTableName("orders");

		insert = withTableName.usingGeneratedKeyColumns("id");
	}

	/**
	 * 未注文（status=0）のレコードが存在するかどうかを返す.
	 * 
	 * @param userId ユーザID
	 * @return status = 0 (未注文)のレコードが存在していればtrue、存在していなければfalseを返す
	 */
	public boolean existsByStatus0ByUserId(Integer userId) {

		String sql = "SELECT count(*) FROM orders WHERE status = 0 AND user_id = :userId";
		SqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId);

		Integer count = template.queryForObject(sql, param, Integer.class);

		if (count == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 未注文（status=0）の注文IDを取得する.
	 * 
	 * @param userId ユーザID
	 * @return 注文ID
	 */
	public Integer findOrderIdByUserIdByStatus0(Integer userId) {

		String sql = "SELECT id FROM orders WHERE status = 0 AND user_id = :userId";
		SqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId);
		Integer orderId = template.queryForObject(sql, param, Integer.class);

		return orderId;
	}

	/**
	 * 未注文（status=0）の注文情報を挿入する.
	 * 
	 * @param order 注文情報
	 * @return 注文情報
	 */
	public Order insertOrder(Order order) {

		SqlParameterSource param = new BeanPropertySqlParameterSource(order);

		Number key = insert.executeAndReturnKey(param);
		order.setId(key.intValue());

		return order;
	}

	/**
	 * 未注文（status=0）の注文リストを取得する.
	 * 
	 * @param userId ユーザID
	 * @return 未注文の注文情報
	 */
	public List<Order> findByUserIdByStatus0(Integer userId) {

		StringBuilder sql = new StringBuilder();

		sql.append("SELECT ");
		sql.append("ord.id AS id, ");
		sql.append("ord.user_id AS user_id, ");
		sql.append("ord.status AS status, ");
		sql.append("ord.total_price AS total_price, ");
		sql.append("ord.order_date AS order_date, ");
		sql.append("ord.destination_name AS destination_name, ");
		sql.append("ord.destination_email AS destination_email, ");
		sql.append("ord.destination_zipcode AS destination_zipcode, ");
		sql.append("ord.destination_address AS destination_address, ");
		sql.append("ord.destination_tel AS destination_tel, ");
		sql.append("ord.delivery_time AS delivery_time, ");
		sql.append("ord.payment_method AS payment_method, ");
		sql.append("orditem.id AS order_item_id, ");
		sql.append("items.id AS item_id, ");
		sql.append("ord.id AS order_id, ");
		sql.append("orditem.quantity AS quantity, ");
		sql.append("orditem.size AS size, ");
		sql.append("items.name AS item_name, ");
		sql.append("items.description AS item_description, ");
		sql.append("items.price_m AS item_pricem, ");
		sql.append("items.price_l AS item_pricel, ");
		sql.append("items.image_path AS item_imagepath, ");
		sql.append("ordtop.id AS order_topping_id, ");
		sql.append("toppings.id AS topping_id, ");
		sql.append("toppings.name AS topping_name, ");
		sql.append("toppings.price_m AS topping_pricem, ");
		sql.append("toppings.price_l AS topping_pricel ");
		sql.append("FROM ");
		sql.append("orders AS ord ");
		sql.append("LEFT OUTER JOIN ");
		sql.append("order_items AS orditem ");
		sql.append("ON ord.id = orditem.order_id ");
		sql.append("LEFT OUTER JOIN ");
		sql.append("order_toppings AS ordtop ");
		sql.append("ON orditem.id = ordtop.order_item_id ");
		sql.append("LEFT OUTER JOIN ");
		sql.append("items ");
		sql.append("ON items.id = orditem.item_id ");
		sql.append("LEFT OUTER JOIN ");
		sql.append("toppings ");
		sql.append("ON toppings.id = ordtop.topping_id ");
		sql.append("WHERE ");
		sql.append("ord.user_id = :userId ");
		sql.append("AND ");
		sql.append("ord.status = 0 ");
		sql.append("ORDER BY ");
		sql.append("orditem.id DESC, ");
		sql.append("ordtop.id;");

		SqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId);

		List<Order> orderList = template.query(sql.toString(), param, ORDER_ROW_MAPPER);

		return orderList;
	}

	/**
	 * 合計金額を更新する.
	 * 
	 * @param totalPrice 合計金額
	 * @param userId     ユーザID
	 */
	public void updateTotalPriceByUserIdByStatus0(Integer totalPrice, Integer userId) {

		String sql = "UPDATE orders SET total_price = :totalPrice WHERE status = 0 AND user_id = :userId";

		SqlParameterSource param = new MapSqlParameterSource().addValue("totalPrice", totalPrice).addValue("userId",
				userId);

		template.update(sql, param);
	}

	/**
	 * 注文情報を未注文から注文済みに更新する.
	 * 
	 * @param order 注文情報
	 */
	public void updateOrderByUserIdByStatus0(Order order) {

		StringBuilder sql = new StringBuilder();

		sql.append("UPDATE orders ");
		sql.append("SET ");
		sql.append("status = :status, ");
		sql.append("order_date = :orderDate, ");
		sql.append("destination_name = :destinationName, ");
		sql.append("destination_email = :destinationEmail, ");
		sql.append("destination_zipcode = :destinationZipcode, ");
		sql.append("destination_address = :destinationAddress, ");
		sql.append("destination_tel = :destinationTel, ");
		sql.append("delivery_time = :deliveryTime, ");
		sql.append("payment_method = :paymentMethod ");
		sql.append("WHERE ");
		sql.append("user_id = :userId ");
		sql.append("AND ");
		sql.append("status = 0;");

		SqlParameterSource param = new MapSqlParameterSource().addValue("status", order.getStatus())
				.addValue("orderDate", order.getOrderDate()).addValue("destinationName", order.getDestinationName())
				.addValue("destinationEmail", order.getDestinationEmail())
				.addValue("destinationZipcode", order.getDestinationZipcode())
				.addValue("destinationAddress", order.getDestinationAddress())
				.addValue("destinationTel", order.getDestinationTel()).addValue("deliveryTime", order.getDeliveryTime())
				.addValue("paymentMethod", order.getPaymentMethod()).addValue("userId", order.getUserId());

		template.update(sql.toString(), param);
	}

	/**
	 * 注文済の注文リストを取得する.
	 * 
	 * @param userId ユーザID
	 * @return 注文済の注文情報
	 */
	public List<Order> findByUserIdByStatusNot0(Integer userId) {

		StringBuilder sql = new StringBuilder();

		sql.append("SELECT ");
		sql.append("ord.id AS id, ");
		sql.append("ord.user_id AS user_id, ");
		sql.append("ord.status AS status, ");
		sql.append("ord.total_price AS total_price, ");
		sql.append("ord.order_date AS order_date, ");
		sql.append("ord.destination_name AS destination_name, ");
		sql.append("ord.destination_email AS destination_email, ");
		sql.append("ord.destination_zipcode AS destination_zipcode, ");
		sql.append("ord.destination_address AS destination_address, ");
		sql.append("ord.destination_tel AS destination_tel, ");
		sql.append("ord.delivery_time AS delivery_time, ");
		sql.append("ord.payment_method AS payment_method, ");
		sql.append("orditem.id AS order_item_id, ");
		sql.append("items.id AS item_id, ");
		sql.append("ord.id AS order_id, ");
		sql.append("orditem.quantity AS quantity, ");
		sql.append("orditem.size AS size, ");
		sql.append("items.name AS item_name, ");
		sql.append("items.description AS item_description, ");
		sql.append("items.price_m AS item_pricem, ");
		sql.append("items.price_l AS item_pricel, ");
		sql.append("items.image_path AS item_imagepath, ");
		sql.append("ordtop.id AS order_topping_id, ");
		sql.append("toppings.id AS topping_id, ");
		sql.append("toppings.name AS topping_name, ");
		sql.append("toppings.price_m AS topping_pricem, ");
		sql.append("toppings.price_l AS topping_pricel ");
		sql.append("FROM ");
		sql.append("orders AS ord ");
		sql.append("LEFT OUTER JOIN ");
		sql.append("order_items AS orditem ");
		sql.append("ON ord.id = orditem.order_id ");
		sql.append("LEFT OUTER JOIN ");
		sql.append("order_toppings AS ordtop ");
		sql.append("ON orditem.id = ordtop.order_item_id ");
		sql.append("LEFT OUTER JOIN ");
		sql.append("items ");
		sql.append("ON items.id = orditem.item_id ");
		sql.append("LEFT OUTER JOIN ");
		sql.append("toppings ");
		sql.append("ON toppings.id = ordtop.topping_id ");
		sql.append("WHERE ");
		sql.append("ord.user_id = :userId ");
		sql.append("AND ");
		sql.append("ord.status <> 0 ");
		sql.append("ORDER BY ");
		sql.append("ord.id, ");
		sql.append("orditem.id DESC, ");
		sql.append("ordtop.id;");

		SqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId);

		List<Order> orderList = template.query(sql.toString(), param, ORDER_ROW_MAPPER);

		return orderList;
	}

	/**
	 * order_itemsのorder_idをログインユーザのstatus = 0のものに変更する.
	 * 
	 * @param uuidOrderId UUIDのorder_id
	 * @param userOrderId ログインユーザのorder_id
	 */
	public void updateOrderId(Integer uuidOrderId, Integer userOrderId) {

		String sql = "UPDATE order_items SET order_id = :userOrderId WHERE order_id = :uuidOrderId";

		SqlParameterSource param = new MapSqlParameterSource().addValue("userOrderId", userOrderId)
				.addValue("uuidOrderId", uuidOrderId);

		template.update(sql, param);
	}

	/**
	 * ログイン前に作成した仮のレコードを削除.
	 * 
	 * @param uuid UUID
	 */
	public void deleteUuidRecordByUuid(Integer uuid) {

		String sql = "DELETE FROM orders WHERE status = 0 AND user_id = :uuid";

		SqlParameterSource param = new MapSqlParameterSource().addValue("uuid", uuid);

		template.update(sql, param);
	}

	/**
	 * UUID(仮UserId)をログイン後のUserIdに更新する.
	 * 
	 * @param userId ユーザID
	 * @param uuid   UUID
	 */
	public void updateUserId(Integer userId, Integer uuid) {

		String sql = "UPDATE orders SET user_id = :userId WHERE user_id = :uuid AND status = 0";

		SqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId).addValue("uuid", uuid);

		template.update(sql, param);
	}
}
