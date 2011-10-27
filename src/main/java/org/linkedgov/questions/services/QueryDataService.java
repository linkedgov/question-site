package org.linkedgov.questions.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.linkedgov.questions.model.Query;
import org.linkedgov.questions.model.Triple;

import uk.me.mmt.sprotocol.SprotocolException;

/**
 * Interface that executes {@Link Query}
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> and 
 * @author <a href="http://mmt.me.uk/foaf.rdf#mischa">Mischa Tuffield</a> for LinkedGov
 *
 */
public interface QueryDataService {

    public List<Triple> executeQuery(Query query) throws SprotocolException;
    
    /**
     * IF query is a count, returns 1. Otherwise, executes a query that is the equivalent to the query being executed with questionType={@Link QuestionType.COUNT}.
     * 
     * @param query the query.
     * 
     * @return the number of rows that will result from this query
     * @throws SprotocolException 
     * @throws IOException 
     */
    public int executeCountForQuery(Query query, boolean forPagination) throws SprotocolException, IOException;
      
    public List<Triple> executeQuery(Query query, Integer limit, Integer offset, String orderBy) throws SprotocolException;
    
    public List<Triple> executeBnodeQuery(String bnode) throws SprotocolException, IOException;
    
    public List<Triple> executeIRIQuery(String iri) throws SprotocolException, IOException;
    
    public Map<String,String> executeGetAllGraphNames(Query query) throws SprotocolException, IOException;
    
    public int executeReliabilityScore(Map<String,String> graphs) throws SprotocolException, IOException;
 
}
