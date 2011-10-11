package org.linkedgov.questions.components;

import java.util.List;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.OnEvent;
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
	
	private List<String> classes;
	
	@Inject
	private StaticDataService staticDataService;
	
	//TODO: label/uri pairs
	@OnEvent(value=EventConstants.VALUE_CHANGED, component="questionType")
	public Object getSubjectModel(QuestionType questionType){
		return staticDataService.getClasses();
	}
	
	@OnEvent(value=EventConstants.VALUE_CHANGED, component="subject")
	public Object getPredicateModel(String rdfClass){
		return classes;
	}
	
	@OnEvent(value=EventConstants.VALUE_CHANGED, component="predicate")
	public Object getObjectZone(String rdfClass){
		return classes;
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
