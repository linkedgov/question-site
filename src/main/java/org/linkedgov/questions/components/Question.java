package org.linkedgov.questions.components;

import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.linkedgov.questions.model.Query;

/**
 * 
 * Luke Wilson-Mawer <a href="http://viscri.co.uk">Viscri</a> for LinkedGov
 *
 */
public class Question {

	private Query query;
	
	
	@OnEvent("questionType")
	public Zone handleQuestionTypeEvent(){
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
