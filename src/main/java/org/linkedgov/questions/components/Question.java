package org.linkedgov.questions.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linkedgov.questions.model.Query;
import org.linkedgov.questions.model.QuestionType;
import org.linkedgov.questions.services.StaticDataService;

/**
 * 
 * Luke Wilson-Mawer <a href="http://viscri.co.uk">Viscri</a> for LinkedGov
 *
 */
public class Question {

	private Query query;
	
	@Persist
	@SuppressWarnings("unused")
	@Property
	private List<String> subjects;
	
	@Persist
	@SuppressWarnings("unused")
	@Property
	private List<String> predicates;
	
	@Persist
	@SuppressWarnings("unused")
	@Property
	private List<String> objects;
	
	//XXX: correct?
	@Persist
	private String subjectSoFar;
	
	@Inject
	private StaticDataService staticDataService;
	
	@Inject
	private Block freetextObjectBlock;
	
	@Inject
	private Block locationObjectBlock;
	
	@Inject
	private Block selectObjectBlock;
	
	@Inject
	private Block noObjectsBlock;
	
	@Persist
	@Property
	private Block objectBlock;
	
	@InjectComponent
	private Zone subjectZone; 
	
	@InjectComponent
	private Zone predicateZone;
	
	@InjectComponent
	private Zone objectZone;
	
	@SuppressWarnings("unused")
	@SetupRender
	private void setup(){
		subjects = new ArrayList<String>();
		predicates = new ArrayList<String>();
		objects = new ArrayList<String>();
	}
	
	//TODO: label/uri pairs
	@OnEvent(value=EventConstants.VALUE_CHANGED, component="questionType")
	public Object onQuestionTypeChanged(QuestionType questionType){
		subjects =  staticDataService.getClasses();
		return subjectZone.getBody();
	}
	
	@OnEvent(value=EventConstants.VALUE_CHANGED, component="subject")
	public Object getPredicateZone(String subject){
		subjectSoFar = subject;
		predicates = staticDataService.getPredicates(subject);
		return predicateZone.getBody();
	}
	
	@OnEvent(value=EventConstants.VALUE_CHANGED, component="predicate")
	public Object getObjectZone(String predicate){
		
		objects = staticDataService.getObjects(subjectSoFar, predicate);
		
		if(objects == null){
			return noObjectsBlock;
		} else if(isLocationPredicate(predicate)){
			objectBlock = locationObjectBlock;
		} else if(objects.size() < 20){
			objectBlock = selectObjectBlock;
		} else {
			objectBlock = freetextObjectBlock;
		}
		
		return objectZone.getBody();
	}

	//TODO: make this more generic and implement it properly; 
	public boolean isLocationPredicate(String predicate){
		return predicate.contains("location");
	}
	
	public void setQuery(Query query) {
		this.query = query;
	}

	public Query getQuery() {
		if(this.query == null){
			this.query = new Query();
		}
		return this.query;
	}
}
