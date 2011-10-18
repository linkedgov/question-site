package org.linkedgov.questions.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.linkedgov.questions.model.Pair;
import org.linkedgov.questions.model.Query;
import org.linkedgov.questions.model.Triple;
import org.linkedgov.questions.services.QueryDataService;
import org.linkedgov.questions.services.SparqlDao;

import uk.me.mmt.sprotocol.SelectResult;
import uk.me.mmt.sprotocol.SelectResultSet;
import uk.me.mmt.sprotocol.SparqlResource;

/**
 * This class generates the object needed by the dataTable component
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
        final List<Triple> triples = new ArrayList<Triple>();

        if (!query.isNull()) {            
            SelectResultSet results = sparqlDao.executeSelect(query.toSparqlString());

            for (SelectResult result : results.getResults()) {
                Triple triple = new Triple();    

                for (String variable : results.getHead() ) {
                    SparqlResource resource =  result.getResult().get(variable);
                    if (resource != null) {
                        System.err.println("This variable '"+variable+"' with this result: '"+resource.getValue()+"' was returned");
                    }
                    Pair<SparqlResource,String> sub = new Pair<SparqlResource,String>();
                    Pair<SparqlResource,String> pred = new Pair<SparqlResource,String>();
                    Pair<SparqlResource,String> obj = new Pair<SparqlResource,String>();
                    
                    if (variable.equals("sub")) {
                        sub.setFirst(resource);
                        triple.setSubject(sub);
                    } else if (variable.equals("pred")) {
                        pred.setFirst(resource);
                        triple.setPredicate(pred);
                    } else if (variable.equals("obj")) {
                        obj.setFirst(resource);
                        triple.setObject(obj);
                    } else if (variable.equals("cnt")) {
                        sub.setFirst(resource);
                        triple.setSubject(sub);
                    } else if (variable.equals("slabel") && resource != null) {
                        sub.setFirst(resource);
                        triple.setSubject(sub);
                    } else if (variable.equals("plabel") && resource != null) {
                        pred.setFirst(resource);
                        triple.setPredicate(pred);
                    } else if (variable.equals("olabel") && resource != null) {
                        obj.setFirst(resource);
                        triple.setObject(obj);
                    }
                }
                triples.add(triple);
            }
        }
        return triples;
    }
}
