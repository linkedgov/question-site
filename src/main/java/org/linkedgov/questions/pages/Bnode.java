package org.linkedgov.questions.pages;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linkedgov.questions.services.QueryDataService;
import org.linkedgov.questions.model.BNodeResultDataSource;
import org.linkedgov.questions.model.Query;
import org.linkedgov.questions.model.QuestionType;
import org.linkedgov.questions.model.Triple;

public class Bnode {

    @Property
    private String bnodeString;

    /**
     * QueryDataService service, to actually do the query with.
     */
    @Inject
    private QueryDataService queryDataService;

    /**
     * Datasource to back the result table.
     */
    @Property
    @Persist
    private BNodeResultDataSource dataSource;

    /**
     * A row in the list of triples that come back as results.
     */
    @SuppressWarnings("unused")
    @Property
    private Triple triple;

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
     * @return - a block which contains the results or an error message if there aren't any.
     */
    public Block getResultsBlock(){
        return dataSource.getAvailableRows() > 0 ? resultsTableBlock : emptyResultsBlock;
    }
    
    /**
     * Select Result Data Source
     */
    @SetupRender
    public void setupDataSource(){
        dataSource = new BNodeResultDataSource(bnodeString, queryDataService);
    }

    public void onActivate(String bnodeString){
        this.bnodeString = bnodeString;    

        System.err.println("This is super lame "+bnodeString);
    }


    /**
     * Returns a comma separated list of the grid columns.
     * 
     * @return a comma separated list of the grid columns.
     */
    public String getGridColumns() {
        return "resultSubject,resultPredicate,resultObject";
    }

}
