package wyvern.tools.tests.utils;

public class TestCase {
	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

	public String getExpectedValue() {
		return expectedValue;
	}

	public String getExpectedType() {
		return expectedType;
	}

	private final String name;
	private final String code;
	private final String expectedValue;
	private final String expectedType;

	public TestCase(String name, String code, String expectedValue, String expectedType) {

		this.name = name;
		this.code = code;
		this.expectedValue = expectedValue;
		this.expectedType = expectedType;
	}
}
