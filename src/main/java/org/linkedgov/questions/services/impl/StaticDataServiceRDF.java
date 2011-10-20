package org.linkedgov.questions.services.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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

    private static final String GET_INITIALPREDICATE_QUERY = "SELECT DISTINCT ?pred ?plabel WHERE " +
            "{?s ?pred ?o . " +
            "OPTIONAL {?pred <http://www.w3.org/2000/01/rdf-schema#label> ?plabel } } ORDER BY ?pred"; 
  
    private static final String GET_CLASSES_QUERY = "SELECT DISTINCT ?class ?clabel WHERE " +
            "{?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?class . " +
            "OPTIONAL {?class <http://www.w3.org/2000/01/rdf-schema#label> ?clabel } } ORDER BY ?class"; 
    
    private static final String GET_CLASSES_WITHPRED_QUERY = "SELECT DISTINCT ?class ?clabel WHERE " +
            "{?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?class . " +
            "?s <%s> ?foo . " +
             "OPTIONAL {?class <http://www.w3.org/2000/01/rdf-schema#label> ?clabel } } ORDER BY ?class"; 
    
    private static final String GET_FIRSTFILTER_PREDICATE_QUERY = "SELECT DISTINCT ?pred ?plabel WHERE " +
            "{?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <%s> ; " +
            "?pred ?o . " +
            "OPTIONAL {?pred <http://www.w3.org/2000/01/rdf-schema#label> ?plabel } . " +
            "FILTER (?pred != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>) } ORDER BY ?pred";
        
    private static final String GET_FIRSTFILTER_OBJECTS_QUERY = "SELECT DISTINCT ?object ?olabel WHERE " +
            "{?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <%s> ; " +
            "<%s> ?object . " +
            "OPTIONAL {?object <http://www.w3.org/2000/01/rdf-schema#label> ?olabel } . " +
            "} ORDER BY ?object";
    
    private static final String GET_SECONDFILTER_PREDICATE_QUERY = "SELECT DISTINCT ?pred ?plabel WHERE " +
            "{?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <%s> ; " +
            "<%s> <%s> ; " +
            "?pred ?obj . " +
            "OPTIONAL {?pred <http://www.w3.org/2000/01/rdf-schema#label> ?plabel } . " +
            "} ORDER BY ?pred";
    
    private static final String GET_SECONDFILTER_OBJECT_QUERY = "SELECT DISTINCT ?object ?olabel WHERE " +
            "{?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <%s> ; " +
            "<%s> <%s> ; " +
            "<%s> ?object . " +
            "OPTIONAL {?object <http://www.w3.org/2000/01/rdf-schema#label> ?olabel } . " +
            "} ORDER BY ?object";
    
    private static final String CLASS_VARIABLE = "class";
    private static final String CLASS_VARIABLE_LABEL = "clabel";
    private static final String PREDICATE_VARIABLE = "pred";
    private static final String PREDICATE_VARIABLE_LABEL = "plabel";
    private static final String OBJECT_VARIABLE = "object";
    private static final String OBJECT_VARIABLE_LABEL = "olabel";

    private final Map<String,String> predicates = new ConcurrentHashMap<String,String>();
        
    private final SparqlDao sparqlDao;
    
    private final HashSet<String> classBlacklist;
    
    /**
     * TODO Mischa need to populate this configuration thingy
     * @param sparqlDao
     * @param configClassBlacklist
     */
    public StaticDataServiceRDF (SparqlDao sparqlDao, Collection<String> configClassBlacklist){
        this.sparqlDao = sparqlDao;
        this.classBlacklist = new HashSet<String>(configClassBlacklist);
        
        System.err.println("This is size "+classBlacklist.size());
    }
    
    /**
     * This is used to populate the initial list of predicates
     */
    public Map<String,String> getPredicates() {
        return predicates.isEmpty() ? queryForGetPredicates() : predicates;
    }
    
    /**
     * @param subject - the subject of the query/question.
     * @param predicate - the predicate of the second filter.
     * @param firstFilterPredicate - the predicate of the third filter.
     * @param firstFilterObject - the predicate of the third filter.
     * @return a {@Link org.apache.tapestry5.json.JSONObject} containing a list of potential objects and the id of the editor to display,
     */
    public Map<String,String> getObjects(String subject, String predicate) {
        String query = String.format(GET_FIRSTFILTER_OBJECTS_QUERY, subject, predicate);
        Map<String,String> retValues = new HashMap<String,String>();
        final SelectResultSet results = sparqlDao.executeSelect(query);        
        for (SelectResult result : results.getResults()) {
            final SparqlResource element = result.getResult().get(OBJECT_VARIABLE);
            if (result.getResult().get(OBJECT_VARIABLE_LABEL) != null) {
                retValues.put(element.getValue(), result.getResult().get(OBJECT_VARIABLE_LABEL).getValue());
            } else {
                retValues.put(element.getValue(),element.getValue());
            }
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
    public Map<String,String> getObjects(String subject, String predicate, QueryFilter filter) {
        String query = String.format(GET_SECONDFILTER_OBJECT_QUERY, subject, filter.getPredicate(), filter.getObject(), predicate);
        Map<String,String> retValues = new HashMap<String,String>();
        final SelectResultSet results = sparqlDao.executeSelect(query);        
        for (SelectResult result : results.getResults()) {
            final SparqlResource element = result.getResult().get(OBJECT_VARIABLE);
            if (result.getResult().get(OBJECT_VARIABLE_LABEL) != null) {
                retValues.put(element.getValue(), result.getResult().get(OBJECT_VARIABLE_LABEL).getValue());
            } else {
                retValues.put(element.getValue(),element.getValue());
            }            
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
    public Map<String,String> getPredicates(String subject, QueryFilter filter) {
        String query = String.format(GET_SECONDFILTER_PREDICATE_QUERY, subject, filter.getPredicate(), filter.getObject());
        Map<String,String> retValues = new HashMap<String,String>();
        final SelectResultSet results = sparqlDao.executeSelect(query);        
        for (SelectResult result : results.getResults()) {
            final SparqlResource element = result.getResult().get(PREDICATE_VARIABLE);
            if (result.getResult().get(PREDICATE_VARIABLE_LABEL) != null) {
                retValues.put(element.getValue(), result.getResult().get(PREDICATE_VARIABLE_LABEL).getValue());
            } else {
                retValues.put(element.getValue(),element.getValue());
            }
        }
        return retValues;
    }
    
    
    public Map<String,String> queryForGetPredicates() {
        final SelectResultSet results = sparqlDao.executeSelect(GET_INITIALPREDICATE_QUERY);        
        for (SelectResult result : results.getResults()) {
            final SparqlResource element = result.getResult().get(PREDICATE_VARIABLE);
            if (result.getResult().get(PREDICATE_VARIABLE_LABEL) != null) {
                predicates.put(element.getValue(), result.getResult().get(PREDICATE_VARIABLE_LABEL).getValue());
            } else {
                predicates.put(element.getValue(),element.getValue());
            }
        }
        return predicates;
    }
    
    /**
     * 
     * This function is used to return a list of predicates given a Class
     * 
     * @param subject : The Class type to find a list of predicates for
     * @return A List of Strings for the first list of predicates 
     */
    public Map<String,String> getPredicates(String subject) {
        String query = String.format(GET_FIRSTFILTER_PREDICATE_QUERY, subject);
        Map<String,String> retValues = new HashMap<String,String>();
        final SelectResultSet results = sparqlDao.executeSelect(query);        
        for (SelectResult result : results.getResults()) {
            final SparqlResource element = result.getResult().get(PREDICATE_VARIABLE);
                if (result.getResult().get(PREDICATE_VARIABLE_LABEL) != null) {
                    retValues.put(element.getValue(), result.getResult().get(PREDICATE_VARIABLE_LABEL).getValue());
                } else {
                    retValues.put(element.getValue(),element.getValue());
                }
        }
        return retValues;
    }

    /**
     * This function will get a list of all the classes in the KB
     * if no label then return a Map of URI,URI
     * @return A List of Strings for the first drop-down
     */
    public Map<String,String> getClasses() {    
        final SelectResultSet results = sparqlDao.executeSelect(GET_CLASSES_QUERY);  
        Map<String,String> retValues = new HashMap<String,String>();
        for (SelectResult result : results.getResults()) {
            final SparqlResource element = result.getResult().get(CLASS_VARIABLE);
            if (result.getResult().get(CLASS_VARIABLE_LABEL) != null) {
                retValues.put(element.getValue(), result.getResult().get(CLASS_VARIABLE_LABEL).getValue());
            } else {
                retValues.put(element.getValue(),element.getValue());
            }
        }
        return retValues;
    }
    
    public Map<String,String> getClasses(String predicate) {
        String query = String.format(GET_CLASSES_WITHPRED_QUERY, predicate);

        final SelectResultSet results = sparqlDao.executeSelect(query);  
        Map<String,String> retValues = new HashMap<String,String>();
        for (SelectResult result : results.getResults()) {
            final SparqlResource element = result.getResult().get(CLASS_VARIABLE);
            if (result.getResult().get(CLASS_VARIABLE_LABEL) != null) {
                retValues.put(element.getValue(), result.getResult().get(CLASS_VARIABLE_LABEL).getValue());
            } else {
                retValues.put(element.getValue(),element.getValue());
            }
        }
        return retValues;
    }

}
