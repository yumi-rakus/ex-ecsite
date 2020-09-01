/**
 * 
 */
'use strict';

$(function() {

	$('#item').on(
			'change',
			function() {

				var itemSize = $('input[name=size]:checked');

				// 商品価格
				var itemPriceStr = itemSize.nextAll('.money').text();
				var itemPrice = Number(itemPriceStr.replace(',', ''));

				// 商品数量
				var itemQuantity = Number($('select[name=quantity]').val());

				// トッピング価格
				var toppingPrice;
				if (itemSize.val() === 'M') {
					toppingPrice = 200;
				} else {
					toppingPrice = 300;
				}

				// トッピング数量
				var toppingQuantity = $('.topping:checked').size();

				// 合計金額
				var totalPrice = (itemPrice + (toppingPrice * toppingQuantity))
						* itemQuantity;

				// 合計金額の更新
				$('#updateTotalPrice').text(totalPrice.toLocaleString());
			});

});