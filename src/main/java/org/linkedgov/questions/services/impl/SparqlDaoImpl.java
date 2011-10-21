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

    /**
     * The client that does the actual querying.
     */
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
    public SelectResultSet executeQuery(String query)  {
        System.err.println("The is query being fired "+query);
    	try {
            return client.executeSelect(query);
        } catch (SprotocolException e) {
            System.err.println("Error making SPARQL protocol call"+e.getMessage());
        }
        return new SelectResultSet();
    }
    
    /**
     * 
     * 
     * @param query
     * @return
     * @throws SprotocolException 
     */
    public String getTsv(String query) throws SprotocolException {
        return client.sparqlQueryRawAccept(query, "text/tab-separated-values");
    }
    
    /**
     *  
     * @param query
     * @return
     * @throws SprotocolException 
     */
    public String getCsv(String query) throws SprotocolException {
		return client.sparqlQueryRawAccept(query,"text/csv");
    }
    
    /**
     * Execute a select query with an offset and limit.
     * 
     * @param query - the sparql select query string, e.g. SELECT ?x ?y ?z
     * @return a {@Link SelectResultSet} representing the results of the query.
     * @throws SprotocolException 
     */
    public SelectResultSet executeQuery(String query, Integer limit, Integer offset, String orderBy) {
        
        query = appendLimitOffsetOrderBy(query, limit, offset, orderBy);
        try {
            return client.executeSelect(query);
        } catch (SprotocolException e) {
            System.err.println("Error making SPARQL protocol call"+e.getMessage());
        }
        return new SelectResultSet();
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
