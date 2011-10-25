package org.linkedgov.questions.services;

import java.util.List;
import java.util.Map;

import org.linkedgov.questions.model.Query;
import org.linkedgov.questions.model.Triple;

/**
 * Interface that executes {@Link Query}
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> and 
 * @author <a href="http://mmt.me.uk/foaf.rdf#mischa">Mischa Tuffield</a> for LinkedGov
 *
 */
public interface QueryDataService {

    public List<Triple> executeQuery(Query query);
    
    /**
     * IF query is a count, returns 1. Otherwise, executes a query that is the equivalent to the query being executed with questionType={@Link QuestionType.COUNT}.
     * 
     * @param query the query.
     * @return the number of rows that will result from this query
     */
    public int executeCountForQuery(Query query, boolean forPagination);
      
    public List<Triple> executeQuery(Query query, Integer limit, Integer offset, String orderBy);
    
    public List<Triple> executeBnodeQuery(String bnode);
    
    public List<Triple> executeIRIQuery(String iri);
    
    public Map<String,String> executeGetAllGraphNames(Query query);
    
    public int executeReliabilityScore(Map<String,String> graphs);
 
}
