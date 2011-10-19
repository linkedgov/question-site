package org.linkedgov.questions.pages;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linkedgov.questions.http.MultiformatStreamResponse;
import org.linkedgov.questions.services.SparqlDao;

public class CommaSeparatedResults {

	@Inject
	private SparqlDao sparqlDao;
	
	@SuppressWarnings("unused")
	private StreamResponse onActivate(String query){
		final String results = sparqlDao.getCsv(query);
		return new MultiformatStreamResponse("text/csv", results,"csv");
	}
}