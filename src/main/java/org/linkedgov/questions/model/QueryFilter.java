package org.linkedgov.questions.model;

import org.apache.commons.lang.StringUtils;

/**
 * Pojo that represents a query, or question, built up by the user. 
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> and 
 * @author <a href="http://mmt.me.uk/foaf.rdf#mischa">Mischa Tuffield</a> for LinkedGov
 * 
 */
public class QueryFilter {

    private String predicate;
    
    private String object;
    
    public QueryFilter(String predicate, String object){
        this.predicate = predicate;
        this.object = object;
    }

    public QueryFilter() {
    }

    public void setObject(String object) {
        if(StringUtils.isBlank(object)){
            return;
        }
        this.object = object;
    }

    public String getObject() {
        return object;
    }

    public void setPredicate(String predicate) {
        if(StringUtils.isBlank(predicate)){
            return;
        }
        this.predicate = predicate;
    }

    public String getPredicate() {
        return predicate;
    }
    
    public boolean isNull() {
        if (predicate == null && object == null) {
            return true;
        }
        
        return false;
    }
}
