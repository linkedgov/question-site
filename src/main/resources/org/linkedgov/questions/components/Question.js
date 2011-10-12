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
        	
    		
        	var showFilter = function(filterSelector,data){
        		$(filterSelector).formFragment().show();
    			$(filterSelector).css("display","inline-block");
    			var object = $(filterSelector).find(".object");
    			object.attr("disabled","disabled"); 
    			populatePredicatesInFilter($(filterSelector),data.predicates);
    			$("#ask").attr("disabled","disabled");
    			$("#subject").attr("disabled","disabled");
        	}
    
    		var handleAddFirstFilter = function(data){
    			showFilter("#firstFilter", data);
    			$("#addFilter").attr("disabled","disabled"); 
    		}
    		
    		var handleAddSecondFilter = function(data){
    			showFilter("#secondFilter", data);
    			//it's the last filter, so hide the add button.
    			$("#addFilter").hide();
    			$("#firstFilter").find(":input").each(function(key,value){
    					$(value).attr("disabled","disabled");
    				});
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
                    	success : handleAddSecondFilter, 
                    	data : 	{
                    				subject : $('#subject').val(), 
                    				predicate : $('#firstFilter').find('.predicate').val(), 
                    				object : $('#firstFilter').find('.object').val()
                    			},
                        type : "GET"
                };   
            	
            	var ajaxRequest = $("#firstFilter").css("display") !== "none" ? secondFilterAjaxRequest : firstFilterAjaxRequest;
            	
            	$.ajax(ajaxRequest);
            });
        },
        
        firstFilter : function(specs){       	
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
        	
         	var handlePredicateChange = function(data){
         		
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
                    	success : handlePredicateChange, 
                    	data : 	{
                    				subject: $('#subject').val(),
                    				predicate: firstFilterPredicate.val()
                    			},
                        type : "GET"
                };
        		
        		$.ajax(ajaxRequest);
        	});
        	
        	var firstFilterObjects = $("#firstFilter").find(".object");
        	var handleObjectChangeEvent = function(){
        		if(typeof($(this).val()) != 'undefined' 
					&& $(this).val() != null &&
					$(this).val() != ""){
        			
					if($("#secondFilter").css("display") === "none"){
						$("#addFilter").show();
						$("#addFilter").css("display","inline-block");
						$("#addFilter").removeAttr("disabled");
					} 
					$("#ask").removeAttr("disabled");
				}
        	};
        	firstFilterObjects.each(function(index,value){
        		if($(value).is("select")){
        			$(value).change(handleObjectChangeEvent);		
        		}
        		if($(value).is("input")){
        			$(value).keydown(handleObjectChangeEvent);	
        		}
        	});
        }
    	
    });
})(jQuery);