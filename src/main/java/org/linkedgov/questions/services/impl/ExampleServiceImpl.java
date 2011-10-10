package org.linkedgov.questions.services.impl;

import org.linkedgov.questions.model.ExampleObject;
import org.linkedgov.questions.services.ExampleService;
import org.linkedgov.questions.services.SparqlDao;

import com.google.inject.Inject;

public class ExampleServiceImpl implements ExampleService {

	@Inject
	private SparqlDao sparqlDao;
	
	private ExampleObject getExampleObject(String example){
		
		//Get the results and turn them into an example object. May need several queries here.
		//String results = sparqlDao.query("SELECT ?x ?y ?z WHERE");
		return new ExampleObject();
	}
	

}
