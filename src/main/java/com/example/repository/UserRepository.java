package com.example.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.example.domain.User;

/**
 * usersテーブルを操作するリポジトリ.
 * 
 * @author yumi takahashi
 *
 */
@Repository
public class UserRepository {

	/**
	 * Userオブジェクトを生成するローマッパー.
	 */
	private static final RowMapper<User> USER_ROW_MAPPER = (rs, i) -> {

		User user = new User();

		user.setId(rs.getInt("id"));
		user.setName(rs.getString("name"));
		user.setEmail(rs.getString("email"));
		user.setPassword(rs.getString("password"));
		user.setZipcode(rs.getString("zipcode"));
		user.setAddress(rs.getString("address"));
		user.setTelephone(rs.getString("telephone"));

		return user;
	};

	@Autowired
	private NamedParameterJdbcTemplate template;

	/**
	 * メールアドレスが既に登録されているものかどうかを判定する.
	 * 
	 * @param email メールアドレス
	 * @return 既に登録されていればtrue, 登録されていなければfalseを返す
	 */
	public boolean existEmail(String email) {

		String sql = "SELECT count(*) FROM users WHERE email = :email";
		SqlParameterSource param = new MapSqlParameterSource().addValue("email", email);

		Integer count = template.queryForObject(sql, param, Integer.class);

		if (count == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * メールアドレスからユーザ情報を取得する.
	 * 
	 * @param email メールアドレス
	 * @return ユーザ情報
	 */
	public User findByEmail(String email) {

		boolean judge = existEmail(email);

		if (judge) {
			String sql = "SELECT id, name, email, password, zipcode, address, telephone FROM users WHERE email = :email";
			SqlParameterSource param = new MapSqlParameterSource().addValue("email", email);

			List<User> user = template.query(sql, param, USER_ROW_MAPPER);

			return user.get(0);
		} else {
			return new User();
		}

	}

	/**
	 * ユーザ情報を挿入する.
	 * 
	 * @param user ユーザ情報
	 */
	public void insert(User user) {
		String sql = "INSERT INTO users (name, email, password, zipcode, address, telephone) VALUES (:name, :email, :password, :zipcode, :address, :telephone);";
		SqlParameterSource param = new MapSqlParameterSource().addValue("name", user.getName())
				.addValue("email", user.getEmail()).addValue("password", user.getPassword())
				.addValue("zipcode", user.getZipcode()).addValue("address", user.getAddress())
				.addValue("telephone", user.getTelephone());

		template.update(sql, param);
	}

	/**
	 * IDからユーザ情報を取得する.
	 * 
	 * @param id ユーザID
	 * @return ユーザ情報
	 */
	public User findById(Integer id) {

		String sql = "SELECT id, name, email, password, zipcode, address, telephone FROM users WHERE id = :id";
		SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);
		List<User> user = template.query(sql, param, USER_ROW_MAPPER);

		return user.get(0);
	}
}
