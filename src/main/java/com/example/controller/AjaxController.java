package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.ItemService;

/**
 * 非同期通信をするコントローラー.
 * 
 * @author yumi takahashi
 *
 */
@RestController
@RequestMapping("/ajax")
public class AjaxController {

	@Autowired
	private ItemService itemService;

	/**
	 * 検索のオートコンプリート.
	 * 
	 * @param key 検索キー
	 * @return 検索キーに引っかかる商品名一覧
	 */
	@RequestMapping("/autoComplete")
	public List<String> autoComplete(String key) {

		List<String> nameList = itemService.getNameList(key);

		return nameList;
	}
}
