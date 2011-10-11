package org.linkedgov.questions.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Pojo that represents a query, or question, built up by the user. 
 * 
 * Luke Wilson-Mawer <a href="http://viscri.co.uk">Viscri</a> for LinkedGov
 *
 */
public class Query {

	private QuestionType questionType = QuestionType.SELECT;
	
	private String subject;

	private List<QueryFilter> filters = new ArrayList<QueryFilter>(); 
	
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSubject() {
		return subject;
	}

	public void setQuestionType(QuestionType questionType) {
		this.questionType = questionType;
	}

	public QuestionType getQuestionType() {
		return questionType;
	}

	public void setFilters(List<QueryFilter> filters) {
		this.filters = filters;
	}

	public List<QueryFilter> getFilters() {
		return filters;
	}
	
}
