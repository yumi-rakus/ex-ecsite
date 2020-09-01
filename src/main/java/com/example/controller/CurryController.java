package com.example.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.Item;
import com.example.domain.Order;
import com.example.domain.Topping;
import com.example.domain.User;
import com.example.form.ItemForm;
import com.example.form.ItemSearchForm;
import com.example.form.OrderForm;
import com.example.form.UserForm;
import com.example.service.ItemService;
import com.example.service.OrderItemService;
import com.example.service.OrderService;
import com.example.service.SendMailService;
import com.example.service.ToppingService;
import com.example.service.UserService;

/**
 * ECサイトを操作するコントローラー.
 * 
 * @author yumi takahashi
 *
 */
@Controller
@RequestMapping("")
public class CurryController {

	@Autowired
	private ItemService itemService;
	@Autowired
	private ToppingService toppingService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderItemService orderItemService;
	@Autowired
	private UserService userService;
	@Autowired
	private SendMailService sendMailService;
	@Autowired
	private HttpSession session;

	@ModelAttribute
	public ItemForm setUpItemForm() {
		return new ItemForm();
	}

	@ModelAttribute
	public UserForm setUpUserForm() {
		return new UserForm();
	}

	@ModelAttribute
	public ItemSearchForm setUpItemSearchForm() {
		return new ItemSearchForm();
	}

	@ModelAttribute
	public OrderForm setUpOrderForm() {
		return new OrderForm();
	}

	//////////////////////////////////////////////
	//// 商品一覧を表示する
	//////////////////////////////////////////////
	@RequestMapping("")
	public String index(Model model, String page) {

		if (Objects.isNull((Integer) session.getAttribute("count"))) {
			session.setAttribute("count", itemService.getCount());
		}

		if (Objects.isNull(page)) {
			session.setAttribute("page", 1);
		} else {
			session.setAttribute("page", Integer.parseInt(page));
		}

		session.removeAttribute("url");

		Integer pageNum = (Integer) session.getAttribute("page");
		List<Item> itemList = itemService.showListPriceAsc((pageNum * 9) - 9);
		model.addAttribute("itemList", itemList);

		return "item_list_curry";
	}

	//////////////////////////////////////////////
	//// 商品を検索する
	//////////////////////////////////////////////
	@RequestMapping("/search")
	public String search(ItemSearchForm form, Model model, String page) {

		try {
			if (("").equals(form.getSearchName()) && Integer.valueOf(1).equals(form.getSoting())) {
				return "redirect:/";
			}

			if (Objects.nonNull(form.getSoting())) {
				session.setAttribute("sform", form);
			}

			if (Objects.isNull(page)) {
				session.setAttribute("searchPage", 1);
				session.setAttribute("searchCount", itemService.getSearchCount(form.getSearchName()));
			} else {
				session.setAttribute("searchPage", Integer.parseInt(page));
			}

			session.setAttribute("url", "/search");
			Integer searchPageNum = (Integer) session.getAttribute("searchPage");

			List<Item> itemList;

			ItemSearchForm sform = (ItemSearchForm) session.getAttribute("sform");
			String searchName = sform.getSearchName().replace("　", " ").trim();
			Integer offset = (searchPageNum * 9) - 9;

			if (sform.getSoting() == 1) {
				itemList = itemService.showSearchItemList(searchName, offset);
			} else if (sform.getSoting() == 2) {
				itemList = itemService.showListPriceDesc(searchName, offset);
			} else {
				itemList = itemService.showSearchItemList(searchName, offset);
			}

			form.setSearchName(searchName);
			form.setSoting(sform.getSoting());

			if (itemList.isEmpty()) {
				model.addAttribute("searchResult", "該当する商品はありません");
				return index(model, "1");
			} else {
				model.addAttribute("itemList", itemList);
			}

			return "item_list_curry";
		} catch (Exception e) {
			return index(model, "1");
		}
	}

	//////////////////////////////////////////////
	//// 商品詳細ページを表示する
	//////////////////////////////////////////////
	@RequestMapping("/showDetail")
	public String showDetail(String id, Model model, ItemForm form) {

		Item item = itemService.showDetail(Integer.parseInt(id));
		List<Topping> toppingList = toppingService.showList();
		item.setToppingList(toppingList);

		form.setSize("M");
		form.setQuantity(1);

		model.addAttribute("item", item);

		return "item_detail";
	}

