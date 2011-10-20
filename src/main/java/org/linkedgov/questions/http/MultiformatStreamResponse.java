package org.linkedgov.questions.http;

import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.util.TextStreamResponse;

/**
 * Extension of {@Link TextStreamResponse} to return csv and tsv files with http headers set to try to make the browser pop up a download box.
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> for LinkedGov
 *
 */
public class MultiformatStreamResponse extends TextStreamResponse {

    private final String fileExtension;
    private final String contentType;

    public MultiformatStreamResponse(String contentType, String text, String fileExtension) {
        super(contentType, text);
        this.fileExtension = fileExtension;
        this.contentType = contentType;
    }

    @Override
    public void prepareResponse(Response response) {
        response.setHeader("Content-Type",contentType);
        response.setHeader("Content-Disposition", "attachment;filename=answer."+fileExtension);
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
    }
}
