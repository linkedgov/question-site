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
	
	private String predicate;
	
	private String object;
	
	private String location;

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocation() {
		return location;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getObject() {
		return object;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSubject() {
		return subject;
	}

	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}

	public String getPredicate() {
		return predicate;
	}

	public void setQuestionType(QuestionType questionType) {
		this.questionType = questionType;
	}

	public QuestionType getQuestionType() {
		return questionType;
	}
	
}
