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

    public SelectResultSet executeSelect(String query);
    
    public int executeCount(String query);

    public SelectResultSet executeSelect(String query, int offset, int limit);

	public String getCsv(String query);
	
	public String getTsv(String query);
}
