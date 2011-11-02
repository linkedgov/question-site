package org.linkedgov.questions.services;

import java.io.IOException;

import uk.me.mmt.sprotocol.SelectResultSet;
import uk.me.mmt.sprotocol.SprotocolException;

/**
 * 
 * Does Sparql queries.
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> and 
 * @author <a href="http://mmt.me.uk/foaf.rdf#mischa">Mischa Tuffield</a> for LinkedGov
 *
 */
public interface SparqlDao {

    public SelectResultSet executeQuery(String query) throws SprotocolException, IOException;

    public SelectResultSet executeQuery(String query, Integer offset, Integer limit, String orderBy) throws SprotocolException, IOException;

    public String getCsv(String query) throws SprotocolException, IOException;

    public String getTsv(String query) throws SprotocolException, IOException;
}
