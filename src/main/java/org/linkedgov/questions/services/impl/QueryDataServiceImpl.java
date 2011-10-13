package org.linkedgov.questions.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.linkedgov.questions.model.Query;
import org.linkedgov.questions.services.QueryDataService;
import org.linkedgov.questions.services.SparqlDao;

/**
 * TODO: this isn't actually used anymore. See if it ends up being required and deleted it if not.
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk">Viscri</a> for LinkedGov
 *
 */
public class QueryDataServiceImpl implements QueryDataService {

	private final SparqlDao sparqlDao;
	
	public QueryDataServiceImpl(SparqlDao sparqlDao){
		this.sparqlDao = sparqlDao;
	}
	
	public List<Map<String,String>> executeQuery(Query query) {
		
		 List<Map<String,String>> results = new ArrayList<Map<String,String>>();
		 Map<String,String> map = new HashMap<String,String>();
		 map.put("key", "value");
		 map.put("key2", "value2");
		 
		 Map<String,String> map2 = new HashMap<String,String>();
		 map.put("a", "bb");
		 map.put("a", "bbb");
		 results.add(map);
		 results.add(map2);
		 
		 return results;
	}

}
