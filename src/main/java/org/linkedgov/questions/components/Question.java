package org.linkedgov.questions.components;

import org.apache.tapestry5.SelectModel;
import org.linkedgov.questions.model.WhereIsModel;

public class Question {

	public SelectModel getWhereIsModel() {
		return new WhereIsModel();
	}
}
