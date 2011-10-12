package org.linkedgov.questions.model;

/**
 * Pojo that represents a query, or question, built up by the user. 
 * 
 * Luke Wilson-Mawer <a href="http://viscri.co.uk">Viscri</a> for LinkedGov
 *
 */
public class QueryFilter {

	private String predicate;
	
	private String object;

	public void setObject(String object) {
		this.object = object;
	}

	public String getObject() {
		return object;
	}

	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}

	public String getPredicate() {
		return predicate;
	}

}
