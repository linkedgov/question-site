package org.linkedgov.questions.components;


import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linkedgov.questions.model.Pair;

import uk.me.mmt.sprotocol.BNode;
import uk.me.mmt.sprotocol.Literal;
import uk.me.mmt.sprotocol.SparqlResource;

/**
 * Displays a literal, bNode, or URI.
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> for LinkedGov
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
     * The block which displays readings.
     */
    @Inject
    private Block readingBlock; 
    
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
     * Returns the appropriate block, bNode or URI.
     * 
     * @return
     */
    public Block getDisplayBlock(){
        
        if(resource.getFirst() instanceof BNode && !StringUtils.isBlank(context)){
            
            return getBlockForBnodeDisplay();
        }
        
        //perhaps we could use polymorphism more smartly to avoid this instanceof call.
        return resource.getFirst() instanceof Literal ? literalBlock : nonLiteralBlock;
    }

    private Block getBlockForBnodeDisplay() {
        //TODO: mischa, replace these tests with ones for real predicates.
        if(context.contains("location")){
            return locationBlock;
        } else if(context.contains("reading")){
            return readingBlock;
        }
        
        System.out.println(context.contains("location")+"   "+context);
        
        return bnodeLinkBlock;
    }
    
    /**
     * Gets a string representation of a reading.
     */
    public String getReadingString(){ 
        //TODO: Mischa, put a service call here.
        return "SOME METER READING";
    }
    
    /**
     * Gets a string representation of a reading.
     */
    public String getLocationString(){ 
        //TODO: Mischa, put a service call here.
        return "10 hat street, onion road, townsville.";
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
     * @return
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
