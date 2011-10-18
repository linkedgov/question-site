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
 * Component that represents a predicate/object pair that helps narrow down the question.
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> for LinkedGov
 *
 */
public class Filter {

	@Property
	@Persist
	private String predicate;

	@Property
	@Persist
	private String selectObject;

	@Property
	@Persist
	private String freeTextObject;

	@Property
	@Persist
	private String locationObject;
	
    @SuppressWarnings("unused")
	@Property
    private List<String> emptyList;
    
    @Inject
    private ComponentResources resources;
    
    @SetupRender
    public void setup(){
    	emptyList = new ArrayList<String>();
    }
    
    public String getId(){    	
    	return resources.getId();
    }
    
    public QueryFilter getFilter(){
    	final QueryFilter filter = new QueryFilter();
    	filter.setPredicate(predicate);
    	filter.setObject(getObject());
		return filter;
    }
    
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
