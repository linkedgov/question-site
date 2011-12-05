package org.linkedgov.questions.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.linkedgov.questions.model.QueryFilter;
import org.linkedgov.questions.model.SparqlUtils;
import org.linkedgov.questions.services.SparqlDao;
import org.linkedgov.questions.services.StaticDataService;


import uk.me.mmt.sprotocol.SelectResultRow;
import uk.me.mmt.sprotocol.SelectResultSet;
import uk.me.mmt.sprotocol.SparqlResource;
import uk.me.mmt.sprotocol.SprotocolException;

/**
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
            "{?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <%s> . " +
            "%s  " +
            "?s ?pred ?obj . " +
            "OPTIONAL {?pred <http://www.w3.org/2000/01/rdf-schema#label> ?plabel } . " +
            "} ORDER BY ?pred";
    
    private static final String GET_SECONDFILTER_OBJECT_QUERY = "SELECT DISTINCT ?object ?olabel WHERE " +
            "{?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <%s> . " +
            "%s  " +
            "?s <%s> ?object . " +
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
    
    private final ArrayList<String> blacklist;
        
    /**
     * @param sparqlDao
     * @param configClassBlacklist
     */
    public StaticDataServiceRDF (SparqlDao sparqlDao, Collection<String> configBlacklist) {
        this.sparqlDao = sparqlDao;
        this.blacklist = new ArrayList<String>(configBlacklist);
    }
    
    /**
     * This is used to populate the initial list of predicates
     * @throws SprotocolException 
     * @throws IOException 
     */
    public Map<String,String> getPredicates() throws SprotocolException, IOException {
        return predicates.isEmpty() ? queryForGetPredicates() : predicates;
    }
    
    /**
     * @param subject - the subject of the query/question.
     * @param predicate - the predicate of the second filter.
     * @param firstFilterPredicate - the predicate of the third filter.
     * @param firstFilterObject - the predicate of the third filter.
     * @return a {@Link org.apache.tapestry5.json.JSONObject} containing a list of potential objects and the id of the editor to display,
     * @throws SprotocolException 
     * @throws IOException 
     */
    public Map<String,String> getObjects(String subject, String predicate) throws SprotocolException, IOException {
        String query = String.format(GET_FIRSTFILTER_OBJECTS_QUERY, subject, predicate);
        Map<String,String> retValues = new HashMap<String,String>();
        final SelectResultSet results = sparqlDao.executeQuery(query);        
        for (SelectResultRow result : results) {
            final SparqlResource element = result.get(OBJECT_VARIABLE);
            if (result.get(OBJECT_VARIABLE_LABEL) != null) {
                retValues.put(element.getValue(), WordUtils.capitalize(result.get(OBJECT_VARIABLE_LABEL).getValue()));
            } else {
                retValues.put(element.getValue(), WordUtils.capitalize(createLabel(element.getValue())));
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
     * @throws SprotocolException 
     * @throws IOException 
     */
    public Map<String,String> getObjects(String subject, String predicate, QueryFilter filter) throws SprotocolException, IOException {
        String toPopulate = "";
        String filterObject = filter.getObject();
        String filterPredicate = filter.getPredicate();

        if (SparqlUtils.isBnode(filterObject)) {
            toPopulate = "?s <"+filterPredicate+"> <bnode:"+filterObject+"> . ";
        } else if (SparqlUtils.isURI(filterObject)) {
            toPopulate = "?s <"+filterPredicate+"> <"+filterObject+"> . ";
        } else  {
            if (SparqlUtils.isInteger(filterObject)) {        
                toPopulate = "{ {?s <"+filterPredicate+"> "+filterObject+" } UNION {?s <"+filterPredicate+"> \""+filterObject+"\" } } . ";
            } else if (SparqlUtils.isFloat(filterObject)) {
                toPopulate = "{ {?s <"+filterPredicate+"> \""+filterObject+"\"^^<http://www.w3.org/2001/XMLSchema#float> } UNION {?s <"+filterPredicate+"> \""+filterObject+"\" } } . ";
            } else {
                toPopulate = "{ {?s <"+filterPredicate+"> \""+filterObject+"\"@EN } UNION {?s <"+filterPredicate+"> \""+filterObject+"\"@en } UNION {?s <"+filterPredicate+"> \""+filterObject+"\"} } . ";
            }
        }
        
        String query = String.format(GET_SECONDFILTER_OBJECT_QUERY, subject, toPopulate, predicate);
        Map<String,String> retValues = new HashMap<String,String>();
        final SelectResultSet results = sparqlDao.executeQuery(query);        
        for (SelectResultRow result : results) {
            final SparqlResource element = result.get(OBJECT_VARIABLE);
            if (result.get(OBJECT_VARIABLE_LABEL) != null) {
                retValues.put(element.getValue(), WordUtils.capitalize(result.get(OBJECT_VARIABLE_LABEL).getValue()));
            } else {
                retValues.put(element.getValue(), WordUtils.capitalize(createLabel(element.getValue())));
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
     * @throws SprotocolException 
     * @throws IOException 
     */
    public Map<String,String> getPredicates(String subject, QueryFilter filter) throws SprotocolException, IOException {
        String toPopulate = "";
        String object = filter.getObject();
        String predicate = filter.getPredicate();
        
        if (SparqlUtils.isBnode(object)) {
            toPopulate = "?s <"+predicate+"> <bnode:"+object+"> . ";
        } else if (SparqlUtils.isURI(object)) {
            toPopulate = "?s <"+predicate+"> <"+object+"> . ";
        } else  {
            if (SparqlUtils.isInteger(object)) {        
                toPopulate = "{ {?s <"+predicate+"> "+object+" } UNION {?s <"+predicate+"> \""+object+"\" } } . ";
            } else if (SparqlUtils.isFloat(object)) {
                toPopulate = "{ {?s <"+predicate+"> \""+object+"\"^^<http://www.w3.org/2001/XMLSchema#float> } UNION {?s <"+predicate+"> \""+object+"\" } } . ";
            } else {
                toPopulate = "{ {?s <"+predicate+"> \""+object+"\"@EN } UNION {?s <"+predicate+"> \""+object+"\"@en } UNION {?s <"+predicate+"> \""+object+"\"} } . ";
            }
        }

        String query = String.format(GET_SECONDFILTER_PREDICATE_QUERY, subject, toPopulate);
        Map<String,String> retValues = new HashMap<String,String>();
        final SelectResultSet results = sparqlDao.executeQuery(query);        
        for (SelectResultRow result : results) {
            final SparqlResource element = result.get(PREDICATE_VARIABLE);
            if (result.get(PREDICATE_VARIABLE_LABEL) != null) {
                retValues.put(element.getValue(), WordUtils.capitalize(result.get(PREDICATE_VARIABLE_LABEL).getValue()));
            } else {
                retValues.put(element.getValue(), WordUtils.capitalize(createLabel(element.getValue())));
            }
        }
        return retValues;
    }
    
    
    public Map<String,String> queryForGetPredicates() throws SprotocolException, IOException {
        final SelectResultSet results = sparqlDao.executeQuery(GET_INITIALPREDICATE_QUERY);        
        for (SelectResultRow result : results) {
            final SparqlResource element = result.get(PREDICATE_VARIABLE);
            if (!isBlacklisted(element.getValue())) {
                if (result.get(PREDICATE_VARIABLE_LABEL) != null) {
                    predicates.put(element.getValue(), WordUtils.capitalize(result.get(PREDICATE_VARIABLE_LABEL).getValue()));
                } else {
                    predicates.put(element.getValue(), WordUtils.capitalize(createLabel(element.getValue())));
                }
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
     * @throws SprotocolException 
     * @throws IOException 
     */
    public Map<String,String> getPredicates(String subject) throws SprotocolException, IOException {
        String query = String.format(GET_FIRSTFILTER_PREDICATE_QUERY, subject);
        Map<String,String> retValues = new HashMap<String,String>();
        final SelectResultSet results = sparqlDao.executeQuery(query);        
        for (SelectResultRow result : results) {
            final SparqlResource element = result.get(PREDICATE_VARIABLE);

            if (!isBlacklisted(element.getValue())) {
                if (result.get(PREDICATE_VARIABLE_LABEL) != null) {
                    retValues.put(element.getValue(), WordUtils.capitalize(result.get(PREDICATE_VARIABLE_LABEL).getValue()));
                } else {
                    retValues.put(element.getValue(), WordUtils.capitalize(createLabel(element.getValue())));
                }
            }
        }
        return retValues;
    }

    /**
     * This function will get a list of all the classes in the KB
     * if no label then return a Map of URI,URI
     * @return A Map of Strings (value to label) for the first drop-down
     * @throws SprotocolException 
     * @throws IOException 
     */
    public Map<String,String> getClasses() throws SprotocolException, IOException {            
        return getClasses(null);
    }
    
    /**
     * This function will get a list of all the classes in the KB for the given predicate.
     * @return
     * @throws SprotocolException 
     * @throws IOException 
     */
    public Map<String,String> getClasses(String predicate) throws SprotocolException, IOException {
        final String query = StringUtils.isBlank(predicate) ? GET_CLASSES_QUERY : String.format(GET_CLASSES_WITHPRED_QUERY, predicate);
        final SelectResultSet results = sparqlDao.executeQuery(query);  
        Map<String,String> retValues = new HashMap<String,String>();
        for (SelectResultRow result : results) {
            final SparqlResource element = result.get(CLASS_VARIABLE);
            if (!isBlacklisted(element.getValue())) {
                if (result.get(CLASS_VARIABLE_LABEL) != null) {
                    retValues.put(element.getValue(), WordUtils.capitalize(result.get(CLASS_VARIABLE_LABEL).getValue()));
                } else {
                    retValues.put(element.getValue(), WordUtils.capitalize(createLabel(element.getValue())));
                }
            }
        }
        return retValues;
    }
    
    /**
     * Create a pretty label for an object or a predicate name
     * @param iri
     * @return
     */
    private String createLabel(String iri) {
        String label = iri;
        if (iri.lastIndexOf("#") != -1) {
            label = iri.substring(iri.lastIndexOf("#")+1);
        } else if (iri.lastIndexOf("/") != -1) {
            label = iri.substring(iri.lastIndexOf("/")+1);
        }
        
        return label;
    }
    
    /**
     * Checks if a value is in the blacklist returns a boolean accordingly
     */
    public boolean isBlacklisted (String value) {
        boolean blacklisted = false;
        for (String blackitem : blacklist) {
            if (blackitem.equals(value)) {
                blacklisted = true; 
                break;
            }
        }
        return blacklisted;
    }

}
