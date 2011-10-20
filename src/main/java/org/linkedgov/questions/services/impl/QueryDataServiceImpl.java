package org.linkedgov.questions.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.linkedgov.questions.model.Pair;
import org.linkedgov.questions.model.Query;
import org.linkedgov.questions.model.Triple;
import org.linkedgov.questions.services.QueryDataService;
import org.linkedgov.questions.services.SparqlDao;
import org.slf4j.Logger;

import uk.me.mmt.sprotocol.SelectResult;
import uk.me.mmt.sprotocol.SelectResultSet;
import uk.me.mmt.sprotocol.SparqlResource;

/**
 * This class generates the object needed by the dataTable component
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> and 
 * @author <a href="http://mmt.me.uk/foaf.rdf#mischa">Mischa Tuffield</a> for LinkedGov
 * 
 */
public class QueryDataServiceImpl implements QueryDataService {

    /**
     * To do the actual querying with.
     */
    private final SparqlDao sparqlDao;

    /**
     * To log stuff with.
     */
    private final Logger log;
    
    /**
     * Automatically called by tapestry when instantiating the service, which is a singleton.
     */
    public QueryDataServiceImpl(SparqlDao sparqlDao, Logger log){
        this.sparqlDao = sparqlDao;
        this.log = log;
    }

    /**
     * Get results for the user's question.
     * 
     * @param Query object representing a user's question.
     * @return a list of triples representing the answer to the question.
     */
    public List<Triple> executeQuery(Query query) { 
        final List<Triple> triples = new ArrayList<Triple>();

        if (!query.isNull()) {          
            final String sparqlString = query.toSparqlString();
            log.info("SPARQL ASKED:{}", sparqlString);
            log.info("QUESTION ASKED:{}", query.toString());
            final SelectResultSet results = sparqlDao.executeSelect(sparqlString);
            for (SelectResult result : results.getResults()) {
                final Triple triple = resultToTriple(results.getHead(), result);
                triples.add(triple);
            }
        }
        return triples;
    }

    /**
     * Converts a result into a triple.
     * 
     * @param head a list of the variable names in the results.
     * @param result the result to convert.
     * @return the triple that represents the result. 
     */
	private Triple resultToTriple(List<String> head, SelectResult result) {
		final Triple triple = new Triple();    

		for (String variable : head) {
		    final SparqlResource resource =  result.getResult().get(variable);
		    
		    final Pair<SparqlResource,String> sub = new Pair<SparqlResource,String>();
		    final Pair<SparqlResource,String> pred = new Pair<SparqlResource,String>();
		    final Pair<SparqlResource,String> obj = new Pair<SparqlResource,String>();
		    
		    if (variable.equals("sub")) {
		        sub.setFirst(resource);
		        triple.setSubject(sub);
		    } else if (variable.equals("pred")) {
		        pred.setFirst(resource);
		        triple.setPredicate(pred);
		    } else if (variable.equals("obj")) {
		        obj.setFirst(resource);
		        triple.setObject(obj);
		    } else if (variable.equals("cnt")) {
		        sub.setFirst(resource);
		        triple.setSubject(sub);
		    } else if (variable.equals("slabel") && resource != null) {
		        sub.setFirst(resource);
		        triple.setSubject(sub);
		    } else if (variable.equals("plabel") && resource != null) {
		        pred.setFirst(resource);
		        triple.setPredicate(pred);
		    } else if (variable.equals("olabel") && resource != null) {
		        obj.setFirst(resource);
		        triple.setObject(obj);
		    }
		}
		return triple;
	}

}
