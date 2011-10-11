package org.linkedgov.questions.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

/**
 * 
 * Luke Wilson-Mawer <a href="http://viscri.co.uk">Viscri</a> for LinkedGov
 *
 */
@Import(stylesheet="context:layout/layout.css")
public class Layout
{
    /** The page title, for the <title> element and the <h1> element. */
    @SuppressWarnings("unused")
	@Property
    @Parameter(required = true, defaultPrefix = BindingConstants.LITERAL)
    private String title;

    @SuppressWarnings("unused")
	@Property
    private String pageName;

    @SuppressWarnings("unused")
	@Property
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String sidebarTitle;

    @SuppressWarnings("unused")
	@Property
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private Block sidebar;

}
