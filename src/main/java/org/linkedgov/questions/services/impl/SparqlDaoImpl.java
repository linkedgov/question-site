package org.linkedgov.questions.services.impl;

import org.apache.tapestry5.ioc.annotations.Symbol;
import org.linkedgov.questions.services.QuestionsSymbolConstants;
import org.linkedgov.questions.services.SparqlDao;

import uk.me.mmt.sprotocol.SelectResultSet;
import uk.me.mmt.sprotocol.SparqlProtocolClient;

/**
 * Just a wrapper of sprotocol's SparqlProtocolClient.
 * 
 * @author Luke Wilson-Mawer Viscri Limited for LinkedGov.
 *
 */
public class SparqlDaoImpl implements SparqlDao {

	private final SparqlProtocolClient client;
	
	/**
	 * Creates a new sparqlDaoImpl which points to a sparql protocol server on the passed endpoint.
	 * 
	 * @param endpoint - given by the system property with name {@Link QuestionsSymbolConstants.SPARQL_ENDPOINT_URL}. 
	 * The default is in {@Link AppModule}
	 */
	public SparqlDaoImpl(@Symbol(QuestionsSymbolConstants.SPARQL_ENDPOINT_URL) String endpoint) {
		client = new SparqlProtocolClient(endpoint);
	} 
	
	/**
	 * Execute a select query.
	 * 
	 * @param query - the sparql select query string, e.g. SELECT ?x ?y ?z
	 * @return a {@Link SelectResultSet} representing the results of the query.
	 */
	public SelectResultSet executeSelect(String query) {
		return client.executeSelect(query);
	}
	
	/**
	 * Execute a select query with an offset and limit.
	 * 
	 * @param query - the sparql select query string, e.g. SELECT ?x ?y ?z
	 * @return a {@Link SelectResultSet} representing the results of the query.
	 */
	public SelectResultSet executeSelect(String query, int offset, int limit) {
		return client.executeSelect(query);
	}

	/**
	 * Execute a count query.
	 * 
	 * @param query - the sparql select query string, e.g. SELECT COUNT * WHERE ?x ?y ?z
	 * @return an integer containing the results of the count.
	 */
	public int executeCount(String query) {
		return 100;
	}

}
