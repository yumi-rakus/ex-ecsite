package com.example.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.service.UserDetailsServiceImpl;

/**
 * Spring Security設定クラス.
 * 
 * @author yumi takahashi
 *
 */
@Configuration
@EnableWebSecurity // Spring Securityの基本設定
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.userDetailsService(userDetailsServiceImpl) // 認証するユーザを設定する
				.passwordEncoder(new BCryptPasswordEncoder()); // 入力値をbcryptでハッシュ化した値でパスワード認証を行う
	}

	@Override
	public void configure(WebSecurity web) throws Exception {

		web.ignoring().antMatchers("/favicon.ico", "/css/**", "/js/**", "/img/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests() // 認可の設定
				.antMatchers("/ajax/**", "/", "/search", "/showDetail", "/cartIn", "/cartInComplete", "/showCartList",
						"/delete", "/toLogin", "/toRegister", "/register")
				.permitAll() // 全ユーザアクセス許可
				.anyRequest().authenticated(); // それ以外はすべて認証無しの場合アクセス不許可

		http.formLogin() // ログイン設定
				.loginPage("/toLogin").permitAll() // ログイン画面を表示するURL
				.loginProcessingUrl("/login") // ログイン処理を起動させるパス
				.failureHandler(new AuthenticationzfailureHandler()) // 認証失敗時に呼ばれるハンドラクラス
				.defaultSuccessUrl("/") // 認証成功時の遷移先
				.failureUrl("/toLogin?error") // ログイン処理失敗時の遷移先
				.usernameParameter("email").passwordParameter("password"); // パラメータの設定

		http.logout() // ログアウト設定
				.logoutUrl("/logout").permitAll() // ログアウト処理のパス
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // ログアウト処理のパス
				.logoutSuccessUrl("/") // ログアウト完了時のパス
				.deleteCookies("JSESSIONID").invalidateHttpSession(true);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
