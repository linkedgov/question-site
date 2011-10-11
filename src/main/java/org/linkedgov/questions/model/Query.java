package org.linkedgov.questions.model;

/**
 * Pojo that represents a query, or question, built up by the user. 
 * 
 * Luke Wilson-Mawer <a href="http://viscri.co.uk">Viscri</a> for LinkedGov
 *
 */
public class Query {

	private QuestionType questionType = QuestionType.SELECT;
	
	private String subject;

	private QueryFilter firstFilter = new QueryFilter();
	
	private QueryFilter secondFilter = new QueryFilter();
	
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

	public void setSecondFilter(QueryFilter secondFilter) {
		this.secondFilter = secondFilter;
	}

	public QueryFilter getSecondFilter() {
		return secondFilter;
	}

	public void setFirstFilter(QueryFilter firstFilter) {
		this.firstFilter = firstFilter;
	}

	public QueryFilter getFirstFilter() {
		return firstFilter;
	}
	
	//TODO Mischa
	public String toSparqlString() {
		return "lame";
	}
	
}
