package org.linkedgov.questions.pages;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linkedgov.questions.http.MultiformatStreamResponse;
import org.linkedgov.questions.services.SparqlDao;

public class TabSeparatedResults {

	@Inject
	private SparqlDao sparqlDao;
	
	@SuppressWarnings("unused")
	private StreamResponse onActivate(String query){
		final String results = sparqlDao.getTsv(query);
		return new MultiformatStreamResponse("text/tab-separated-values", results,"tsv");
	}
}
