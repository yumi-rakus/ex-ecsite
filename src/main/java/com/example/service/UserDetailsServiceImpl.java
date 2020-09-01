package com.example.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.domain.LoginUser;
import com.example.domain.Order;
import com.example.domain.User;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserService userService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private HttpSession session;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		User user = userService.findByEmail(email);

		if (Objects.isNull(user.getName())) {
			throw new UsernameNotFoundException("not found this mailaddress.");
		} else {

			// ログインーログアウトでカートの中身を統合する処理
			if (Objects.nonNull((Integer) session.getAttribute("userId"))) {

				Integer uuid = (Integer) session.getAttribute("userId");
				List<Order> userOrder = orderService.getOrderListByUserIdByStatus0(user.getId());

				if (!userOrder.isEmpty()) {
					// ログインしたユーザのショッピングカートに商品が存在した場合

					if (orderService.existsByStatus0ByUserId(uuid)) {
						Integer uuidOrderId = orderService.getOrderIdByUserIdByStatus0(uuid);
						Integer userOrderId = orderService.getOrderIdByUserIdByStatus0(user.getId());

						// 注文IDをログインユーザのものに変更
						orderService.updateOrderId(uuidOrderId, userOrderId);
						orderService.deleteUuidRecordByUuid(uuid);

						if (userOrder.get(0).getOrderItemList().get(0).getItem().getId() != 0) {
							// 合計金額を更新
							orderService.updateTotalPrice(user.getId());
						}
					}
				} else {
					// ログインしたユーザのショッピングカートに商品が存在しない場合
					// ユーザIDのみ変更
					orderService.updateUserId(user.getId(), uuid);
				}
			}

			session.setAttribute("userId", user.getId());
		}

		Collection<GrantedAuthority> authorityList = new ArrayList<>();
		authorityList.add(new SimpleGrantedAuthority("ROLE_USER")); // ユーザ権限付与
		return new LoginUser(user, authorityList);
	}
}
