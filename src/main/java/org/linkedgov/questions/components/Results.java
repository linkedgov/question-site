package org.linkedgov.questions.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linkedgov.questions.model.Query;
import org.linkedgov.questions.model.SelectResultDataSource;
import org.linkedgov.questions.services.QueryDataService;

public class Results {
	
	/**
	 * The value of the query.
	 */
	@Parameter
	private Query query;
	
	/**
	 * QueryDataService service, to actually do the query with.
	 */
	@Inject
	private QueryDataService queryDataService;
	
	/**
	 * Block to show results table in.
	 */
	@Inject
	private Block resultsTableBlock;
	
	/**
	 * Block to show empty results in.
	 */
	@Inject
	private Block emptyResultsBlock;
	
	/**
	 * Block with no results yet.
	 */
	@Inject
	private Block noResultsYetBlock;
	
	/**
	 * Datasource to back the result table.
	 */
	@Property
	private GridDataSource dataSource;
	
	/**
	 * Select Result Data Source
	 */
	@SetupRender
	public void setupDataSource(){
		dataSource = new SelectResultDataSource(query, queryDataService);
	}
	
	public Block getResultsBlock(){
		System.out.println("My results");
		if(query.getSubject() == null){
			return noResultsYetBlock;
		} else {
			return dataSource.getAvailableRows() > 0 ? resultsTableBlock : emptyResultsBlock;
		}
	}
	
}
