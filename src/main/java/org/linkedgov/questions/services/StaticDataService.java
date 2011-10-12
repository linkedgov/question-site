package org.linkedgov.questions.services;

import java.util.List;

import org.linkedgov.questions.model.QueryFilter;

/**
 * Service for data that doesn't change very often.
 * 
 * @author Luke Wilson-Mawer {@Link http://viscri.co.uk} for LinkedGov
 *
 */
public interface StaticDataService {
	
	public List<String> getClasses();
	
	public List<String> getObjects(String subject, String predicate);
	
	public List<String> getPredicates(String subject);

	List<String> getPredicates(String subject, QueryFilter filter);

	List<String> getObjects(String subject, String predicate, QueryFilter filter);
}
