/**
 * 
 */
$(function() {
	
	autoCom("");
	
	$('#search').keyup(function() {
		
		var key = $(this).val();
		autoCom(key);
	});
});

function autoCom(key) {
	
	$.ajax({
		url: "/ajax/autoComplete",
		data: {
			"key": key
		},
		dataType: "json"
	}).done(function(nameList){
		
		$('#searchName').empty();
		
		for(var name of nameList){
			var option = $('<option>').val(name).text(name);
			$('#searchName').append(option);
		}
		
	}).fail(function(XMLHttpRequest,textStatus,errorThrown){
    	alert("リクエストに失敗"+textStatus+":\n"+errorThrown)
    });
}