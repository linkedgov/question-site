package org.linkedgov.questions.model;

/**
 * Pojo that represents a query, or question, built up by the user. 
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> and 
 * @author <a href="http://mmt.me.uk/foaf.rdf#mischa">Mischa Tuffield</a> for LinkedGov
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
	
	//TODO Mischa, turn this into a real sparql query that represents the query object.
	public String toSparqlString() {
		if(questionType.equals(QuestionType.COUNT)){
			return "SELECT COUNT * WHERE {?x ?y ?z} LIMIT 150";
		}else if(questionType.equals(QuestionType.SELECT)){
			return "SELECT DISTINCT ?s ?o WHERE {?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?o} LIMIT 150";
		}
		return subject;
	}
	
}
