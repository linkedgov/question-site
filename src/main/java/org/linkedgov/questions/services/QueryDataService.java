package org.linkedgov.questions.services;

import java.util.List;
import java.util.Map;

import org.linkedgov.questions.model.Query;

/**
 * TODO: this isn't actually used anymore. See if it ends up being required and deleted it if not.
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk">Viscri</a> for LinkedGov
 *
 */
public interface QueryDataService {

	public List<Map<String, String>> executeQuery(Query query);
}
