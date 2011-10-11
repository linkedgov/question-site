package org.linkedgov.questions.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.linkedgov.questions.services.StaticDataService;

public class StaticDataServiceStub implements StaticDataService {

	public List<String> getClasses() {
		
		ArrayList<String> classes = new ArrayList<String>();
		classes.add("http://viscri.co.uk/gpsurgery");
		classes.add("http://viscri.co.uk/recyclingcentre");
		classes.add("http://viscri.co.uk/onions");
		
		return classes;
	}

	public List<String> getObjects(String subject, String predicate) {
		
		ArrayList<String> objects = new ArrayList<String>();
		objects.add("50");
		objects.add("40");
		objects.add("Nonsense");
		objects.add("Jonathan Edwards OBE");
		
		return objects;
	}

	public List<String> getPredicates(String subject) {
		
		ArrayList<String> predicates = new ArrayList<String>();
		predicates.add("50");
		predicates.add("40");
		predicates.add("location");
		predicates.add("Jonathan Edwards OBE");
		
		return predicates;

	}

}
