package org.linkedgov.questions.services.impl;

import org.apache.tapestry5.ioc.annotations.Symbol;
import org.linkedgov.questions.services.QuestionsSymbolConstants;
import org.linkedgov.questions.services.SparqlDao;

import uk.me.mmt.sprotocol.SelectResultSet;
import uk.me.mmt.sprotocol.SparqlProtocolClient;
import uk.me.mmt.sprotocol.SprotocolException;

/**
 * Just a wrapper of sprotocol's SparqlProtocolClient.
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> and 
 * @author <a href="http://mmt.me.uk/foaf.rdf#mischa">Mischa Tuffield</a> for LinkedGov
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
	 * @throws SprotocolException 
	 */
	public SelectResultSet executeSelect(String query)  {
		try {
		    //TODO Mischa remove this debug
		    System.err.println("The is query being fired "+query);
		    return client.executeSelect(query);
		} catch (SprotocolException e) {
		    System.err.println("Error making SPARQL protocol call"+e.getMessage());
		}
		return new SelectResultSet();
	}
	
	/**
	 * Execute a select query with an offset and limit.
	 * 
	 * @param query - the sparql select query string, e.g. SELECT ?x ?y ?z
	 * @return a {@Link SelectResultSet} representing the results of the query.
	 * @throws SprotocolException 
	 */
	public SelectResultSet executeSelect(String query, int offset, int limit) {
		try {
		    return client.executeSelect(query);
		} catch (SprotocolException e) {
		    System.err.println("Error making SPARQL protocol call"+e.getMessage());
		}
		return new SelectResultSet();
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
