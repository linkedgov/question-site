package org.linkedgov.questions.pages;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.util.TextStreamResponse;
import org.linkedgov.questions.model.Query;
import org.linkedgov.questions.model.Triple;
import org.linkedgov.questions.services.QueryDataService;

import uk.me.mmt.sprotocol.SprotocolException;

/**
 * Class that serves up comma separated results from the ResultsGrid in the UI
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> and 
 * @author <a href="http://mmt.me.uk/foaf.rdf#mischa">Mischa Tuffield</a> for LinkedGov
 *
 */
public class DelimeterSeparatedResults {
  
    @Inject
    private QueryDataService queryDataService;
    
    @Persist
    private String delimeter;
    
    @Persist
    private String contentType;
    
    @Persist
    private String fileExtension;

    /**
     * A list of triples representing the results.
     */
    @Persist
    private Query query;
    
    @Inject
    private Response response;
    
    /**
     * Amount to be read from the database in one go.
     */
    private static final int LIMIT = 1000;
    
    /**
     * Offset
     */
    private int offset;
    
    /**
     * List of triples.
     */
    private List<Triple> triples;

    /**
     * This is used to iterate through the triples used to generate the Grid component
     * 
     * @return returns a csv file to the user via their browser
     * @throws SprotocolException 
     * @throws IOException  
     */
    public StreamResponse onActivate() throws SprotocolException, IOException {
        final PrintWriter printWriter = setupPrintWriter();
        streamTriples(printWriter);
        
        printWriter.flush();
        printWriter.close();
        
        //Return this to appease tapestry
        return new TextStreamResponse(contentType, "");
    }

    private void streamTriples(final PrintWriter writer) throws IOException {
        triples = getChunkOfResults(offset);
        while(triplesLeft()){
            writeChunkToStream(writer, triples);
            offset+=LIMIT;
            triples = getChunkOfResults(offset);        
        }
    }

    private boolean triplesLeft() {
        return triples != null && !triples.isEmpty();
    }

    private PrintWriter setupPrintWriter() throws IOException {
        final PrintWriter writer = response.getPrintWriter(contentType);
        writeColumnHeadings(writer);
        offset = 0;
        setResponseHeaders();
        return writer;
    }

    private void writeColumnHeadings(PrintWriter writer) {
        writer.append("Result Subject");
        writer.append(delimeter);
        writer.append("Result Predicate");   
        writer.append(delimeter);
        writer.append("Result Object");   
        writer.append("\n");
    }

    private void setResponseHeaders() {
        response.setHeader("Content-Type",contentType);
        response.setHeader("Content-Disposition", "attachment;filename=answer."+fileExtension);
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
    }

    private List<Triple> getChunkOfResults(int offset) throws IOException {
        return queryDataService.executeQuery(query, LIMIT, offset,  null);
    }

    private void writeChunkToStream(final PrintWriter writer,
            final List<Triple> triples) {
        for (Triple row : triples) {
            appendLine(writer, row);
        }
        writer.flush();
    }

    private void appendLine(PrintWriter writer, Triple row) {
        String object;
        writer.append(row.getSubject().getFirst().getValue());
        writer.append(delimeter);
        writer.append(row.getPredicate().getFirst().getValue());
        writer.append(delimeter);
        object = row.getObject().getFirst().getValue();
        if (object.contains("\"")) {
            object = object.replace("\"", "\"\"");
            object = "\""+object+"\"";
        } else if (object.contains(",")) {
            object = "\""+object+"\"";
        } 
        writer.append(object);
        writer.append("\n");
    }

    public void initialize(Query query, String delimeter, String contentType, String fileExtension) {
        this.query = query;
        this.delimeter = delimeter;
        this.contentType = contentType;
        this.fileExtension = fileExtension;
    }

    public void s(String delimeter) {
        this.delimeter = delimeter;
    }


}
