//TODO: pass more ids etc through tapestry.
(function($){
	
    /** Container of functions that may be invoked by the Tapestry.init() function. */
    $.extend(Tapestry.Initializer, {
         addFilter: function(specs){
    			
        	//TODO: remove this and put it into a utility method.
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
            	
            	var ajaxRequest = $("#firstFilter").css("display") === "none" ? secondFilterAjaxRequest : firstFilterAjaxRequest;
            	
            	$.ajax(ajaxRequest);
            });
        },
        
        firstFilterPredicate : function(specs){       	
        	//TODO this is generic, move outside of here.
        	var populateSelectInFilter = function(selectElem, options){
        		if(options.length == 0){
        			return;
        		}
        		selectElem.empty();	
        	    for(var i = 0; i < options.length; i++){
        	    	var option = options[i];
        	    	selectElem.append("<option value='"+option.value+"'>"+option.label+"</option>");
        	    } 
        	}
        	
         	var handleFirstFilterPredicateChange = function(data){
         		
         		$("#firstFilter").find(".objectContainer").formFragment().hide();
        		var objectEditor = $("#firstFilter").find(".objectContainer#"+data.editorId);
        		objectEditor.formFragment().show();
        		objectEditor.css("display","inline-block");
        		
        		var field = objectEditor.find(":input");
        		field.show();
        		field.css("display","inline-block");
        		field.removeAttr("disabled");
        		if(field.is("select")){
        			populateSelectInFilter(field, data.objects);
        		}
        		
        	};
        	
        	var firstFilterPredicate = $("#firstFilter").find(".predicate");
        	firstFilterPredicate.change(function(){
        		
        		var ajaxRequest = {
                    	url : specs.url,
                    	success : handleFirstFilterPredicateChange, 
                    	data : 	{
                    				subject: $('#subject').val(),
                    				predicate: firstFilterPredicate.val()
                    			},
                        type : "GET"
                };
        		
        		$.ajax(ajaxRequest);
        	});
        }
    	
    });
})(jQuery);