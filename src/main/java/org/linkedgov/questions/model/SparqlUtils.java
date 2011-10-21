package org.linkedgov.questions.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Util class to check if something is a URI or a literal or a bnode either. 
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk/">Viscri</a> and 
 * @author <a href="http://mmt.me.uk/foaf.rdf#mischa">Mischa Tuffield</a> for LinkedGov
 */
public class SparqlUtils {

    /**
     * This enumerates a list of well known URI prefixes
     * 
     */
    public final static List<String> URI_PREFIXES;
    static {
        final ArrayList<String> lst = new ArrayList<String>();
        lst.add("http:");
        lst.add("ftp");
        lst.add("tag");
        lst.add("urn");
        lst.add("mailto");
        lst.add("tel");
        URI_PREFIXES = Collections.unmodifiableList(lst);
    };
    
    /**
     * 
     * @param input a literal value returned by 4store
     * @return checks whether or not it is a URI
     */
    public static boolean isURI (String input) {
        boolean isURI = false;
        for (String prefix : URI_PREFIXES) {
            if (input.startsWith(prefix)) {
                isURI = true;
                break;
            }
        }
        return isURI;
    }

    /**
     * 
     * @param input a literal 
     * @return whether or not it is an Integer
     */
    public static boolean isInteger(String input) {  
        try {  
            Integer.parseInt(input);  
            return true;  
        } catch(Exception e) {  
            return false;  
        }  
    } 

    /**
     * 
     * @param input which is a literal value
     * @return a boolean stating with it is a floating point number or not
     */
    public static boolean isFloat (String input) {
        try {
            Float.parseFloat(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * This is 4store specific ...
     * 
     * @param input which is a literal value
     * @return a boolean stating whether something is a bnode or not
     */
    public static boolean isBnode (String input) {
        if (input.startsWith("b") && input.length() > 16 && !input.contains(" ")) {
            return true;
        }
        return false;
    }

}
