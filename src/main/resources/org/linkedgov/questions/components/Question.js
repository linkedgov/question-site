//TODO: try to pass more ids etc through tapestry. This should be tidied up before it gets too hard to understand.
(function($){
	
    /** Container of functions that may be invoked by the Tapestry.init() function. */
    $.extend(Tapestry.Initializer, {
         addFilter: function(specs){
		
        	var showFilter = function(filterSelector,data){
        		$(filterSelector).show();
    			$(filterSelector).css("display","inline-block");
    			var object = $(filterSelector).find(".object");
    			$.question.utils.makeReadOnly(object);
    			$.question.utils.populateSelectInFilter($(filterSelector+' .predicate'),data.predicates);
    			$.question.utils.makeReadOnly($("#ask"));
    			$.question.utils.makeReadOnly($("#subject"));
    			$(".removeFilterContainer").formFragment().show();
    			$(".removeFilterContainer").css("display","inline-block");
        	}
    
    		var handleAddFirstFilter = function(data){
    			showFilter("#firstFilter", data);
    			$.question.utils.makeReadOnly($("#addFilter"));
    		}
    		
    		var handleAddSecondFilter = function(data){
    			showFilter("#secondFilter", data);
    			//it's the last filter, so hide the add button.
    			$("#addFilter").hide();
    			$("#firstFilter").find(":input:not(.removeFilter)").each(function(key,value){
    					$.question.utils.makeReadOnly($(value));
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
        	
         	var handlePredicateChange = function(data,filterSelector){
         		
         		$(filterSelector).find(".objectContainer").formFragment().hide();
         		
         		var objectEditor = $(filterSelector+" .objectContainer."+data.editorClass);
         		objectEditor.formFragment().show();
         		objectEditor.css("display","inline-block");
        		
        		var field = objectEditor.find(":input:not(input[type=hidden])").css("display","inline-block");
        		$.question.utils.makeReadable(field);
        		if(field.is("select")){
        			$.question.utils.populateSelectInFilter(field, data.objects);
        		}
        	};
         	
         	var handleFirstFilterPredicateChange = function(data){
         		handlePredicateChange(data,"#firstFilter");
         	};
         	
         	var handleSecondFilterPredicateChange = function(data){
         		handlePredicateChange(data,"#secondFilter");
         	};

        	var firstFilterPredicate = $("#firstFilter").find(".predicate");
        	firstFilterPredicate.change(function(){
        		
        		var ajaxRequest = {
                    	url : specs.firstFilterUrl,
                    	success : handleFirstFilterPredicateChange, 
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
						$.question.utils.makeReadable($("#addFilter"));
					} 
					$.question.utils.makeReadable($("#ask"));
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
        	
        	$(".removeFilter").click(function(){
        		var secondFilter = $("#secondFilter").css("display") !== "none" ;
        		var filterToRemove;
        		if(secondFilter){
        			filterToRemove = $("#secondFilter");
        			$.question.utils.makeReadable($("#firstFilter").find(":input"));
        			$("#addFilter").show();
        			$("#addFilter").css("display","inline-block");
        		} else {
        			filterToRemove = $("#firstFilter");
        			$.question.utils.makeReadable($(".subject"));
        			$(".removeFilterContainer").formFragment().hide();
        			$.question.utils.makeReadable($("#ask"));
        		}
        		$.question.utils.makeReadable($("#addFilter"));
        		filterToRemove.hide();
        		filterToRemove.find(".objectContainer").formFragment().hide();
        		filterToRemove.find(":input:not(.tapestry-formfragment)").val("");
        		filterToRemove.find("select").empty();  
        	});        	
        }    	
    });
    
    /**
     * Static functions for the question component.
     */
    $.question = {
    	utils: {
    		/**
    		 * Makes an form element (input, textarea, select, button, etc.) readonly, which means
    		 * the value is still submitted but the field is not changeable by the user.
    		 * 
    		 * For a select element, this means creating a hidden input with the select's value
    		 * and making the select disabled.
    		 */
           	makeReadOnly: function(elements){
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
           	},
           	
           	/**
           	 * Undoes $.question.utils.makeFieldReadOnly.
           	 */
           	makeReadable: function(element){
         		if(!element.is("select")){
         			element.removeAttr("readonly");
         			return;
         		}	
         		element.removeAttr("disabled");
         		element.siblings("input[type=hidden][name="+element.attr("name")+"]").remove();
         	},
         	
         	/**
         	 * Empty the select element and populate the options in it
         	 * 
         	 * @param selectElem whose options you want to populate.
         	 * @param options of this form: [{label : myLabel, value: myValue}]
         	 */
        	populateSelectInFilter : function(selectElem, options){
        		if(options.length == 0){
        			return;
        		}
        		selectElem.empty();	
        	    for(var i = 0; i < options.length; i++){
        	    	var option = options[i];
        	    	selectElem.append("<option value='"+option.value+"'>"+option.label+"</option>");
        	    } 
        	}
           	
    	}	
    }
})(jQuery);
