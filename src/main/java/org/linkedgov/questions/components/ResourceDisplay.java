package org.linkedgov.questions.components;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linkedgov.questions.model.Pair;
import org.linkedgov.questions.model.Triple;
import org.linkedgov.questions.services.QueryDataService;

import uk.me.mmt.sprotocol.BNode;
import uk.me.mmt.sprotocol.Literal;
import uk.me.mmt.sprotocol.SparqlResource;
import uk.me.mmt.sprotocol.SprotocolException;

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
     * @throws SprotocolException 
     * @throws IOException 
     */
    public Block getDisplayBlock() throws SprotocolException, IOException{
        if (resource.getFirst() instanceof BNode && !StringUtils.isBlank(context)) {
            return getBlockForBnodeDisplay();
        } 

        return resource.getFirst() instanceof Literal ? literalBlock : nonLiteralBlock;
    }

    /**
     * This function is used to decide what to do with a BNode in the Grid component, 
     * i.e. does the particular class of bnode want to be treated as a special case ?
     * 
     * @return a block to be rendered in the Grid Component
     * @throws SprotocolException 
     * @throws IOException 
     */
    private Block getBlockForBnodeDisplay() throws SprotocolException, IOException {
        //TODO ongoing, special handling of given predicates in the Grid should be added here
        List<Triple> bnodeTriples = queryDataService.executeBnodeQuery(getValue());

        boolean isGeoPoint = false;
        for (Triple rows : bnodeTriples) {
            if (rows.getPredicate().getFirst().getValue().equals("http://www.w3.org/2003/01/geo/wgs84_pos#Point")) {
                isGeoPoint = true;
                break;
            }
        }
        
        if (isGeoPoint){
            return locationBlock;
        }

        if (resource.getFirst() instanceof BNode && resource.getSecond() != null) {
            return literalBlock; 
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
        if (resource.getSecond() != null ) {
            return resource.getSecond();
        }
        return resource.getFirst().getValue();
    }
    
}
