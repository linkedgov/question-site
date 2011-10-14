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
	
	public boolean isNull() {
		if (subject == null) {
			return true;
		}
		
		return false;
	}
	
	public String toSparqlString() {
		StringBuilder query = new StringBuilder();
			
		if (questionType.equals(QuestionType.COUNT)) {
			query.append("SELECT (COUNT(?sub) AS ?cnt) ");
		} else {
			query.append("SELECT DISTINCT ?sub ?pred ?obj ");
		}
		
		query.append("WHERE { ");
		
		query.append("?sub <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <");
		query.append(subject);
		query.append("> . ");
		
		query.append("?sub ?pred ?obj . ");
    	
	    if (!firstFilter.isNull()) {
	    	query.append("?sub <");
	    	query.append(firstFilter.getPredicate());
	    	query.append("> <");
	    	query.append(firstFilter.getObject());
	    	query.append("> . ");
	    }
	    
	    if (!secondFilter.isNull()) {
	    	query.append("?sub <");
	    	query.append(secondFilter.getPredicate());
	    	query.append("> <");
	    	query.append(secondFilter.getObject());
	    	query.append("> . ");
	    }
		
	    query.append("} ");
	    
		return query.toString();
	}
	
}
