package org.linkedgov.questions.components;

import java.io.IOException;
import java.util.Map;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linkedgov.questions.model.Query;
import org.linkedgov.questions.model.QuestionType;
import org.linkedgov.questions.model.SelectResultDataSource;
import org.linkedgov.questions.model.Triple;
import org.linkedgov.questions.pages.DelimeterSeparatedResults;
import org.linkedgov.questions.pages.ExcelResults;
import org.linkedgov.questions.services.QueryDataService;

import uk.me.mmt.sprotocol.SprotocolException;

/**
 * Component that represents results.
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> and 
 * @author <a href="http://mmt.me.uk/foaf.rdf#mischa">Mischa Tuffield</a> for LinkedGov
 */
public class Results {

    /**
     * The value of the query.
     */
    @Property
    @Parameter
    private Query query;

    /**
     * Dataset value; 
     */
    @Property
    private String datasetKey;

    /**
     * A row in the list of triples that come back as results.
     */
    @SuppressWarnings("unused")
    @Property
    private Triple triple;

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
     * Page to go to if somebody asks for a .xls file.
     */
    @InjectPage
    private ExcelResults excelResults;

    /**
     * Page to go to if somebody asks for delimeter separated results (csv or tsv).
     */
    @InjectPage
    private DelimeterSeparatedResults delimeterResults;

    /**
     * Datasource to back the result table.
     */
    @Property
    @Persist
    private SelectResultDataSource dataSource;

    /**
     * Select Result Data Source
     */
    @SetupRender
    public void setupDataSource(){
        dataSource = new SelectResultDataSource(query, queryDataService);
    }

    /**
     * @return - a block which contains the results or an error message if there aren't any.
     */
    public Block getResultsBlock(){
        if(query.getSubject() == null){
            return noResultsYetBlock;
        } else {
            return dataSource.getAvailableRows() > 0 ? resultsTableBlock : emptyResultsBlock;
        }
    }

    /**
     * Returns a comma separated list of the grid columns.
     * 
     * @return a comma separated list of the grid columns.
     */
    public String getGridColumns() {
        if (query.getQuestionType().equals(QuestionType.SELECT)) {
            return "resultSubject,resultPredicate,resultObject";
        } else {
            return "resultCount";
        }
    }

    /**
     * Return the sparql query.
     */
    public String getSparql(){
        return query.toSparqlString();
    }


    /**
     * Handles clicks on the excel link
     * 
     * @return the excel results page with the triples set.
     * 
     * @throws IOException
     */
    @SuppressWarnings("unused")
    @OnEvent("excelEvent")
    private Object excel() throws IOException{
        excelResults.setQuery(query);
        return excelResults;
    }

    /**
     * Handles clicks on the excel link
     * 
     * @return the excel results page with the triples set.
     * 
     * @throws IOException
     */
    @SuppressWarnings("unused")
    @OnEvent("csvEvent")
    private Object csv() {
        delimeterResults.initialize(query, ",", "text/csv", "csv");
        return delimeterResults;
    }

    /**
     * Handles clicks on the excel link
     * 
     * @return the excel results page with the triples set.
     * 
     * @throws IOException
     */
    @SuppressWarnings("unused")
    @OnEvent("tsvEvent")
    private Object tsv() {
        delimeterResults.initialize(query, "\t", "text/tab-separated-values", "tsv");
        return delimeterResults;
    }

    /**
     * Return a list of datasets.
     * 
     * @return
     * @throws SprotocolException 
     * @throws IOException 
     */
    public Map<String,String> getDataSets() throws SprotocolException, IOException {
        return queryDataService.executeGetAllGraphNames(query);
    }

    /**
     * 
     * @return the label for a given dataset
     * @throws SprotocolException 
     * @throws IOException 
     */
    public String getDataSetValue() throws SprotocolException, IOException {
        return getDataSets().get(datasetKey);
    }

    /**
     *
     * Return the reliability score
     * 
     * @return a random number ;)
     * @throws SprotocolException 
     * @throws IOException 
     * 
     */
    public int getReliability() throws SprotocolException, IOException {
        return queryDataService.executeReliabilityScore(getDataSets());
    }

}
