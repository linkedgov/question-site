package org.linkedgov.questions.services.impl;

import java.io.IOException;

import org.apache.tapestry5.ioc.annotations.Symbol;
import org.linkedgov.questions.services.QuestionsSymbolConstants;
import org.linkedgov.questions.services.SparqlDao;
import org.slf4j.Logger;

import uk.me.mmt.sprotocol.SelectResultSet;
import uk.me.mmt.sprotocol.SparqlQueryProtocolClient;
import uk.me.mmt.sprotocol.SprotocolException;

/**
 * Just a wrapper of sprotocol's SparqlProtocolClient.
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> and 
 * @author <a href="http://mmt.me.uk/foaf.rdf#mischa">Mischa Tuffield</a> for LinkedGov
 * 
 */
public class SparqlDaoImpl implements SparqlDao {

    /**
     * To log stuff with.
     */
    private final Logger log;

    /**
     * The client that does the actual querying.
     */
    private final SparqlQueryProtocolClient client;

    /**
     * Creates a new sparqlDaoImpl which points to a sparql protocol server on the passed endpoint.
     * 
     * @param endpoint - given by the system property with name {@Link QuestionsSymbolConstants.SPARQL_ENDPOINT_URL}. 
     * The default is in {@Link AppModule}
     */
    public SparqlDaoImpl(@Symbol(QuestionsSymbolConstants.SPARQL_ENDPOINT_URL) String endpoint, Logger log) {
        this.client = new SparqlQueryProtocolClient(endpoint);
        this.log = log;
    } 

    /**
     * Execute a select query.
     * 
     * @param query - the sparql select query string, e.g. SELECT ?x ?y ?z
     * @return a {@Link SelectResultSet} representing the results of the query.
     * @throws SprotocolException 
     * @throws IOException 
     */
    public SelectResultSet executeQuery(String query) throws SprotocolException, IOException  {
        log.info("The is query being fired "+query);
        try {
            return client.executeSelect(query);
        } catch (SprotocolException e) {
            throw new SprotocolException("Error making SPARQL protocol call", e);
        }
    }

    /**
     * 
     * 
     * @param query
     * @return
     * @throws SprotocolException 
     * @throws IOException 
     */
    public String getTsv(String query) throws SprotocolException, IOException {
        return client.executeSparqlRawAccept(query, "text/tab-separated-values");
    }

    /**
     *  
     * @param query
     * @return
     * @throws SprotocolException 
     * @throws IOException 
     */
    public String getCsv(String query) throws SprotocolException, IOException {
        return client.executeSparqlRawAccept(query,"text/csv");
    }

    /**
     * Execute a select query with an offset and limit.
     * 
     * @param query - the sparql select query string, e.g. SELECT ?x ?y ?z
     * @return a {@Link SelectResultSet} representing the results of the query.
     * @throws SprotocolException 
     * @throws IOException 
     */
    public SelectResultSet executeQuery(String query, Integer limit, Integer offset, String orderBy) throws SprotocolException, IOException {

        query = appendLimitOffsetOrderBy(query, limit, offset, orderBy);
        try {
            return client.executeSelect(query);
        } catch (SprotocolException e) {
            throw new SprotocolException("Error making SPARQL protocol call", e);
        }
    }

    /**
     * Appends to the end of the query if required.
     * 
     * @param query
     * @param offset
     * @param limit
     * @param orderBy
     * @return the new query
     */
    private String appendLimitOffsetOrderBy(String query, Integer limit,
            Integer offset, String orderBy) {
        final StringBuilder sb = new StringBuilder(query);
        if(limit != null){
            sb.append(" LIMIT ");
            sb.append(limit);
        }
        if(offset != null){
            sb.append(" OFFSET ");
            sb.append(offset);
        }
        if(orderBy != null){
            sb.append(" ORDER BY ");
            sb.append(orderBy);
        }

        return sb.toString();
    }

}
