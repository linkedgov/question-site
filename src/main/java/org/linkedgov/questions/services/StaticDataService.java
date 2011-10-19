package org.linkedgov.questions.services;

import java.util.Map;

import org.linkedgov.questions.model.QueryFilter;

/**
 * Service for data that doesn't change very often.
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> and 
 * @author <a href="http://mmt.me.uk/foaf.rdf#mischa">Mischa Tuffield</a> for LinkedGov
 *
 */
public interface StaticDataService {
    
    public Map<String,String> getClasses();
    
    public Map<String,String> getObjects(String subject, String predicate);
    
    public Map<String,String> getPredicates(String subject);

    public Map<String,String> getPredicates(String subject, QueryFilter filter);

    public Map<String,String> getObjects(String subject, String predicate, QueryFilter filter);
}
