package org.linkedgov.questions.model;

import java.io.IOException;
import java.util.List;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;
import org.linkedgov.questions.services.QueryDataService;

import uk.me.mmt.sprotocol.SprotocolException;

/**
 * 
 * Basic implementation of {@Link GridDataSource} for a Sparql query.
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> and 
 * @author <a href="http://mmt.me.uk/foaf.rdf#mischa">Mischa Tuffield</a> for LinkedGov
 * 
 */
public class SelectResultDataSource implements GridDataSource {
    
    /**
     * The results of the query.
     */
    private List<Triple> currentPage;
    
    /**
     * The query.
     */
    private final Query query;
    
    /**
     * QueryDataService.
     */
    private final QueryDataService queryDataService;
    
    /**
     * The start index.
     */
    private int startIndex = 0;
    
    /**
     * The end index.
     */
    private int endIndex = 0;
    
    /**
     * Constructs a new SelectResultDataSource.
     * 
     * @param query - the query to get the data.
     * @param queryService - the service to send the query to to get the data.
     */
    public SelectResultDataSource(Query query, QueryDataService queryDataService) {
        this.queryDataService = queryDataService;
        this.query = query;
    }
    
    public void prepare(int startIndex, int endIndex,
            List<SortConstraint> sortConstraints) {
        this.endIndex = endIndex;
        this.startIndex = startIndex;
        try {
            this.currentPage = queryDataService.executeQuery(query, endIndex-startIndex+1, startIndex, null);
        } catch (SprotocolException e) {
            System.err.println("Problem talking to sparqlstore, SprotocolException: "+e.getMessage());
        } catch (IOException e) {
            System.err.println("Problem talking to sparqlstore, IOException: "+e.getMessage());
        }
    }

    public int getAvailableRows() { 
        try {
            return queryDataService.executeCountForQuery(query, true);
        } catch (SprotocolException e) {
            System.err.println("Problem talking to sparqlstore SprotocolException: "+e.getMessage());
        } catch (IOException e) {
            System.err.println("Problem talking to sparqlstore IOException: "+e.getMessage());
        }
        return endIndex;
    }
    
    public Object getRowValue(int index) {
        return currentPage.get(index - startIndex);
    } 

    public Class<?> getRowType() {
        return Triple.class;
    }

}
