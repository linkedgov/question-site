package org.linkedgov.questions.pages;

import java.io.IOException;
import java.util.List;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linkedgov.questions.http.MultiformatStreamResponse;
import org.linkedgov.questions.model.Query;
import org.linkedgov.questions.model.Triple;
import org.linkedgov.questions.services.QueryDataService;

import uk.me.mmt.sprotocol.SprotocolException;

/**
 *  Class that serves up tab separated results based on the Grid component
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> 
 * @author <a hred="http://mmt.me.uk/foaf.rdf#mischa">Mischa Tuffield</a> for LinkedGov
 *
 */
public class TabSeparatedResults {

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
     * @return returns a tsv file to the user via their browser
     * @throws SprotocolException 
     * @throws IOException  
     */
    @SuppressWarnings("unused")
    private StreamResponse onActivate() throws SprotocolException, IOException {
        final StreamResponse streamResponse;
        //TODO: Ongoing. stream this response.
        //TODO: Ongoing. stream this response.
        final List<Triple> triples = queryDataService.executeQuery(query, 1000, 0,  null);

        StringBuilder tsv = new StringBuilder();
        String object = "";
        tsv.append("Result Subject\tResult Predicate\tResult Object\n");
        for (Triple row : triples) {
            tsv.append(row.getSubject().getFirst().getValue());
            tsv.append("\t");
            tsv.append(row.getPredicate().getFirst().getValue());
            tsv.append("\t");
            object = row.getObject().getFirst().getValue();
            if (object.contains("\"")) {
                object.replace("\"", "\"\"");
                object = "\""+object+"\"";
            } else if (object.contains("\t")) {
                object = "\""+object+"\"";
            }
            tsv.append(object);
            tsv.append("\n");
        }

        return new MultiformatStreamResponse("text/tab-separated-values", tsv.toString(),"tsv");
    }

    public void setQuery(Query query) {
        this.query = query;
    }
}
