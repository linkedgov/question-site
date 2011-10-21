package org.linkedgov.questions.services;

import uk.me.mmt.sprotocol.SelectResultSet;

/**
 * 
 * Does Sparql queries.
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> and 
 * @author <a href="http://mmt.me.uk/foaf.rdf#mischa">Mischa Tuffield</a> for LinkedGov
 *
 */
public interface SparqlDao {

    public SelectResultSet executeQuery(String query);

    public SelectResultSet executeQuery(String query, Integer offset, Integer limit, String orderBy);

	public String getCsv(String query);
	
	public String getTsv(String query);
}
