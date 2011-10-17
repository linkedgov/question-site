//TODO: try to pass more ids etc through tapestry. This should be tidied up before it gets too hard to understand.
(function($){
	
    /** Container of functions that may be invoked by the Tapestry.init() function. */
    $.extend(Tapestry.Initializer, {
         addFilter: function(specs){
    			
           	//TODO: remove this and put it into a utility method.
           	var makeReadOnly = function(elements){
           		elements.each(function(key,value){
           			var element = $(value);
   	        		if(!element.is("select")){
   	        			element.attr("readonly","readonly");
   	        			return;
   	        		}	
   	        		if(element.siblings("input[type=hidden][name="+element.attr("name")+"]").size() == 0){
   	        			element.after("<input name="+element.attr("name")+" type='hidden' value='"+element.val()+"'>");		
   	        			element.attr("disabled","disabled");
   	        		}
           		});
           	}
         	
         	var makeReadable = function(element){
         		if(!element.is("select")){
         			element.removeAttr("readonly");
         			return;
         		}	
         		element.removeAttr("disabled");
         		element.siblings("input[type=hidden][name="+element.attr("name")+"]").remove();
         	}	
        	
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
    			makeReadOnly(object);
    			populatePredicatesInFilter($(filterSelector),data.predicates);
    			makeReadOnly($("#ask"));
    			makeReadOnly($("#subject"));
    			$(".removeFilterContainer").formFragment().show();
    			$(".removeFilterContainer").css("display","inline-block");
        	}
    
    		var handleAddFirstFilter = function(data){
    			showFilter("#firstFilter", data);
    			makeReadOnly($("#addFilter"));
    		}
    		
    		var handleAddSecondFilter = function(data){
    			showFilter("#secondFilter", data);
    			//it's the last filter, so hide the add button.
    			$("#addFilter").hide();
    			$("#firstFilter").find(":input:not(.removeFilter)").each(function(key,value){
    					makeReadOnly($(value));
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
                    	url : specs.secondFilterUrl,
                    	success : handleAddSecondFilter, 
                    	data : 	{
                    				subject : $('#subject').val(), 
                    				predicate : $('#firstFilter').find('.predicate').val(), 
                    				object : $('#firstFilter').find('.objectContainer:visible .object').val()
                    			},
                        type : "GET"
                };   
            	
            	var ajaxRequest = $("#firstFilter").css("display") !== "none" ? secondFilterAjaxRequest : firstFilterAjaxRequest;
            	
            	$.ajax(ajaxRequest);
            });
        },
        
        filters : function(specs){      
        	
         	//TODO: remove this and put it into a utility method.
           	var makeReadOnly = function(elements){
           		elements.each(function(key,value){
           			var element = $(value);
   	        		if(!element.is("select")){
   	        			element.attr("readonly","readonly");
   	        			return;
   	        		}	
   	        		if(element.siblings("input[type=hidden][name="+element.attr("name")+"]").size() == 0){
   	        			element.after("<input name="+element.attr("name")+" type='hidden' value='"+element.val()+"'>");		
   	        			element.attr("disabled","disabled");
   	        		}
           		});
           	}
        	
        	var makeReadable = function(element){
        		if(!element.is("select")){
        			element.removeAttr("readonly");
        			return;
        		}	
        		element.removeAttr("disabled");
        		element.siblings("input[type=hidden][name="+element.attr("name")+"]").remove();
        	}	
        	
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
        	
        	//TODO, merge the two functions below - they are the same apart from the id of the filter
         	var handlePredicateChange = function(data){
         		
         		$("#firstFilter").find(".objectContainer").formFragment().hide();
        		var objectEditor = $("#firstFilter").find(".objectContainer#"+data.editorId);
        		objectEditor.formFragment().show();
        		objectEditor.css("display","inline-block");
        		
        		var field = objectEditor.find(":input");
        		objectEditor.formFragment().show();
        		field.css("display","inline-block");
        		makeReadable(field);
        		if(field.is("select")){
        			populateSelectInFilter(field, data.objects);
        		}
        	}
        	
        	var handleSecondFilterPredicateChange = function(data){
         		
         		$("#secondFilter").find(".objectContainer").formFragment().hide();
        		var objectEditor = $("#secondFilter").find(".objectContainer#"+data.editorId);
        		objectEditor.formFragment().show();
        		objectEditor.css("display","inline-block");
        		
        		var field = objectEditor.find(":input");
        		objectEditor.formFragment().show();
        		field.css("display","inline-block");
        		makeReadable(field);
        		if(field.is("select")){
        			populateSelectInFilter(field, data.objects);
        		}
        		
        	}
        	
        	var firstFilterPredicate = $("#firstFilter").find(".predicate");
        	firstFilterPredicate.change(function(){
        		
        		var ajaxRequest = {
                    	url : specs.firstFilterUrl,
                    	success : handlePredicateChange, 
                    	data : 	{
                    				subject: $('#subject').val(),
                    				predicate: firstFilterPredicate.val()
                    			},
                        type : "GET"
                };
        		
        		$.ajax(ajaxRequest);
        	});
        	
        	var secondFilterPredicate = $("#secondFilter").find(".predicate");
        	secondFilterPredicate.change(function(){
        		
        		var firstFilterPredicate = $("#firstFilter").find(".predicate").val();
            	var firstFilterObject = $("#firstFilter").find(".objectContainer:visible .object").val();
            			
            	var ajaxRequest = {
                       	url : specs.secondFilterUrl,
                       	success : handleSecondFilterPredicateChange, 
                       	data : 	{
                       				subject : $('#subject').val(),
                       				predicate : secondFilterPredicate.val(),
                       				firstFilterPredicate : firstFilterPredicate,
            						firstFilterObject : firstFilterObject
                       			},
                        type : "GET"
                };
            		
            	$.ajax(ajaxRequest);
        	});
        	
        	var filterObjects = $(".filtersContainer").find(".object");
        	var handleObjectChangeEvent = function(){
        		if(typeof($(this).val()) != 'undefined' 
					&& $(this).val() != null &&
					$(this).val() != ""){
        			
					if($("#secondFilter").css("display") === "none"){
						$("#addFilter").show();
						$("#addFilter").css("display","inline-block");
						makeReadable($("#addFilter"));
					} 
					makeReadable($("#ask"));
				}
        	}
        	
        	filterObjects.each(function(index,value){
        		if($(value).is("select")){
        			$(value).change(handleObjectChangeEvent);		
        		}
        		if($(value).is("input")){
        			$(value).keydown(handleObjectChangeEvent);	
        		}
        	});
        	
        	//TODO tidy this up since it doesn't look like this belongs here
        	$(".removeFilter").click(function(){
        		var secondFilter = $("#secondFilter").css("display") !== "none" ;
        		var filterToRemove;
        		if(secondFilter){
        			filterToRemove = $("#secondFilter");
        			makeReadable($("#firstFilter").find(":input"));
        			$("#addFilter").show();
        			$("#addFilter").css("display","inline-block");
        		} else {
        			filterToRemove = $("#firstFilter");
        			makeReadable($(".subject"));
        			$(".removeFilterContainer").formFragment().hide();
        			makeReadable($("#ask"));
        		}
       			makeReadable($("#addFilter"));
        		filterToRemove.formFragment().hide();
        		filterToRemove.find(":input").val("");
        		filterToRemove.find("select").empty();  
        	});        	
        }    	
    });
})(jQuery);
