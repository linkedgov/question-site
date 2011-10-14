package org.linkedgov.questions.services;

import java.util.List;

import org.linkedgov.questions.model.Query;
import org.linkedgov.questions.model.Triple;

/**
 * TODO: this isn't actually used anymore. See if it ends up being required and deleted it if not.
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> and 
 * @author <a href="http://mmt.me.uk/foaf.rdf#mischa">Mischa Tuffield</a> for LinkedGov
 *
 */
public interface QueryDataService {

	public List<Triple> executeQuery(Query query);
}
