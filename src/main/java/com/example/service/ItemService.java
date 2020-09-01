package com.example.service;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.Item;
import com.example.repository.ItemRepository;

/**
 * 商品情報を操作するサービス.
 * 
 * @author yumi takahashi
 *
 */
@Service
@Transactional
public class ItemService {

	@Autowired
	private ItemRepository itemRepository;

	/**
	 * （削除フラグが立っていない）全商品情報を価格が安い順で取得する.
	 * 
	 * @return 全商品情報一覧
	 */
	public List<Item> showListPriceAsc(Integer offset) {

		List<Item> itemList = itemRepository.findAll(offset);

		// comparatorを作成
		// thenComparing : 前の条件が同じだった場合にさらに比較を行う
		Comparator<Item> comparator = Comparator.comparing(Item::getPriceM).thenComparing((item1, item2) // 型は省略可能
		-> {
			Collator collator = Collator.getInstance(Locale.JAPANESE);
			return collator.compare(item1.getName(), item2.getName());
		});

		// sort処理
		List<Item> sorted = itemList.stream().sorted(comparator).collect(Collectors.toList());

		return sorted;
	}

	/**
	 * 商品情報を取得する.
	 * 
	 * @param id 商品ID
	 * @return 商品情報
	 */
	public Item showDetail(Integer id) {
		return itemRepository.load(id);
	}

	/**
	 * 商品を曖昧検索する.
	 * 
	 * @param name 検索キー
	 * @return 検索結果商品一覧
	 */
	public List<Item> showSearchItemList(String name, Integer offset) {
		List<Item> itemList = itemRepository.findByNameContainingByDeletedFalse(name, offset);

		// comparatorを作成
		// thenComparing : 前の条件が同じだった場合にさらに比較を行う
		Comparator<Item> comparator = Comparator.comparing(Item::getPriceM).thenComparing((item1, item2) // 型は省略可能
		-> {
			Collator collator = Collator.getInstance(Locale.JAPANESE);
			return collator.compare(item1.getName(), item2.getName());
		});

		// sort処理
		List<Item> sorted = itemList.stream().sorted(comparator).collect(Collectors.toList());

		return sorted;
	}

	/**
	 * （削除フラグが立っていない）価格が高い順で全商品情報を取得する.
	 * 
	 * @return 商品情報一覧
	 */
	public List<Item> showListPriceDesc(String name, Integer offset) {

		List<Item> itemList = itemRepository.findByNameContainingByDeletedFalse(name, offset);

		// comparatorを作成
		// thenComparing : 前の条件が同じだった場合にさらに比較を行う
		Comparator<Item> comparator = Comparator.comparing(Item::getPriceM).reversed().thenComparing((item1, item2) // 型は省略可能
		-> {
			Collator collator = Collator.getInstance(Locale.JAPANESE);
			return collator.compare(item1.getName(), item2.getName());
		});

		// sort処理
		List<Item> sorted = itemList.stream().sorted(comparator).collect(Collectors.toList());

		return sorted;
	}

	/**
	 * （削除フラグが立っていない）商品名をID順で取得する.
	 * 
	 * @param name 検索キー
	 * @return 商品名リスト
	 */
	public List<String> getNameList(String name) {
		return itemRepository.getNameList(name);
	}

	/**
	 * （削除フラグが立っていない）商品件数を取得する.
	 * 
	 * @return 商品件数
	 */
	public Integer getCount() {
		return itemRepository.getCount();
	}

	/**
	 * （削除フラグが立っていない）検索された商品件数を取得する.
	 * 
	 * @param name 検索キー
	 * @return 商品件数
	 */
	public Integer getSearchCount(String name) {
		return itemRepository.getSearchCount(name);
	}
}
