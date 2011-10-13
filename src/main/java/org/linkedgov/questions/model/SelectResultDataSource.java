package org.linkedgov.questions.model;

import java.util.List;
import java.util.Map;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;
import org.linkedgov.questions.services.QueryDataService;

/**
 * TODO: improve with pagination that goes all the way back to 4store.
 * 
 * Basic implementation of {@Link GridDataSource} for a Sparql query.
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk">Viscri</a> for LinkedGov
 *
 */
public class SelectResultDataSource implements GridDataSource {

	/**
	 * The query to get the data.
	 */
	private final Query query;
	
	/**
	 * For doing the actual sparql query.
	 */
	private final QueryDataService queryDataService;
	
	/**
	 * The results of the query.
	 */
	private final List<Map<String, String>> results;
	
	/**
	 * The start index.
	 */
	private int startIndex = 0;
	
	/**
	 * Constructs a new SelectResultDataSource.
	 * 
	 * @param query - the query to get the data.
	 * @param queryService - the service to send the query to to get the data.
	 */
	public SelectResultDataSource(Query query, QueryDataService queryDataService){
		this.query = query;
		this.queryDataService = queryDataService;
		this.results = queryDataService.executeQuery(query);
	}
	
	public void prepare(int startIndex, int endIndex,
			List<SortConstraint> sortConstraints) {
		this.startIndex = startIndex;
	}

	public int getAvailableRows() {
		return results == null ? 0 : results.size();
	}
	
	public Object getRowValue(int index) {
		return results.get(index-startIndex);
	} 

	public Class<?> getRowType() {
		return Map.class;
	}

}