	//////////////////////////////////////////////
	//// 商品をショッピングカートに追加する
	//////////////////////////////////////////////
	@RequestMapping("/cartIn")
	public String cartIn(ItemForm form) {

		try {
			Integer userId;

			if (Objects.isNull((Integer) session.getAttribute("userId"))) {
				UUID uuid = UUID.randomUUID();
				userId = uuid.hashCode();
				session.setAttribute("userId", userId);
			} else {
				userId = (Integer) session.getAttribute("userId");
			}

			orderItemService.cartIn(form, userId);

			return "redirect:/cartInComplete";
		} catch (Exception e) {
			return "redirect:/";
		}
	}

	//////////////////////////////////////////////
	//// 商品追加完了ページを表示する
	//////////////////////////////////////////////
	@RequestMapping("/cartInComplete")
	public String cartInComplete(Model model) {

		Integer userId = (Integer) session.getAttribute("userId");

		if (Objects.nonNull(userId)) {

			if (orderService.existsByStatus0ByUserId(userId)) {
				List<Order> order = orderService.getOrderListByUserIdByStatus0(userId);

				if (order.get(0).getOrderItemList().get(0).getItem().getId() != 0) {
					model.addAttribute("orderItemList", order.get(0).getOrderItemList());
					model.addAttribute("tax", order.get(0).getTax());
					model.addAttribute("totalPrice", order.get(0).getTotalPrice() + order.get(0).getTax());
					model.addAttribute("cartInComplete", "cartInComplete");
				} else {
					model.addAttribute("notExistOrderItemList", "カートに商品がありません");
				}
			} else {
				return "redirect:/";
			}
		} else {
			return "redirect:/";
		}
		return "cart_list";
	}

	//////////////////////////////////////////////
	//// ショッピングカートの中身を表示する
	//////////////////////////////////////////////
	@RequestMapping("/showCartList")
	public String showCartList(Model model) {

		if (Objects.isNull((Integer) session.getAttribute("userId"))) {
			model.addAttribute("notExistOrderItemList", "カートに商品がありません");
		} else {
			Integer userId = (Integer) session.getAttribute("userId");

			if (orderService.existsByStatus0ByUserId(userId)) {
				List<Order> order = orderService.getOrderListByUserIdByStatus0(userId);

				if (order.get(0).getOrderItemList().get(0).getItem().getId() != 0) {
					model.addAttribute("orderItemList", order.get(0).getOrderItemList());
					model.addAttribute("tax", order.get(0).getTax());
					model.addAttribute("totalPrice", order.get(0).getTotalPrice() + order.get(0).getTax());
				} else {
					model.addAttribute("notExistOrderItemList", "カートに商品がありません");
				}
			} else {
				model.addAttribute("notExistOrderItemList", "カートに商品がありません");
			}
		}
		return "cart_list";
	}

	//////////////////////////////////////////////
	//// ショッピングカートから商品を削除する
	//////////////////////////////////////////////
	@RequestMapping("/delete")
	public String delete(String orderItemId) {

		if (Objects.nonNull((Integer) session.getAttribute("userId"))) {

			Integer userId = (Integer) session.getAttribute("userId");
			try {
				orderItemService.deleteOrderItem(Integer.parseInt(orderItemId), userId);
				return "redirect:/showCartList";
			} catch (Exception e) {
				return "redirect:/showCartList";
			}
		} else {
			return "redirect:/showCartList";
		}
	}

	//////////////////////////////////////////////
	//// 注文確認画面を表示する
	//////////////////////////////////////////////
	@RequestMapping("/order")
	public String order(Model model, OrderForm form) {

		try {
			Integer userId = (Integer) session.getAttribute("userId");

			List<Order> order = orderService.getOrderListByUserIdByStatus0(userId);

			if (!order.isEmpty()) {
				model.addAttribute("orderItemList", order.get(0).getOrderItemList());
				model.addAttribute("tax", order.get(0).getTax());
				model.addAttribute("totalPrice", order.get(0).getTotalPrice() + order.get(0).getTax());

				User user = userService.findById(userId);
				form.setName(user.getName());
				form.setEmail(user.getEmail());
				form.setZipcodeFirst(user.getZipcode().substring(0, 3));
				form.setZipcodeLast(user.getZipcode().substring(3));
				form.setAddress(user.getAddress());
				form.setTelephone(user.getTelephone());

				return "order_confirm";
			} else {
				return "redirect:/showCartList";
			}

		} catch (Exception e) {
			return showCartList(model);
		}
	}

