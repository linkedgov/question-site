 package org.linkedgov.questions.pages;

import org.apache.tapestry5.annotations.Property;

public class Bnode {

    @SuppressWarnings("unused")
    @Property
    private String bnodeString;
    
    public void onActivate(String bnodeString){
        this.bnodeString = bnodeString;        
    }
}
