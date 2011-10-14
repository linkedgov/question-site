package org.linkedgov.questions.services.impl;

import java.util.List;

import org.linkedgov.questions.model.Query;
import org.linkedgov.questions.model.Triple;
import org.linkedgov.questions.services.QueryDataService;
import org.linkedgov.questions.services.SparqlDao;

/**
 * TODO: this isn't actually used anymore. See if it ends up being required and deleted it if not.
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk">Viscri</a> for LinkedGov
 *
 */
public class QueryDataServiceImpl implements QueryDataService {

	private final SparqlDao sparqlDao;
	
	public QueryDataServiceImpl(SparqlDao sparqlDao){
		this.sparqlDao = sparqlDao;
	}
	
	public List<Triple> executeQuery(Query query) { 
		sparqlDao.executeSelect(query.toSparqlString());
		//TODO: Mischa to parse this and return a list of triples.
		//sparqlDao.executeSelect(query.toSparqlString());
		return null;
	}

}
