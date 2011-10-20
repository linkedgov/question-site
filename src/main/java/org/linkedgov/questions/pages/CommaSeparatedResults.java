package org.linkedgov.questions.pages;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linkedgov.questions.http.MultiformatStreamResponse;
import org.linkedgov.questions.services.SparqlDao;

/**
 * Class that serves up comma separated results for a given SPARQL query.
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> for LinkedGov
 *
 */
public class CommaSeparatedResults {

    /**
     * The sparqlDao, for making queries.
     */
	@Inject
	private SparqlDao sparqlDao;
	
	/**
	 * Sends the query to 4store and turns the results into a {@Link StreamResponse}.
	 * 
	 * @param query
	 * @return the response to send to the client.
	 */
	@SuppressWarnings("unused")
	private StreamResponse onActivate(String query){
		final String results = sparqlDao.getCsv(query);
		final String[] resArray = results.split("\\r?\\n");
		return new MultiformatStreamResponse("text/csv", results,"csv");
	}
}
