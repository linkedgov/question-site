package org.linkedgov.questions.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.SelectModelVisitor;
import org.apache.tapestry5.internal.OptionModelImpl;

public class WhereIsModel implements SelectModel {

	public List<OptionGroupModel> getOptionGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<OptionModel> getOptions() {

		final List<OptionModel> optionModels = new ArrayList<OptionModel>();
		
		optionModels.add(new OptionModelImpl("How Many"));
		optionModels.add(new OptionModelImpl("How Much"));
		optionModels.add(new OptionModelImpl("When"));
		optionModels.add(new OptionModelImpl("Where"));
		
		return optionModels;
	}

	public void visit(SelectModelVisitor visitor) {
		// TODO Auto-generated method stub
		
	}

}
