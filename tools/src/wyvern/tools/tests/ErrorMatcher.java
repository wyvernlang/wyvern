package wyvern.tools.tests;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import wyvern.tools.errors.ToolError;
import wyvern.tools.errors.ErrorMessage;

class ErrorMatcher extends BaseMatcher<ToolError> {
	public ErrorMatcher(ErrorMessage msg) {
		this.msg = msg;
	}
	private ErrorMessage msg;
	
	@Override
	public boolean matches(Object item) {
		if (!(item instanceof ToolError))
			return false;
		ErrorMessage otherMessage = ((ToolError) item).getTypecheckingErrorMessage();
		return otherMessage == msg;
	}
	@Override
	public void describeTo(Description description) {
		description.appendText("What is this description of ErrorMatcher supposed to be?");
	}
}