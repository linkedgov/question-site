//TODO: pass more ids etc through tapestry.
(function($){
	
    /** Container of functions that may be invoked by the Tapestry.init() function. */
    $.extend(Tapestry.Initializer, {
         addFilter: function(specs){
    			
        	var populatePredicatesInFilter = function(filter,predicates){
        		if(predicates.length > 0){
        			var predicateSelect = filter.find(".predicate");
        			predicateSelect.empty();	
        			for(var i = 0; i < predicates.length; i++){
        				var predicate = predicates[i];
        				predicateSelect.append("<option value='"+predicate.value+"'>"+predicate.label+"</option>");
        			}
        		}
        	}
    
    		var handleAddFirstFilter = function(data){
    			$("#firstFilter").formFragment().show();
    			$("#firstFilter").css("display","inline-block");
    			var object = $("#firstFilter").find(".object");
    			object.attr("disabled","disabled");
    			$("#addFilter").attr("disabled","disabled");  
    			populatePredicatesInFilter($("#firstFilter"),data.predicates);
    			$("#ask").attr("disabled","disabled");
    			$("#subject").attr("disabled","disabled");
    		}
    		
    		var handleAddSecondFilter = function(data){
    			$("#secondFilter").formFragment().hide();
    			var object = $("#firstFilter").find(".object");
    			object.attr("disabled","disabled");
    			$("#addFilter").hide();
    			populatePredicatesInFilter($("#secondFilter"),data.predicates);
    			$("#ask").attr("disabled","disabled");
    		}
    	
        	
            $("#" + specs.id).click(function(){
            	
            	var firstFilterAjaxRequest = {
                    	url : specs.firstFilterUrl,
                    	success : handleAddFirstFilter, 
                    	data : 	{subject : $('#subject').val()},
                    	
                        type : "GET"
                };   
            	
            	var secondFilterAjaxRequest = {
                    	url : specs.firstFilterUrl,
                    	success : handleAddFirstFilter, 
                    	data : 	{
                    				subject : $('#subject').val(), 
                    				predicate : $('#firstFilter').find('.predicate').val(), 
                    				object : $('#firstFilter').find('.object').val()
                    			},
                        type : "GET"
                };   
            	
            	var ajaxRequest = $("#firstFilter").css("display") == "none" ? secondFilterAjaxRequest : firstFilterAjaxRequest;
            	
            	$.ajax(ajaxRequest);
            });
        }  
    	
    });
})(jQuery);