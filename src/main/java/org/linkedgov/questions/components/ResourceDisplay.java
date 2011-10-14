package org.linkedgov.questions.components;


import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linkedgov.questions.model.Pair;

import uk.me.mmt.sprotocol.Literal;
import uk.me.mmt.sprotocol.SparqlResource;

public class ResourceDisplay {

	@Parameter
	private Pair<SparqlResource,String> resource;

	@Inject
	private Block literalBlock; 
	
	@Inject
	private Block nonLiteralBlock;
	
	public Block getDisplayBlock(){
		//perhaps we could use polymorphism more smartly to avoid this instanceof call.
		return resource.getFirst() instanceof Literal ? literalBlock : nonLiteralBlock;
	}
	
	public boolean isLabelDisplayed(){
		return resource.getSecond() != null;
	}
	
	public String getLabel(){
		return resource.getSecond();
	}
	
	public String getValue(){
		return resource.getFirst().getValue();
	}
	
}
