package org.linkedgov.questions.components;


import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linkedgov.questions.model.Pair;
import org.linkedgov.questions.model.Triple;
import org.linkedgov.questions.services.QueryDataService;

import uk.me.mmt.sprotocol.BNode;
import uk.me.mmt.sprotocol.IRI;
import uk.me.mmt.sprotocol.Literal;
import uk.me.mmt.sprotocol.SparqlResource;

/**
 * Displays a literal, bNode, or URI.
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> and
 * @author <a href="http://mmt.me.uk/foaf.rdf#mischa">Mischa Tuffield</a> for LinkedGov
 *
 */
public class ResourceDisplay {

    /**
     * The resource to display.
     */
    @Parameter
    private Pair<SparqlResource,String> resource;

    /**
     * Optional, in the case of a bnode, this is the predicate.
     */
    @Parameter
    private String context;

    /**
     * The block which displays literals.
     */
    @Inject
    private Block literalBlock; 

    /**
     * The block which displays locations.
     */
    @Inject
    private Block locationBlock; 

    /**
     * The block which displays bnode links.
     */
    @Inject
    private Block bnodeLinkBlock; 

    /**
     * The block which displays bNodes or URIs.
     */
    @Inject
    private Block nonLiteralBlock;

    /**
     * QueryDataService service, to actually do the query with.
     */
    @Inject
    private QueryDataService queryDataService;

    /**
     * Returns the appropriate block, bNode or URI.
     * 
     * @return
     */
    public Block getDisplayBlock(){
        if (resource.getFirst() instanceof BNode && !StringUtils.isBlank(context)) {
            return getBlockForBnodeDisplay();
        } else if (resource.getFirst() instanceof IRI && !StringUtils.isBlank(context)) {
            return getBlockForURIDisplay();
        }

        return resource.getFirst() instanceof Literal ? literalBlock : nonLiteralBlock;
    }

    /**
     * This function is used to decide what to do with a URI in the Grid component, 
     * i.e. does the particular form of URI want to be treated as a special case 
     * 
     * @return a block to be redendered in the Grid Component
     */
    private Block getBlockForURIDisplay() {
        List<Triple> iriTriples = queryDataService.executeIRIQuery(getValue());

        System.err.println("This size of the triples for a Given IRI is"+iriTriples.size());
        boolean isAddress = false;
        for (Triple row : iriTriples) {
            if (row.getPredicate().getFirst().getValue().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type") && row.getObject().getFirst().getValue().equals("http://www.w3.org/2006/vcard/ns#Address")) {
                isAddress = true;
                break;
            }
        }

        if (isAddress) {
            resource.getFirst().setValue(makeAddressPretty(iriTriples));
        }

        return nonLiteralBlock;
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

        for (Triple row : triples) {
            if (row.getPredicate().getFirst().getValue().equals("http://www.w3.org/2006/vcard/ns#street-address")) {
                street = row.getObject().getFirst().getValue(); 
            } else if (row.getPredicate().getFirst().getValue().equals("http://www.w3.org/2006/vcard/ns#region")) {
                region = row.getObject().getFirst().getValue(); 
            } else if (row.getPredicate().getFirst().getValue().equals("http://www.w3.org/2006/vcard/ns#locality")) {
                locality = row.getObject().getFirst().getValue(); 
            } else if (row.getPredicate().getFirst().getValue().equals("http://www.w3.org/2006/vcard/ns#postcode")) {
                postcode = row.getObject().getFirst().getValue().substring(row.getObject().getFirst().getValue().lastIndexOf("/")+1); 
            }
        }

        return StringUtils.strip(street)+", "+StringUtils.strip(region)+", "+StringUtils.strip(locality)+", "+StringUtils.strip(postcode);
    }

    private Block getBlockForBnodeDisplay() {
        //TODO ongoing, special handling of given predicates in the Grid should be 
        //added here
        List<Triple> bnodeTriples = queryDataService.executeBnodeQuery(getValue());

        boolean isGeoPoint = false;
        for (Triple rows : bnodeTriples) {
            if (rows.getPredicate().getFirst().getValue().equals("http://www.w3.org/2003/01/geo/wgs84_pos#Point")) {
                isGeoPoint = true;
                break;
            }
        }
        
        System.err.println("The size of these triples is"+bnodeTriples.size());
        if (bnodeTriples.size() == 1) {
            resource.getFirst().setValue(bnodeTriples.get(0).getObject().getFirst().getValue());
            return literalBlock;
        } else if (isGeoPoint){
            return locationBlock;
        }

        return bnodeLinkBlock;
    }

    /**
     * Gets a string representation of a reading.
     */
    public String getLocationString(){ 
        //TODO: Ongoing this is where the smarts for the geoPoint would go in
        return "Here would be a link to the OSM of something";
    }

    /**
     * Returns a boolean representing whether the label should be displayed or not.
     * 
     * @return a boolean representing whether the label should be displayed or not.
     */
    public boolean isLabelDisplayed(){
        return StringUtils.isNotBlank(resource.getSecond());
    }

    /**
     * Returns the label.
     * 
     * @return a string of the label
     */
    public String getLabel(){
        return resource.getSecond();
    }

    /**
     * @return the actual value (e.g. the URI for a uri, a string representing a bnode, or the literal value.
     */
    public String getValue(){
        return resource.getFirst().getValue();
    }

}