	//////////////////////////////////////////////
	//// 注文を完了する
	//////////////////////////////////////////////
	@RequestMapping("/orderComplete")
	public String orderComplete(@Validated OrderForm form, BindingResult result, Model model) {

		if (result.hasErrors()) {
			return order(model, form);
		}

		Integer userId = (Integer) session.getAttribute("userId");
		Order userOrder = orderService.getOrderListByUserIdByStatus0(userId).get(0);
		userOrder.setDestinationAddress(form.getAddress());
		userOrder.setDestinationEmail(form.getEmail());
		userOrder.setDestinationName(form.getName());
		userOrder.setDestinationZipcode(form.getZipcodeFirst() + "-" + form.getZipcodeLast());

		try {
			// 現在時間から3時間以内の選択は不可
			Date now = new Date();
			String delivery = form.getOrderDate() + " " + form.getOrderTime() + ":59:59";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Timestamp timestampDelivery = new Timestamp(sdf.parse(delivery).getTime());

			long diff = sdf.parse(delivery).getTime() - now.getTime();

			if (diff / (60 * 60 * 1000) < 3) {
				model.addAttribute("orderTimeResult", "今から3時間後以降の日時をご入力ください");
				return order(model, form);
			}

			Integer status;

			if (form.getPaymentMethod() == 1) {
				status = 1;
			} else {
				status = 2;
			}
			userOrder.setStatus(status);

			Order order = new Order();
			order.setUserId(userId);
			order.setStatus(status);
			order.setOrderDate(now);
			order.setDestinationName(form.getName());
			order.setDestinationEmail(form.getEmail());
			order.setDestinationZipcode(form.getZipcodeFirst() + form.getZipcodeLast());
			order.setDestinationAddress(form.getAddress());
			order.setDestinationTel(form.getTelephone());
			order.setDeliveryTime(timestampDelivery);
			order.setPaymentMethod(form.getPaymentMethod());
			orderService.updateOrderByUserIdByStatus0(order);
			sendMailService.sendMail(userId, userOrder);

			return "order_finished";

		} catch (Exception e) {
			return showCartList(model);
		}
	}

	//////////////////////////////////////////////
	//// 注文履歴を表示する
	//////////////////////////////////////////////
	@RequestMapping("/orderHistory")
	public String orderHistory(Model model) {

		Integer userId = (Integer) session.getAttribute("userId");
		List<Order> orderList = orderService.getOrderHistoryListByUserIdByStatusNon0(userId);

		if (!orderList.isEmpty()) {
			model.addAttribute("orderList", orderList);
		} else {
			model.addAttribute("orderResult", "注文履歴がありません");
		}

		return "order_history";
	}

	//////////////////////////////////////////////
	//// ログイン画面を表示する
	//////////////////////////////////////////////
	@RequestMapping("/toLogin")
	public String toLogin() {
		return "login";
	}

	//////////////////////////////////////////////
	//// ユーザ登録ページを表示する
	//////////////////////////////////////////////
	@RequestMapping("/toRegister")
	public String toRegister() {
		return "register_user";
	}

	//////////////////////////////////////////////
	//// ユーザ登録をする
	//////////////////////////////////////////////
	@RequestMapping("/register")
	public String register(@Validated UserForm form, BindingResult result, Model model) {

		try {
			if (userService.existEmail(form.getEmail())) {
				model.addAttribute("emailResult", "そのメールアドレスは既に登録されています");
				return "register_user";
			}

			if (!form.getPassword().equals(form.getConfirmPassword())) {
				model.addAttribute("passwordResult", "パスワードと確認用パスワードが不一致です");
				return "register_user";
			}

			if (result.hasErrors()) {
				return "register_user";
			}

			User user = new User();
			user.setName(form.getName());
			user.setEmail(form.getEmail());
			user.setPassword(form.getPassword());
			user.setZipcode(form.getZipcodeFirst() + form.getZipcodeLast());
			user.setAddress(form.getAddress());
			user.setTelephone(form.getTelephone());
			userService.insert(user);

			return "redirect:/toLogin";
		} catch (Exception e) {
			return "redirect:/toRegister";
		}
	}
}
