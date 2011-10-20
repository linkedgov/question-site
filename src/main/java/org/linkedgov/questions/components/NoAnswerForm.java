package org.linkedgov.questions.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linkedgov.questions.model.Query;
import org.slf4j.Logger;

/**
 * Form that comes up when you don't answer.
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> for LinkedGov
 *
 */
public class NoAnswerForm {
   
    @Parameter
    private Query query;
    
    @Inject
    private Logger log;
    
    @Property
    private String freeText; 
    
    @Property
    private String email;
    
    @Inject
    private Block formBlock;
    
    @Inject
    private Block thanksBlock;
    
    @OnEvent(EventConstants.SUCCESS)
    public void onSuccess(){
        log.info("SOMEBODY HAD NO ANSWERS TO THEIR QUERY - FreeText:[{}], Email:[{}], Query:[{}]",
                new Object[]{freeText,email,query.toString()});
    }
    
    public Block getNoAnswerBlock(){
        return email == null ? formBlock : thanksBlock;
    }
}
