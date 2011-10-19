package org.linkedgov.questions.http;

import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.util.TextStreamResponse;

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
