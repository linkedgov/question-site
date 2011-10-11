package org.linkedgov.questions.services.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
			"{?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?class}"; 
	
	private static final String CLASS_VARIABLE = "class";
	
	private final List<String> classes = new CopyOnWriteArrayList<String>();
	
	private final SparqlDao sparqlDao;
	
	public StaticDataServiceRDF (SparqlDao sparqlDao){
		this.sparqlDao = sparqlDao; 
	}
	
	public List<String> getClasses(){	
		return classes.isEmpty() ? queryForClasses() : classes;
	}
	
	public List<String> getObjects(String subject, String predicate){
		return null;
	}
	
	public List<String> getPredicates(String subject) {
		// TODO Auto-generated method stub
		return null;
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
