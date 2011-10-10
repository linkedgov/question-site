package org.linkedgov.questions.services;

import uk.me.mmt.sprotocol.SelectResultSet;

public interface SparqlDao {

	public SelectResultSet executeSelect(String query);
	
	public int executeCount(String query);
}
