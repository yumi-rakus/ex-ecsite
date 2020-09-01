package com.example.domain;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

/**
 * ログインユーザを表すドメイン.
 * 
 * @author yumi takahashi
 *
 */
public class LoginUser extends org.springframework.security.core.userdetails.User {

	private static final long serialVersionUID = 1L;

	/** ユーザ情報 */
	private User user;

	// constructor
	public LoginUser(User user, Collection<GrantedAuthority> authorityList) {
		super(user.getEmail(), user.getPassword(), authorityList);
		this.user = user;
	}

	// getter
	public User getUser() {
		return user;
	}

}
