package org.linkedgov.questions.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linkedgov.questions.model.QueryFilter;

/**
 * TODO: move more JS from Question.js to here and hardcode fewer ids.
 * 
 * Component that represents a predicate/object pair that helps narrow down the question.
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> for LinkedGov
 *
 */
public class Filter {

	/**
	 * The value of the predicate (the first dropdown) of this filter.
	 */
	@Property
	@Persist
	private String predicate;

	/**
	 * The object value from the select element.
	 */
	@Property
	@Persist
	private String selectObject;

	/**
	 * The object value entered via free text.
	 */
	@Property
	@Persist
	private String freeTextObject;

	/**
	 * The object value entered via the location field.
	 */
	@Property
	@Persist
	private String locationObject;
	
	/**
	 * Empty list to act as a model for select elements.
	 */
    @SuppressWarnings("unused")
	@Property
    private List<String> emptyList;
    
    /**
     * Bog standard Tapestry stuff.
     */
    @Inject
    private ComponentResources resources;
    
    /**
     * Setup the empty list.
     */
    @SetupRender
    public void setup(){
    	emptyList = new ArrayList<String>();
    }
    
    /**
     * The ID attribute of this filter.
     */
    public String getId(){    	
    	return resources.getId();
    }
    
    /**
     * 
     * @return the query filter that has been entered.
     */
    public QueryFilter getFilter(){
    	final QueryFilter filter = new QueryFilter();
    	filter.setPredicate(predicate);
    	filter.setObject(getObject());
		return filter;
    }
    
    /**
     * Checks all the different object fields and returns the one that isn't null, or null if all object fields are null.
     *  
     * @return the object, or null if there isn't one.
     */
    private String getObject(){
    	if(selectObject != null){
			return selectObject;
		} else if(freeTextObject != null){
			return freeTextObject;		
		} else if(locationObject != null){
			return locationObject;
		} else {
			return null;
		}
    }	
}
