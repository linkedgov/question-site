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
	
	public SparqlDaoImpl(@Symbol(QuestionsSymbolConstants.SPARQL_ENDPOINT_URL) String endpoint) {
		client = new SparqlProtocolClient(endpoint);
	} 
	
	public SelectResultSet executeSelect(String query) {
		return client.executeSelect(query);
	}

	public int executeCount(String query) {
		return 0;
	}
}
