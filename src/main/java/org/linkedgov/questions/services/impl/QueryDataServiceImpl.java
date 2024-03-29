package org.linkedgov.questions.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.linkedgov.questions.model.Pair;
import org.linkedgov.questions.model.Query;
import org.linkedgov.questions.model.QuestionType;
import org.linkedgov.questions.model.SparqlUtils;
import org.linkedgov.questions.model.Triple;
import org.linkedgov.questions.services.QueryDataService;
import org.linkedgov.questions.services.SparqlDao;
import org.slf4j.Logger;

import uk.me.mmt.sprotocol.BNode;
import uk.me.mmt.sprotocol.IRI;
import uk.me.mmt.sprotocol.Literal;
import uk.me.mmt.sprotocol.SelectResultRow;
import uk.me.mmt.sprotocol.SelectResultSet;
import uk.me.mmt.sprotocol.SparqlResource;
import uk.me.mmt.sprotocol.SprotocolException;

/**
 * This class generates the object needed by the dataTable component
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> and 
 * @author <a href="http://mmt.me.uk/foaf.rdf#mischa">Mischa Tuffield</a> for LinkedGov
 * 
 */
public class QueryDataServiceImpl implements QueryDataService {

    /**
     * Used to get the reliability score
     */
    private static final String GET_RELIABILITY = "SELECT DISTINCT ?rel WHERE " +
    "{<%s> <http://data.linkedgov.org/ns#reliability> ?rel } LIMIT 1 ";

    private static final String XSD_INTEGER = "http://www.w3.org/2001/XMLSchema#integer";

    /**
     * To do the actual querying with.
     */
    private final SparqlDao sparqlDao;

    /**
     * To log stuff with.
     */
    private final Logger log;

    /**
     * Automatically called by tapestry when instantiating the service, which is a singleton.
     */
    public QueryDataServiceImpl(SparqlDao sparqlDao, Logger log){
        this.sparqlDao = sparqlDao;
        this.log = log;
    }

    /**
     * Get results for the user's question.
     * 
     * @param Query object representing a user's question.
     * @return a list of triples representing the answer to the question.
     * @throws IOException 
     */
    public List<Triple> executeQuery(Query query) throws SprotocolException, IOException {
        return executeQuery(query, null, null, null) ;
    }

    /**
     * Converts a result into a triple.
     * 
     * @param head a list of the variable names in the results.
     * @param result the result to convert.
     * @return the triple that represents the result. 
     */
    private Triple resultToTriple(List<String> head, SelectResultRow result) {
        final Triple triple = new Triple();    

        for (String variable : head) {
            final SparqlResource resource = result.get(variable);

            final Pair<SparqlResource,String> sub = new Pair<SparqlResource,String>();
            final Pair<SparqlResource,String> pred = new Pair<SparqlResource,String>();
            final Pair<SparqlResource,String> obj = new Pair<SparqlResource,String>();

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
                //Literal newLit = new Literal(WordUtils.capitalize(resource.getValue()),null,null);
                pred.setFirst(resource);
                pred.setSecond(WordUtils.capitalize(resource.getValue()));
                triple.setPredicate(pred);
            } else if (variable.equals("olabel") && resource != null) {
                obj.setFirst(resource);
                triple.setObject(obj);
            }
        }

