/**
 * 
 */
$(function() {

	var today = new Date();
	today.setDate(today.getDate());

	var yyyy = today.getFullYear();
	var mm = ("0" + (today.getMonth() + 1)).slice(-2);
	var dd = ("0" + today.getDate()).slice(-2);

	$('#orderDate').val(yyyy + '-' + mm + '-' + dd);

	$('#orderDate').attr('min', yyyy + '-' + mm + '-' + dd);
});