package org.linkedgov.questions.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.linkedgov.questions.model.Pair;
import org.linkedgov.questions.model.Query;
import org.linkedgov.questions.model.Triple;
import org.linkedgov.questions.services.QueryDataService;
import org.linkedgov.questions.services.SparqlDao;

import uk.me.mmt.sprotocol.IRI;
import uk.me.mmt.sprotocol.Literal;
import uk.me.mmt.sprotocol.SparqlResource;

/**
 * TODO: this isn't actually used anymore. See if it ends up being required and deleted it if not.
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> and 
 * @author <a href="http://mmt.me.uk/foaf.rdf#mischa">Mischa Tuffield</a> for LinkedGov
 * 
 */
public class QueryDataServiceImpl implements QueryDataService {

	private final SparqlDao sparqlDao;
	
	public QueryDataServiceImpl(SparqlDao sparqlDao){
		this.sparqlDao = sparqlDao;
	}
	
	public List<Triple> executeQuery(Query query) { 
		
		//TODO: Mischa to parse this and return a list of triples.
		sparqlDao.executeSelect(query.toSparqlString());
	
		final List<Triple> triples = new ArrayList<Triple>();
		final IRI subject = new IRI();
		subject.setValue("http://viscri.co.uk/myResource");
		
		final IRI predicate = new IRI();
		predicate.setValue("http://viscri.co.uk/date");
	
		final Literal object = new Literal();
		object.setValue("2002-01-01");
		object.setLanguage("en");
		object.setDatatype("datatype");
		
		final Pair<SparqlResource, String> labelSubjectPair = new Pair<SparqlResource,String>(subject,"subjectLabel");
		final Pair<SparqlResource, String> labelPredicatePair = new Pair<SparqlResource,String>(predicate,"predicateLabel");
		final Pair<SparqlResource, String> labelObjectPair = new Pair<SparqlResource,String>(object,"predicateLabel");	
		final Triple tripleWithLabels = new Triple(labelSubjectPair, labelPredicatePair, labelObjectPair);	
		
		final Pair<SparqlResource, String> noLabelSubjectPair = new Pair<SparqlResource,String>(subject,null);
		final Pair<SparqlResource, String> noLabelPredicatePair = new Pair<SparqlResource,String>(predicate,null);
		final Pair<SparqlResource, String> noLabelObjectPair = new Pair<SparqlResource,String>(object,null);	
		final Triple tripleNoLabels = new Triple(noLabelSubjectPair, noLabelPredicatePair, noLabelObjectPair);	
		
		sparqlDao.executeSelect(query.toSparqlString());
		
		triples.add(tripleWithLabels);
		triples.add(tripleNoLabels);
		triples.add(tripleNoLabels);
		triples.add(tripleWithLabels);
		triples.add(tripleWithLabels);
		triples.add(tripleNoLabels);
		
		return triples;
	}

}
