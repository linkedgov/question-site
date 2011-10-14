package org.linkedgov.questions.model;

import uk.me.mmt.sprotocol.SparqlResource;

/**
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> and 
 * @author <a href="http://mmt.me.uk/foaf.rdf#mischa">Mischa Tuffield</a> for LinkedGov
 * 
 */
public class Triple {
	
	/**
	 * resource, label
	 */
	private Pair<SparqlResource,String> subject;
	
	private Pair<SparqlResource,String> predicate;
	
	private Pair<SparqlResource,String> object;

	public void setSubject(Pair<SparqlResource, String> subject) {
		this.subject = subject;
	}

	public Pair<SparqlResource, String> getSubject() {
		return subject;
	}

	public void setPredicate(Pair<SparqlResource, String> predicate) {
		this.predicate = predicate;
	}

	public Pair<SparqlResource, String> getPredicate() {
		return predicate;
	}

	public void setObject(Pair<SparqlResource, String> object) {
		this.object = object;
	}

	public Pair<SparqlResource, String> getObject() {
		return object;
	}
}