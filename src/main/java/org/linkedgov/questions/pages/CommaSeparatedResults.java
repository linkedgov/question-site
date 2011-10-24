package org.linkedgov.questions.pages;

import java.util.List;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linkedgov.questions.http.MultiformatStreamResponse;
import org.linkedgov.questions.model.Query;
import org.linkedgov.questions.model.Triple;
import org.linkedgov.questions.services.QueryDataService;

/**
 * Class that serves up comma separated results from the ResultsGrid in the UI
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> and 
 * @author <a href="http://mmt.me.uk/foaf.rdf#mischa">Mischa Tuffield</a> for LinkedGov
 *
 */
public class CommaSeparatedResults {

    @Inject
    private QueryDataService queryDataService;
    
    /**
     * A list of triples representing the results.
     */
    @Persist
    private Query query;

    /**
     * This is used to iterate through the triples used to generate the Grid component
     * 
     * @return returns a csv file to the user via their browser
     */
    @SuppressWarnings("unused")
    private StreamResponse onActivate(){
        final StreamResponse streamResponse;
        //TODO: Ongoing. Stream this response.
        final List<Triple> triples = queryDataService.executeQuery(query, 1000, 0,  null);

        StringBuilder csv = new StringBuilder();
        String object = "";
        csv.append("Result Subject,Result Predicate,Result Object\n");
        for (Triple row : triples) {
            csv.append(row.getSubject().getFirst().getValue());
            csv.append(",");
            csv.append(row.getPredicate().getFirst().getValue());
            csv.append(",");
            object = row.getObject().getFirst().getValue();
            if (object.contains("\"")) {
                object.replace("\"", "\"\"");
                object = "\""+object+"\"";
            } else if (object.contains(",")) {
                object = "\""+object+"\"";
            } 
            csv.append(object);
            csv.append("\n");
        }

        return new MultiformatStreamResponse("text/csv", csv.toString(),"csv");
    }

    public void setQuery(Query query) {
        this.query = query;
    }

}
