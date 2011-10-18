package org.linkedgov.questions.pages;

import java.util.List;

import org.linkedgov.questions.model.Triple;

public class CsvResults {

	@SuppressWarnings("unused")
	private void onActivate(List<Triple> context){
		System.out.println("Activate" + context);
	}
}
