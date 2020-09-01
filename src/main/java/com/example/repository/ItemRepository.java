package com.example.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.example.domain.Item;

/**
 * itemsテーブルを操作するリポジトリ.
 * 
 * @author yumi takahashi
 *
 */
@Repository
public class ItemRepository {

	/**
	 * Itemオブジェクトを生成するローマッパー.
	 */
	private static final RowMapper<Item> ITEM_ROW_MAPPER = (rs, i) -> {

		Item item = new Item();
		item.setId(rs.getInt("id"));
		item.setName(rs.getString("name"));
		item.setDescription(rs.getString("description"));
		item.setPriceM(rs.getInt("price_m"));
		item.setPriceL(rs.getInt("price_l"));
		item.setImagePath(rs.getString("image_path"));
		item.setDeleted(rs.getBoolean("deleted"));

		return item;
	};

	@Autowired
	private NamedParameterJdbcTemplate template;

	/**
	 * （削除フラグが立っていない）全商品情報をID順で取得する.
	 * 
	 * @return 全商品情報一覧
	 */
	public List<Item> findAll(Integer offset) {

		String sql = "SELECT id, name, description, price_m, price_l, image_path, deleted FROM items WHERE deleted = false ORDER BY id LIMIT 9 OFFSET :offset;";
		SqlParameterSource param = new MapSqlParameterSource().addValue("offset", offset);

		List<Item> itemList = template.query(sql, param, ITEM_ROW_MAPPER);

		return itemList;
	}

	/**
	 * 商品情報を取得する.
	 * 
	 * @param id 商品ID
	 * @return 商品情報
	 */
	public Item load(Integer id) {

		String sql = "SELECT id, name, description, price_m, price_l, image_path, deleted FROM items WHERE id = :id";
		SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);

		List<Item> item = template.query(sql, param, ITEM_ROW_MAPPER);

		return item.get(0);
	}

	/**
	 * 商品を曖昧検索する.
	 * 
	 * @param name 検索キー
	 * @return 検索結果商品一覧
	 */
	public List<Item> findByNameContainingByDeletedFalse(String name, Integer offset) {

		String sql = "SELECT id, name, description, price_m, price_l, image_path, deleted FROM items WHERE name LIKE :name AND deleted = false LIMIT 9 OFFSET :offset;";
		SqlParameterSource param = new MapSqlParameterSource().addValue("name", "%" + name + "%").addValue("offset",
				offset);

		List<Item> itemList = template.query(sql, param, ITEM_ROW_MAPPER);

		return itemList;
	}

	/**
	 * （削除フラグが立っていない）商品名をID順で取得する.
	 * 
	 * @param name 検索キー
	 * @return 商品名リスト
	 */
	public List<String> getNameList(String name) {
		String sql = "SELECT name FROM items WHERE name LIKE :name AND deleted = false ORDER BY id";
		SqlParameterSource param = new MapSqlParameterSource().addValue("name", "%" + name + "%");
		List<String> nameList = template.queryForList(sql, param, String.class);

		return nameList;
	}

	/**
	 * （削除フラグが立っていない）商品件数を取得する.
	 * 
	 * @return 商品件数
	 */
	public Integer getCount() {

		String sql = "SELECT count(*) FROM items WHERE deleted = false;";
		SqlParameterSource param = new MapSqlParameterSource();
		Integer count = template.queryForObject(sql, param, Integer.class);

		return count;
	}

	/**
	 * （削除フラグが立っていない）検索された商品件数を取得する.
	 * 
	 * @param name 検索キー
	 * @return 商品件数
	 */
	public Integer getSearchCount(String name) {

		String sql = "SELECT count(*) FROM items WHERE name LIKE :name AND deleted = false;";
		SqlParameterSource param = new MapSqlParameterSource().addValue("name", "%" + name + "%");
		Integer count = template.queryForObject(sql, param, Integer.class);

		return count;
	}
}