        return triple;
    }

    /**
     * This function shows how if a URI of a certain rdf:type is returned it can be 
     * implemented as a special case
     */
    private String makeAddressPretty(List<Triple> triples) {
        String street = "";
        String region = "";
        String locality = "";
        String postcode = "";
        for (Triple triple : triples) {
            if (triple.getPredicate().getFirst().getValue().equals("http://www.w3.org/2006/vcard/ns#street-address")) {
                street = triple.getObject().getFirst().getValue(); 
            } else if (triple.getPredicate().getFirst().getValue().equals("http://www.w3.org/2006/vcard/ns#region")) {
                region = triple.getObject().getFirst().getValue(); 
            } else if (triple.getPredicate().getFirst().getValue().equals("http://www.w3.org/2006/vcard/ns#locality")) {
                locality = triple.getObject().getFirst().getValue(); 
            } else if (triple.getPredicate().getFirst().getValue().equals("http://www.w3.org/2006/vcard/ns#postcode")) {
                postcode = triple.getObject().getFirst().getValue().substring(triple.getObject().getFirst().getValue().lastIndexOf("/")+1); 
            }
        }
        String pretty = StringUtils.strip(street)+", "+StringUtils.strip(region)+", "+StringUtils.strip(locality)+", "+StringUtils.strip(postcode);
        pretty.replaceAll(", ,", "");
        return pretty;
    }

    public List<Triple> executeQuery(Query query, Integer limit, Integer offset, String orderBy) throws SprotocolException, IOException {
        final List<Triple> triples = new ArrayList<Triple>();

        if (!query.isNull()) {          
            final String sparqlString = query.toSparqlString();
            log.info("SPARQL ASKED:{}", sparqlString);
            log.info("QUESTION ASKED:{}", query.toString());
            final SelectResultSet results = sparqlDao.executeQuery(sparqlString, limit, offset, orderBy);
            for (SelectResultRow result : results) {
                final Triple triple = resultToTriple(results.getHead(), result);
                triples.add(triple);
            }
        }

        //Specialising bnode.size = 1 and where we define vcard#Address functionality
        final List<Triple> finalTriples = new ArrayList<Triple>();
        for (Triple triple : triples) {
            if (triple.getObject().getFirst() instanceof IRI) {
                List<Triple> iriTriples = executeIRIQuery(triple.getObject().getFirst().getValue());
                log.info("This size of the triples for a Given IRI is"+iriTriples.size());
                boolean isAddress = false;
                for (Triple row : iriTriples) {
                    if (row.getPredicate().getFirst().getValue().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type") && row.getObject().getFirst().getValue().equals("http://www.w3.org/2006/vcard/ns#Address")) {
                        isAddress = true;
                        break;
                    }
                }

                //Am now handling the special cases here 
                if (isAddress) {
                    Triple newTriple = new Triple();
                    newTriple.setSubject(triple.getSubject());
                    newTriple.setPredicate(triple.getPredicate());
                    //(makeAddressPretty(iriTriples)
                    newTriple.setObject(triple.getObject());
                    newTriple.getObject().setSecond((makeAddressPretty(iriTriples)));
                    //Literal newObject = new Literal(makeAddressPretty(iriTriples),null,null);
                    //newTriple.setObject(new Pair<SparqlResource,String>(newObject,null));
                    finalTriples.add(newTriple);
                } else {
                    finalTriples.add(triple);
                }
            } else if (triple.getObject().getFirst() instanceof BNode) {
                List<Triple> bnodeTriples = executeBnodeQuery(triple.getObject().getFirst().getValue());

                if (bnodeTriples.size() == 1) {
                    Triple newTriple = new Triple();
                    newTriple.setSubject(triple.getSubject());
                    newTriple.setPredicate(triple.getPredicate());
                    newTriple.setObject(triple.getObject());
                    newTriple.getObject().setSecond(bnodeTriples.get(0).getObject().getFirst().getValue());
                    //Literal newObject = new Literal(bnodeTriples.get(0).getObject().getFirst().getValue(),null,null);
                    //newTriple.setObject(new Pair<SparqlResource,String>(newObject,null));
                    finalTriples.add(newTriple);
                } else {
                    finalTriples.add(triple);
                }
            } else {
                finalTriples.add(triple);
            }
        }

        return triples;
    }

    /**
     * Executes a count for this query. If the query itself is a count, it returns 1.
     * @throws IOException 
     */
    public int executeCountForQuery(Query query, boolean forPagination) throws SprotocolException, IOException {

        if (QuestionType.COUNT.equals(query.getQuestionType())) {
            return 1;
        }
        if (query.isNull()) {
            return 0;
        }

        final String countSparqlString = query.toSparqlString(QuestionType.COUNT, forPagination, false);
        final SelectResultSet results = sparqlDao.executeQuery(countSparqlString);

        if (!results.iterator().hasNext()) {
            return 0;
        }

        final String countLabel = results.getHead().get(0);
        final SelectResultRow firstResult = results.iterator().next();
        final String count = firstResult.get(countLabel).getValue();

        if(count == null){
            return 0;
        }

        return Integer.valueOf(count);
    }

    /**
     * This get all of the dataset's used to answer a query created by the user
     * @throws IOException 
     */
    public Map<String,String> executeGetAllGraphNames(Query query) throws SprotocolException, IOException {
        final String queryGraphs = query.toSparqlString(QuestionType.SELECT, false, true);
        final SelectResultSet graphs = sparqlDao.executeQuery(queryGraphs);

        Map<String,String> retValues = new HashMap<String,String>();

        for (SelectResultRow result : graphs) {
            final SparqlResource element = result.get("g");
            if (result.get("glabel") != null) {
                retValues.put(element.getValue(),result.get("glabel").getValue());
            } else {
                retValues.put(element.getValue(),element.getValue());
            }            
        }
        return retValues;             
    }

    /**
     * This get the reliability score by querying the knowledge base
     * 
     * @return the average reliability score of the graphs or 0
     * @throws IOException 
     */
    public int executeReliabilityScore(Map<String,String> graphs) throws SprotocolException, IOException {
        int reliability = 0;
        int count = 0;
        for (String dataSet : graphs.keySet()) {
            String query = String.format(GET_RELIABILITY, dataSet);
            SelectResultSet results = sparqlDao.executeQuery(query);
            for (SelectResultRow result : results) {
                SparqlResource element = result.get("rel");
                if (element instanceof Literal) {
                    if (((Literal) element).getDatatype().equals(XSD_INTEGER)) {
                        if (SparqlUtils.isInteger(element.getValue())) {
                            reliability = reliability + Integer.parseInt(element.getValue());
                            count++;
                        }
                    }
                }        
            }
        }
        if (reliability > 0 && count > 0) {
            reliability = reliability / count;
        } 
        return reliability;
    }

    /**
     * 
     * @param an iri is passed in, this is used to perform the special case's
     * the only special case currently implemented is the vcard#Address
     * @return a list of triples
     * @throws IOException 
     */
    public List<Triple> executeIRIQuery(String iri) throws SprotocolException, IOException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT DISTINCT (<");
        query.append(iri);
        query.append("> as ?sub) ?pred ?obj ");
        query.append("WHERE { <");
        query.append(iri);
        query.append("> ?pred ?obj . ");    
        query.append("}");

        final String sparqlString = query.toString();
        log.info("SPARQL ASKED to grab IRI Info:{}", sparqlString);
        final SelectResultSet results = sparqlDao.executeQuery(sparqlString);
        final List<Triple> triples = new ArrayList<Triple>();

        for (SelectResultRow result : results) {
            final Triple triple = resultToTriple(results.getHead(), result);
            triples.add(triple);
        }    
        return triples;
    }

    /**
     * This function is used to return triples about a given Bnode
     * This is used to fill out the Grid Component 

     * @param a bnode id is passed in, this is used to perform the special case's
     * the only special case currently implemented is the "single bnode case"
     * @return a list of triples
     * @throws IOException 
     */
    public List<Triple> executeBnodeQuery(String bnode) throws SprotocolException, IOException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT DISTINCT (<bnode:");
        query.append(bnode);
        query.append("> as ?sub) ?pred ?obj ?slabel ?plabel ?olabel ");
        query.append("WHERE { <bnode:");
        query.append(bnode);
        query.append("> ?pred ?obj . ");    
        query.append("OPTIONAL {<bnode:");
        query.append(bnode);
        query.append("> <http://www.w3.org/2000/01/rdf-schema#label> ?slabel } . ");
        query.append("OPTIONAL {?pred <http://www.w3.org/2000/01/rdf-schema#label> ?plabel } . ");
        query.append("OPTIONAL {?obj <http://www.w3.org/2000/01/rdf-schema#label> ?olabel } . ");
        query.append("}");

        final String sparqlString = query.toString();
        log.info("SPARQL ASKED to grab Bnode Info:{}", sparqlString);
        final SelectResultSet results = sparqlDao.executeQuery(sparqlString);
        final List<Triple> triples = new ArrayList<Triple>();

        for (SelectResultRow result : results) {
            final Triple triple = resultToTriple(results.getHead(), result);
            triples.add(triple);
        }    
        return triples;
    }
}
