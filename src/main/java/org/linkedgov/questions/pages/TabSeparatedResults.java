package org.linkedgov.questions.pages;

import java.util.List;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Persist;
import org.linkedgov.questions.http.MultiformatStreamResponse;
import org.linkedgov.questions.model.Triple;

/**
 *  Class that serves up tab separated results based on the Grid component
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> 
 * @author <a hred="http://mmt.me.uk/foaf.rdf#mischa">Mischa Tuffield</a> for LinkedGov
 *
 */
public class TabSeparatedResults {

    /**
     * A list of triples representing the results.
     */
    @Persist
    private List<Triple> triples;

    /**
     * This is used to iterate through the triples used to generate the Grid component
     * 
     * @return returns a tsv file to the user via their browser
     */
    @SuppressWarnings("unused")
    private StreamResponse onActivate(){
        final StreamResponse streamResponse;

        StringBuilder tsv = new StringBuilder();
        String object = "";
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

    /**
     * Set the triples to be outputted.
     * 
     * @param triples
     */
    public void setTriples(List<Triple> triples) {
        this.triples = triples;
    }
}
