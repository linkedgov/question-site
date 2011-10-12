package org.linkedgov.questions.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.RequestParameter;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.FormFragment;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.linkedgov.questions.model.Query;
import org.linkedgov.questions.model.QueryFilter;
import org.linkedgov.questions.services.StaticDataService;

/**
 * TODO: major question: make this all work as one form and then do validation etc, 
 * or carry on as now and populate the query object piecemeal whenever events happen on the selects.
 * 
 * @author Luke Wilson-Mawer <a href="http://viscri.co.uk">Viscri</a> for LinkedGov
 *
 */
@Import(library="Question.js")
public class Question {

	private static final String EDITOR_ID = "editorId";

	private static final String OBJECTS = "objects";

	private static final String PREDICATES = "predicates";

	private static final String ADD_FIRST_FILTER = "addFirstFilter";
	
	private static final String ADD_SECOND_FILTER = "addSecondFilter";
	
	private static final String ADD_FILTER = "addFilter";
	
	private static final String FIRST_FILTER_PREDICATE = "firstFilterPredicate";

	@Persist
	private Query query;
	
	@Persist
	@SuppressWarnings("unused")
	@Property
	private List<String> subjects;
	
	@Persist
	@SuppressWarnings("unused")
	@Property
	private List<String> predicates;
	
	@Persist
	@SuppressWarnings("unused")
	@Property
	private List<String> objects;
	
	@Inject
	private StaticDataService staticDataService;
	
	@Inject
	private JavaScriptSupport jsSupport;
	
	@Inject
	private ComponentResources resources;
	
	@Inject
	private Messages messages;
	
	@SuppressWarnings("unused")
	@SetupRender
	private void setup(){
		System.out.println("setupRender");
		query = new Query();
		
		subjects =  staticDataService.getClasses();
		
		//XXX: maybe these aren't required any more.
		predicates = new ArrayList<String>();
		objects = new ArrayList<String>();
	}
	
	@OnEvent(ADD_FIRST_FILTER)
	public Object handleAddFirstFilterEvent(@RequestParameter("subject") String subject){
		final List<String> predicates = staticDataService.getPredicates(subject);
		return generateSelectOptionsJson(predicates, PREDICATES);
	}
	
	@OnEvent(ADD_SECOND_FILTER)
	public Object handleAddSecondFilterEvent(
			@RequestParameter("subject") String subject, 
			@RequestParameter("predicate")  String predicate, 
			@RequestParameter("object") String object){
		final List<String> predicates = staticDataService.getPredicates(subject, new QueryFilter(predicate,object));
		return generateSelectOptionsJson(predicates, PREDICATES);
	}
	
	@OnEvent(FIRST_FILTER_PREDICATE)
	public Object handleFirstFilterPredicateInitializerCall(
			@RequestParameter("subject") String subject,
			@RequestParameter("predicate") String predicate){
		final List<String> objects = staticDataService.getObjects(subject, predicate);
		final JSONObject data = generateSelectOptionsJson(objects, OBJECTS);
		
		//TODO: make this smarter and perhaps put it into a service or something.
		if(subject.contains("postcode")){
			data.put(EDITOR_ID, "firstLocationObjectEditor");
		} else if(objects.size() < 100){
			data.put(EDITOR_ID, "firstSelectObjectEditor");
		} else {
			data.put(EDITOR_ID, "firstFreetextObjectEditor");
		}

		return data;
	}

	private JSONObject generateSelectOptionsJson(List<String> itemList, String name) {
		
		final JSONArray items = new JSONArray();
		final JSONObject data = new JSONObject();
		
		final JSONObject blankItem = new JSONObject();
		blankItem.put("value","");		
		blankItem.put("label",messages.get(name+"BlankLabel"));
		
		items.put(blankItem);
		for (String itemString : itemList) {
			final JSONObject item = new JSONObject();
			item.put("value", itemString);
			//TODO: this will need to be sorted out to use the rdfs:label once we have that functionality.
			item.put("label", itemString);
			items.put(item);
		}

		data.put(name, items);
		
		return data;
	}
	
	@AfterRender
	public void initJs(){		
		addAddFilterInitializerCall();
		addFirstFilterPredicateInitializerCall();
	}

	private void addFirstFilterPredicateInitializerCall() {
		final Link filterFirstPredicateEventLink = resources.createEventLink(FIRST_FILTER_PREDICATE);
		
		final JSONObject specs = new JSONObject();
		specs.put("url", filterFirstPredicateEventLink.toAbsoluteURI());
		
		jsSupport.addInitializerCall(FIRST_FILTER_PREDICATE, specs);
	}

	private void addAddFilterInitializerCall() {
		final Link addFirstFilterEventLink = resources.createEventLink(ADD_FIRST_FILTER);
		final Link addSecondFilterEventLink = resources.createEventLink(ADD_SECOND_FILTER);
		
		final JSONObject specs = new JSONObject();
		specs.put("firstFilterUrl", addFirstFilterEventLink.toAbsoluteURI());
		specs.put("secondFilterUrl", addSecondFilterEventLink.toAbsoluteURI());
		specs.put("id", ADD_FILTER);
		
		jsSupport.addInitializerCall(ADD_FILTER, specs);
	}
	
//	@OnEvent(value=EventConstants.VALUE_CHANGED, component="subject")
//	public void updateQuestionType(QuestionType questionType){
//		query.setQuestionType(questionType);
//	}
//	
//	@OnEvent(value=EventConstants.VALUE_CHANGED, component="subject")
//	public Object getPredicateZone(String subject){
//		query.setSubject(subject);
//		predicates = staticDataService.getPredicates(subject);
//		return predicateZone.getBody();
//	}
//	
//	@OnEvent(value=EventConstants.VALUE_CHANGED, component="predicate")
//	public Object getObjectZone(String predicate){
//		query.getFirstFilter().setPredicate(predicate);
//		objects = staticDataService.getObjects(query.getSubject(), predicate);
//		
////		System.out.println("predicate"+predicate);
////		if(objects == null || objects.isEmpty()){
////			return noObjectsBlock;
////		} else if(isLocationPredicate(predicate)){
////			objectBlock = locationObjectBlock;
////			System.out.println("HERE");
////		} else if(objects.size() < 20){
////			objectBlock = selectObjectBlock;
////		} else {
////			objectBlock = freetextObjectBlock;
////		}
//		
//		return objectZone.getBody();
//	}
//	
//	@OnEvent(value=EventConstants.VALUE_CHANGED, component="object")
//	public void handleFirstFilterObj(String object){
//		query.getFirstFilter().setObject(object);
//	}
	
	//TODO: make this more generic and implement it properly; 
	public boolean isLocationPredicate(String predicate){
		return predicate.contains("postcode");
	}
	
	@OnEvent("askQuestion")
	public Object askQuestion(){
		//TODO: put grid stuff in here.
		//TODO Mischa log CSV to log file for now
		
		
		return null;
	}
	
	public void setQuery(Query query) {
		this.query = query;
	}

	public Query getQuery() {
		if(this.query == null){
			this.query = new Query();
		}
		return this.query;
	}
}
