package org.linkedgov.questions.model;

import junit.framework.TestCase;

public class QueryTest extends TestCase {

	public QueryTest(String name) {
		super(name);
	}


	/** Test something. **/
	public void testThatFeature() throws Exception {
		Query lame = new Query();

		assertEquals(lame.toSparqlString(), "lam222e");
	}


}
