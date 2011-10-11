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
	
	@SuppressWarnings("unused")
	@Property
	private List<String> subjects;
	
	@SuppressWarnings("unused")
	@Property
	private List<String> predicates;
	
	@SuppressWarnings("unused")
	@Property
	private List<String> objects;
	
	//XXX: correct?
	@Persist
	private String subjectSoFar;
	
	@Inject
	private StaticDataService staticDataService;
	
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
	public Block getSubjectZone(QuestionType questionType){
		subjects =  staticDataService.getClasses();
		return subjectZone.getBody();
	}
	
	@OnEvent(value=EventConstants.VALUE_CHANGED, component="subject")
	public Block getPredicateZone(String subject){
		subjectSoFar = subject;
		predicates = staticDataService.getPredicates(subject);
		return predicateZone.getBody();
	}
	
	@OnEvent(value=EventConstants.VALUE_CHANGED, component="predicate")
	public Block getObjectZone(String predicate){
		objects = staticDataService.getObjects(subjectSoFar, predicate);
		return objectZone.getBody();
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
