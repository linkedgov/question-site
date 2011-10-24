package org.linkedgov.questions.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.services.Response;

/**
 * Implementation of {@Link StreamResponse} for excel files.
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> for LinkedGov
 *
 */
public class ExcelStreamResponse implements StreamResponse {

    private final byte[] bytes;

    public ExcelStreamResponse(byte[] bytes){
        this.bytes = bytes;
    }

    public String getContentType() {
        return "application/vnd.ms-excel";
    }

    public InputStream getStream() throws IOException {
        return new ByteArrayInputStream(bytes);
    }

    public void prepareResponse(Response response) {
        response.setHeader("Content-Type", getContentType());
        response.setHeader("Content-Disposition", "attachment;filename=answer.xls");
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setHeader("Content-Disposition", "attachment;filename=answer.xls");
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
    }


}
