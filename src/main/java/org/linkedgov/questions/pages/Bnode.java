package org.linkedgov.questions.pages;

import java.io.IOException;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linkedgov.questions.services.QueryDataService;
import org.linkedgov.questions.model.BNodeResultDataSource;
import org.linkedgov.questions.model.Triple;

import uk.me.mmt.sprotocol.SprotocolException;


/**
 * 
 * Simple Bnode page, with or with a Grid component describing a given bnode
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> and 
 * @author <a href="http://mmt.me.uk/foaf.rdf#mischa">Mischa Tuffield</a> for LinkedGov
 * 
 */
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
     * @throws SprotocolException 
     * @throws IOException 
     */
    @SetupRender
    public void setupDataSource() throws SprotocolException, IOException{
        dataSource = new BNodeResultDataSource(bnodeString, queryDataService);
    }

    public void onActivate(String bnodeString){
        this.bnodeString = bnodeString;    
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
