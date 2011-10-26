package org.linkedgov.questions.model;

import java.util.List;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;
import org.linkedgov.questions.services.QueryDataService;

/**
 * 
 * Basic implementation of {@Link GridDataSource} for a Sparql query.
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> and 
 * @author <a href="http://mmt.me.uk/foaf.rdf#mischa">Mischa Tuffield</a> for LinkedGov
 * 
 */
public class BNodeResultDataSource implements GridDataSource {

    /**
     * The results of the query.
     */
    private List<Triple> currentPage;

    /**
     * The bonsw.
     */
    private final String bnode;

    /**
     * QueryDataService.
     */
    private final QueryDataService queryDataService;

    /**
     * Constructs a new SelectResultDataSource.
     * 
     * @param query - the query to get the data.
     * @param queryService - the service to send the query to to get the data.
     */
    public BNodeResultDataSource(String bnodeURI, QueryDataService queryDataService) {
        this.queryDataService = queryDataService;
        this.bnode = bnodeURI;
        this.currentPage = queryDataService.executeBnodeQuery(bnodeURI);
    }

    public int getAvailableRows() {
        // TODO Auto-generated method stub
        if (currentPage == null) {
            return 0;
        }
        return currentPage.size();
    }

    public void prepare(int startIndex, int endIndex,
            List<SortConstraint> sortConstraints) {
        System.err.println("GAY");
        this.currentPage = queryDataService.executeBnodeQuery(this.bnode);
        // TODO Auto-generated method stub

    }

    public Object getRowValue(int index) {
        return currentPage.get(index);
    }

    public  Class<?>  getRowType() {
        // TODO Auto-generated method stub
        return Triple.class;
    }

    /*
    public int getAvailableRows() { 
        return queryDataService.executeCountForQuery(query, true);
    }

    public Object getRowValue(int index) {
        return currentPage.get(index - startIndex);
    } 

    public Class<?> getRowType() {
        return Triple.class;
    }*/

}
