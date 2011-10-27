package org.linkedgov.questions.services;

import java.io.IOException;
import java.util.Map;

import org.linkedgov.questions.model.QueryFilter;

import uk.me.mmt.sprotocol.SprotocolException;

/**
 * Service for data that populates dropdowns.
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> and 
 * @author <a href="http://mmt.me.uk/foaf.rdf#mischa">Mischa Tuffield</a> for LinkedGov
 *
 */
public interface StaticDataService {
    
    public Map<String,String> queryForGetPredicates() throws SprotocolException, IOException;

    public Map<String,String> getPredicates() throws SprotocolException, IOException;
    
    public Map<String,String> getClasses() throws SprotocolException, IOException;
    
    public Map<String,String> getClasses(String predicate) throws SprotocolException, IOException;

    public Map<String,String> getObjects(String subject, String predicate) throws SprotocolException, IOException;
    
    public Map<String,String> getPredicates(String subject) throws SprotocolException, IOException;

    public Map<String,String> getPredicates(String subject, QueryFilter filter) throws SprotocolException, IOException;

    public Map<String,String> getObjects(String subject, String predicate, QueryFilter filter) throws SprotocolException, IOException;
}
