//TODO: try to pass more ids etc through tapestry.
(function($){
	
    /** Container of functions that may be invoked by the Tapestry.init() function. */
    $.extend(Tapestry.Initializer, {
    	
    	/**
    	 * Initializer that handles the first predicate.
    	 */
    	startingPredicate: function(specs){
    		
    		var handleStartingPredicateChange = function(data){
    			$.question.utils.populateSelectInFilter($("#subject"),data.subjects);
    			$("#addFilter").removeAttr("disabled");
    		};
    		
    		$("#startingPredicate").change(function(){
	    		/**
	        	 * Ajax request when adding the first filter.
	        	 */
	        	var ajaxRequest = {
	                	url : specs.url,
	                	success : handleStartingPredicateChange, 
	                	data : 	{startingPredicate : $('#startingPredicate').val()},
	                    type : "GET"
	            };   
	        	
	        	$.ajax(ajaxRequest);
    		});
    	},
    
    	 /**
    	  * Initializer that handles the add filter button.
    	  * 
    	  * @param specs.id = the ID of the addFilter button
    	  * 
    	  */
         addFilter: function(specs){
		
        	/**
        	 * Show a filter.
        	 * 
        	 * @param filterSelector - the selector which identifies the given filter.
        	 * @param data
        	 * @returns
        	 */
        	var showFilter = function(filterSelector,data){
        		//Show the filter.
        		$(filterSelector).show();
        		$(filterSelector).css("display","inline-block");
        		
        		//Hide all object containers and disable the fields.
        		$.question.utils.hideFormFragment($(filterSelector).find(".objectContainer"));
        		
        		//Show the freetext object container (the default)
        		$.question.utils.showFormFragment($(filterSelector).find(".freetextObjectEditor"));
        		
        		//Make the object part readonly (since the predicate has to be changed first).
    			$.question.utils.makeReadOnly($(filterSelector).find(".object"));
    			
    			//Populate the select element containing the predicates.
    			$.question.utils.populateSelectInFilter($(filterSelector+' .predicate'),data.predicates);
    			
    			//Make the subject and startingPredicate readonly (since changing it now would have a knock on effect)
    			$.question.utils.makeReadOnly($("#subject"));
    			$.question.utils.makeReadOnly($("#startingPredicate"));
    			
    			//Now we have a filter, we can remove it, so show the remove container.
    			$(".removeFilterContainer").show();
    			$(".removeFilterContainer").css("display","inline-block")

    			//Disable the ask button, since we can't ask until we have completed this filter.
    			$("#ask").attr("disabled","disabled"); 
    			
    			//Disable the add filter button, since we can't add another until we've comlpeted this one.
    			$("#addFilter").attr("disabled","disabled");
        	}
    
        	/**
    		 * Success method which handles adding the first filter.
    		 * 
    		 * @param data containing a list of predicates.
    		 * @returns
    		 */
    		var handleAddFirstFilter = function(data){
    			//show the first filter
    			showFilter("#firstFilter", data);
    		}
    		
    		/**
    		 * Success method which handles adding the second filter.
    		 * 
    		 * @param data containing a list of predicates.
    		 * @returns
    		 */
    		var handleAddSecondFilter = function(data){
    			//show the second filter
    			showFilter("#secondFilter", data);
    			
    			//it's the last filter, so we can't add any more.
    			$("#addFilter").hide();
    			
    			//Make all the elements in the first filter readonly, since changing it would have a knock on effect.
    			$("#firstFilter").find(":input:not(.removeFilter)").each(function(key,value){
    					$.question.utils.makeReadOnly($(value));
    				});
    		}    	
        	
    		/**
    		 * Handle clicks on the add button.
    		 */
            $("#" + specs.id).click(function(){
            	
            	/**
            	 * Ajax request when adding the first filter.
            	 */
            	var firstFilterAjaxRequest = {
                    	url : specs.firstFilterUrl,
                    	success : handleAddFirstFilter, 
                    	data : 	{subject : $('#subject').val()},
                        type : "GET"
                };   
            	
            	/**
            	 * Ajax request when adding the second filter.
            	 */
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
            	
            	//choose a request depending on whether the firstFilter is visible or not.
            	var ajaxRequest = $("#firstFilter").css("display") !== "none" ? secondFilterAjaxRequest : firstFilterAjaxRequest;
            	
            	//send the ajax request, to be handled by the methods above.
            	$.ajax(ajaxRequest);
            });
        },
        
        /**
         * Initializer which handles changes to predicates
         * 
         * Also handles the remove button as a sideline.
         * 
         * @param specs.firstFilterUrl the URL to send predicate change for the firstFilterURL to.
         * @param specs.secondFilterUrl the URL to send predicate change events for the secondFilterURL to.
         */
        filters : function(specs){      
        	
        	/**
        	 * Success handler for changes to the predicate field.
        	 * 
        	 * @param data - data in the form: {{editor:"myEditorClass","objects":[{label,value},{label,value}]}
        	 * @param filterSelector - the selector of the filter whose has changed.
        	 */
         	var handlePredicateChange = function(data,filterSelector){
         		
         		//hide all object editor/containers.
         		$.question.utils.hideFormFragment($(filterSelector).find(".objectContainer"));
         				
         		//show the appropriate one for this predicate.
         		var objectEditor = $(filterSelector+" .objectContainer."+data.editorClass);
         		$.question.utils.showFormFragment(objectEditor);
        		
         		//make the object editor readable.
        		var field = objectEditor.find(":input:not(input[type=hidden])").css("display","inline-block");
        		$.question.utils.makeReadable(field);
        		
        		//populate the object editor with values from the response.
        		if(field.is("select")){
        			$.question.utils.populateSelectInFilter(field, data.objects);
        		}
        	};
         	
        	/**
        	 * Calls handlePredicateChange with the first filter selector.
        	 * 
        	 * @param data - the data from the response.
        	 */
         	var handleFirstFilterPredicateChange = function(data){
         		handlePredicateChange(data,"#firstFilter");
         	};
         	
         	/**
        	 * Calls handlePredicateChange with the second filter selector.
        	 * 
        	 * @param data - the data from the response.
        	 */
         	var handleSecondFilterPredicateChange = function(data){
         		handlePredicateChange(data,"#secondFilter");
         	};
        	
        	/**
        	 * Handle the change event on the first filter predicate field.
        	 */
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
        	
        	/**
        	 * Handle the change event on the second filter predicate field.
        	 */
        	var secondFilterPredicate = $("#secondFilter").find(".predicate");
        	secondFilterPredicate.change(function(){
        		
        		//Required for populating potential objects.
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
        	

        	/**
        	 * Handler for changes to the object filter.
        	 */
        	var handleObjectChangeEvent = function(){
        		if(typeof($(this).val()) != 'undefined' 
					&& $(this).val() != null &&
					$(this).val() != ""){
        			
        			//If it is the first filter.
					if($("#secondFilter").css("display") === "none"){
						$("#addFilter").show();
						$("#addFilter").css("display","inline-block");
						$("#addFilter").removeAttr("disabled");
					} 
					//we've filled in the object and completed the filter, allow the user to ask.
					$("#ask").removeAttr("disabled");
				}
        	}
        	
        	/**
        	 * Listen to change or keydown event on the object filter.
        	 */
        	var filterObjects = $(".filtersContainer").find(".object");
        	filterObjects.each(function(index,value){
        		if($(value).is("select")){
        			$(value).change(handleObjectChangeEvent);		
        		}
        		if($(value).is("input")){
        			$(value).keydown(handleObjectChangeEvent);	
        		}
        	});
        	
        	/**
        	 * Handle clicks on the remove filter.
        	 */
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
        			$.question.utils.makeReadable($(".startingPredicate"));
        			$(".removeFilterContainer").hide();
        			$("#ask").removeAttr("disabled");
        		}
        		$("#addFilter").removeAttr("disabled");
        		filterToRemove.hide();
        		filterToRemove.find(".objectContainer").hide();
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
           	makeReadable: function(elements){
           		elements.each(function(key,value){
           			var element = $(value);
	         		if(!element.is("select")){
	         			element.removeAttr("readonly");
	         			return;
	         		}	
	         		element.removeAttr("disabled");
	         		element.siblings("input[type=hidden][name="+element.attr("name")+"]").remove();
           		});
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
        	},
         	
        	/**
         	 * Disables, hides, and clears out all form elements in the container. 
         	 * This should be managed by tapestry formFragments, but doesn't seem to work as expected.
         	 */
         	hideFormFragment : function(elements){
         		elements.each(function(key,value){
         			var element = $(value);
	         		var inputs = element.find(':input');
	         		inputs.attr("disabled","disabled");
	         		inputs.val("");
	         		inputs.hide();
         		});
         	},
        	
        	/**
        	 * Does the opposite of hide.
        	 */
        	showFormFragment : function(elements){
        		elements.each(function(key,value){
        			var element = $(value);
	        		element.find(':input').removeAttr("disabled");
	        		element.find(':input').show();
	        		element.css("display","inline-block");
        		});
        	}
           	
    	}	
    }
})(jQuery);
