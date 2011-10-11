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
 * TODO: major question: make this all work as one form and then do validation etc, 
 * or carry on as now and populate the query object piecemeal whenever events happen on the selects.
 * 
 * Luke Wilson-Mawer <a href="http://viscri.co.uk">Viscri</a> for LinkedGov
 *
 */
public class Question {

	@Persist
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
	@Property
	private List<String> objects;
	
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
		if(query != null){
			query = new Query();
		}
		subjects =  staticDataService.getClasses();
		predicates = new ArrayList<String>();
		objects = new ArrayList<String>();
	}
	
	@OnEvent(value=EventConstants.VALUE_CHANGED, component="subject")
	public void updateQuestionType(QuestionType questionType){
		query.setQuestionType(questionType);
	}
	
	@OnEvent(value=EventConstants.VALUE_CHANGED, component="subject")
	public Object getPredicateZone(String subject){
		query.setSubject(subject);
		predicates = staticDataService.getPredicates(subject);
		return predicateZone.getBody();
	}
	
	@OnEvent(value=EventConstants.VALUE_CHANGED, component="predicate")
	public Object getObjectZone(String predicate){
		query.getFirstFilter().setPredicate(predicate);
		objects = staticDataService.getObjects(query.getSubject(), predicate);
		
		if(objects.isEmpty()){
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
	
	@OnEvent(value=EventConstants.VALUE_CHANGED, component="object")
	public void handleFirstFilterObj(String object){
		query.getFirstFilter().setObject(object);
	}
	
	//TODO: make this more generic and implement it properly; 
	public boolean isLocationPredicate(String predicate){
		return predicate.contains("location");
	}
	
	@OnEvent("askQuestion")
	public Object askQuestion(){
		//TODO: put grid stuff in here.
		return null;
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
