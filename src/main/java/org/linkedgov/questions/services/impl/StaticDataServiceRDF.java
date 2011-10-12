package org.linkedgov.questions.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.linkedgov.questions.model.QueryFilter;
import org.linkedgov.questions.services.SparqlDao;
import org.linkedgov.questions.services.StaticDataService;

import uk.me.mmt.sprotocol.SelectResult;
import uk.me.mmt.sprotocol.SelectResultSet;
import uk.me.mmt.sprotocol.SparqlResource;

/**
 * TODO: decide on what stuff to cache and what not to cache here.
 * 
 * Luke Wilson-Mawer <a href="http://viscri.co.uk">Viscri</a> for LinkedGov
 *
 */
public class StaticDataServiceRDF implements StaticDataService {
		
	private static final String GET_CLASSES_QUERY = "SELECT DISTINCT ?class WHERE " +
			"{?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?class} ORDER BY ?class"; 
	
	private static final String GET_PREDICATE_QUERY = "SELECT DISTINCT ?pred WHERE " +
			"{?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <%s> ; " +
			"?pred ?o . " +
			"FILTER (?pred != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>) } ORDER BY ?class";
		
	private static final String GET_OBJECTS_QUERY = "SELECT DISTINCT ?object WHERE " +
			"{?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <%s> ; " +
			"<%s> ?object} ORDER BY ?object";
	
	private static final String CLASS_VARIABLE = "class";
	private static final String PREDICATE_VARIABLE = "pred";
	private static final String OBJECT_VARIABLE = "object";

	private final List<String> classes = new CopyOnWriteArrayList<String>();
		
	private final SparqlDao sparqlDao;
	
	public StaticDataServiceRDF (SparqlDao sparqlDao){
		this.sparqlDao = sparqlDao; 
	}
	
	public List<String> getClasses(){	
		return classes.isEmpty() ? queryForClasses() : classes;
	}
	
	public List<String> getObjects(String subject, String predicate){
		String query = String.format(GET_OBJECTS_QUERY, subject, predicate);
		List<String> retValues = new ArrayList<String>();
		final SelectResultSet results = sparqlDao.executeSelect(query);		
		for (SelectResult result : results.getResults()) {
			final SparqlResource element = result.getResult().get(OBJECT_VARIABLE);
			retValues.add(element.getValue());
		}
		return retValues;
	}
	
	//TODO Mischa this is for Filter B
	public List<String> getObjects(String subject, String predicate, QueryFilter filter) {
		return getObjects(subject, predicate);
	}
	
	//TOOD Mischa this is for Filter B
	public List<String> getPredicates(String subject, QueryFilter filter) {
		return getPredicates(subject);
	}
	
	public List<String> getPredicates(String subject) {
		String query = String.format(GET_PREDICATE_QUERY, subject);
		List<String> retValues = new ArrayList<String>();
		final SelectResultSet results = sparqlDao.executeSelect(query);		
		for (SelectResult result : results.getResults()) {
			final SparqlResource element = result.getResult().get(PREDICATE_VARIABLE);
				retValues.add(element.getValue());
		}
		return retValues;
	}

	private List<String> queryForClasses() {
		final SelectResultSet results = sparqlDao.executeSelect(GET_CLASSES_QUERY);		
		for (SelectResult result : results.getResults()) {
			final SparqlResource element = result.getResult().get(CLASS_VARIABLE);
			classes.add(element.getValue());
		}
		return classes;
	}
	
}
