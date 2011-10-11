package org.linkedgov.questions.services.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.linkedgov.questions.services.CachedDataService;
import org.linkedgov.questions.services.SparqlDao;

import uk.me.mmt.sprotocol.SelectResult;
import uk.me.mmt.sprotocol.SelectResultSet;
import uk.me.mmt.sprotocol.SparqlElement;

/**
 * 
 * Luke Wilson-Mawer <a href="http://viscri.co.uk">Viscri</a> for LinkedGov
 *
 */
public class CachedDataServiceRDF implements CachedDataService {
	
	//TODO: add in some sparql to get all the classes/things.
	private static final String GET_CLASSES_QUERY = "SELECT ?x ?y ?z"; 
	
	private static final String CLASS_VARIABLE = "class";
	
	private final List<String> classes = new CopyOnWriteArrayList<String>();
	
	private final SparqlDao sparqlDao;
	
	public CachedDataServiceRDF(SparqlDao sparqlDao){
		this.sparqlDao = sparqlDao; 
	}
	
	public List<String> getClasses(){	
		return classes.isEmpty() ? queryForClasses() : classes;
	}

	private List<String> queryForClasses() {
		final SelectResultSet results = sparqlDao.executeSelect(GET_CLASSES_QUERY);		
		for(SelectResult result : results.getResults()){
			final SparqlElement element = result.getResult().get(CLASS_VARIABLE);
			classes.add(element.getValue());
		}
		return classes;
	}
	
}
