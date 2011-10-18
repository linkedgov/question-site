package org.linkedgov.questions.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.linkedgov.questions.model.QueryFilter;
import org.linkedgov.questions.services.SparqlDao;
import org.linkedgov.questions.services.StaticDataService;

import uk.me.mmt.sprotocol.SelectResult;
import uk.me.mmt.sprotocol.SelectResultSet;
import uk.me.mmt.sprotocol.SparqlResource;

/**
 * TODO: decide on what stuff to cache and what not to cache here.
 * 
 * This Class is used to populate the various drop downs in the Question Answering Site
 * by creating SPARQL queries based on the user input
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> and 
 * @author <a href="http://mmt.me.uk/foaf.rdf#mischa">Mischa Tuffield</a> for LinkedGov
 *
 */
public class StaticDataServiceRDF implements StaticDataService {
        
    private static final String GET_CLASSES_QUERY = "SELECT DISTINCT ?class ?clabel WHERE " +
            "{?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?class . " +
            "OPTIONAL {?class <http://www.w3.org/2000/01/rdf-schema#label> ?clabel } } ORDER BY ?class"; 
    
    private static final String GET_PREDICATE_QUERY = "SELECT DISTINCT ?pred WHERE " +
            "{?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <%s> ; " +
            "?pred ?o . " +
            "FILTER (?pred != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>) } ORDER BY ?pred";
        
    private static final String GET_OBJECTS_QUERY = "SELECT DISTINCT ?object WHERE " +
            "{?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <%s> ; " +
            "<%s> ?object} ORDER BY ?object";
    
    private static final String GET_SECONDFILTER_PREDICATE_QUERY = "SELECT DISTINCT ?pred WHERE " +
            "{?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <%s> ; " +
            "<%s> <%s> ; " +
            "?pred ?obj} ORDER BY ?pred";
    
    private static final String GET_SECONDFILTER_OBJECT_QUERY = "SELECT DISTINCT ?object WHERE " +
            "{?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <%s> ; " +
            "<%s> <%s> ; " +
            "<%s> ?object} ORDER BY ?object";
    
    private static final String CLASS_VARIABLE = "class";
    private static final String PREDICATE_VARIABLE = "pred";
    private static final String OBJECT_VARIABLE = "object";

    private final Map<String,String> classes = new ConcurrentHashMap<String,String>();
        
    private final SparqlDao sparqlDao;
    
    /**
     * TODO Mischa need to populate this configuration thingy
     * @param sparqlDao
     * @param configuration
     */
    public StaticDataServiceRDF (SparqlDao sparqlDao, List<String> configuration){
        this.sparqlDao = sparqlDao; 
    }
    
    public Map<String,String> getClasses() {    
        return classes.isEmpty() ? queryForClasses() : classes;
    }
    
    /**
     * @param subject - the subject of the query/question.
     * @param predicate - the predicate of the second filter.
     * @param firstFilterPredicate - the predicate of the third filter.
     * @param firstFilterObject - the predicate of the third filter.
     * @return a {@Link org.apache.tapestry5.json.JSONObject} containing a list of potential objects and the id of the editor to display,
     */
    public List<String> getObjects(String subject, String predicate) {
        String query = String.format(GET_OBJECTS_QUERY, subject, predicate);
        List<String> retValues = new ArrayList<String>();
        final SelectResultSet results = sparqlDao.executeSelect(query);        
        for (SelectResult result : results.getResults()) {
            final SparqlResource element = result.getResult().get(OBJECT_VARIABLE);
            retValues.add(element.getValue());
        }
        return retValues;
    }
    
    /**
     * 
     * This function is used to return a list of objects given the second filter, Class and the First Filter
     * 
     * @param subject : The Class type to find a list of predicates for
     * @param filter : This contains the first filter 
     * @return A List of Strings for the second list of predicates 
     */
    
    public List<String> getObjects(String subject, String predicate, QueryFilter filter) {
        String query = String.format(GET_SECONDFILTER_OBJECT_QUERY, subject, filter.getPredicate(), filter.getObject(), predicate);
        List<String> retValues = new ArrayList<String>();
        final SelectResultSet results = sparqlDao.executeSelect(query);        
        for (SelectResult result : results.getResults()) {
            final SparqlResource element = result.getResult().get(OBJECT_VARIABLE);
            retValues.add(element.getValue());
        }
        return retValues;        
    }
    
    /**
     * 
     * This function is used to return a list of predicates given a Class and the First Filter
     * 
     * @param subject : The Class type to find a list of predicates for
     * @param filter : This contains the first filter 
     * @return A List of Strings for the second list of predicates 
     */
    public List<String> getPredicates(String subject, QueryFilter filter) {
        String query = String.format(GET_SECONDFILTER_PREDICATE_QUERY, subject, filter.getPredicate(), filter.getObject());
        List<String> retValues = new ArrayList<String>();
        final SelectResultSet results = sparqlDao.executeSelect(query);        
        for (SelectResult result : results.getResults()) {
            final SparqlResource element = result.getResult().get(PREDICATE_VARIABLE);
            retValues.add(element.getValue());
        }
        return retValues;
    }
    
    /**
     * 
     * This function is used to return a list of predicates given a Class
     * 
     * @param subject : The Class type to find a list of predicates for
     * @return A List of Strings for the first list of predicates 
     */
    public List<String> getPredicates(String subject) {
        String query = String.format(GET_PREDICATE_QUERY, subject);
        List<String> retValues = new ArrayList<String>();
        final SelectResultSet results = sparqlDao.executeSelect(query);        
        for (SelectResult result : results.getResults()) {
            final SparqlResource element = result.getResult().get(PREDICATE_VARIABLE);
                retValues.add(element.getValue());
        }
        return retValues;
    }

    /**
     * This function will get a list of all the classes in the KB
     * 
     * @return A List of Strings for the first drop-down
     */
    private Map<String,String> queryForClasses() {
        final SelectResultSet results = sparqlDao.executeSelect(GET_CLASSES_QUERY);        
        for (SelectResult result : results.getResults()) {
            final SparqlResource element = result.getResult().get(CLASS_VARIABLE);
            if (result.getResult().get("clabel") != null) {
                classes.put(element.getValue(), result.getResult().get("clabel").getValue());
            } else {
                classes.put(element.getValue(),element.getValue());
            }
        }
        return classes;
    }
    
}
