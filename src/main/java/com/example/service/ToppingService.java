package com.example.service;

import java.text.Collator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.Topping;
import com.example.repository.ToppingRepository;

/**
 * トッピング情報を操作するサービス.
 * 
 * @author yumi takahashi
 *
 */
@Service
@Transactional
public class ToppingService {

	@Autowired
	private ToppingRepository toppingRepository;

	/**
	 * 全トッピング情報を五十音順で取得する.
	 * 
	 * @return 全トッピング情報一覧
	 */
	public List<Topping> showList() {

		List<Topping> toppingList = toppingRepository.findAll();

		// sort処理
		List<Topping> sorted = toppingList.stream().sorted((topping1, topping2) // 型は省略可能
		-> {
			Collator collator = Collator.getInstance(Locale.JAPANESE);
			return collator.compare(topping1.getName(), topping2.getName());
		}).collect(Collectors.toList());

		return sorted;
	}
}
