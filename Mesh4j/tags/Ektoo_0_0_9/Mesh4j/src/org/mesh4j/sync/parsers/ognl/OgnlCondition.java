package org.mesh4j.sync.parsers.ognl;

import java.util.Map;

import ognl.Ognl;

import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

/**
 * OgnlConditions represents a condition. User expressions are converted to valid Ongl expression. 
 */
// TODO (JMT) accepts only secure conditions
public class OgnlCondition {

	// MODEL VARIABLES
	private Object conditionRoot;
	
	// BUSINESS METHODs
	
	/**
	 * Create a new OgnlCondition
	 * @param conditionExpression user expression
	 */
	public OgnlCondition(String conditionExpression) {
		Guard.argumentNotNullOrEmptyString(conditionExpression, "conditionExpression");
		try{
			String localConditionExpression = convertToOgnlExpression(conditionExpression);
			this.conditionRoot = Ognl.parseExpression(localConditionExpression);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	@SuppressWarnings("unchecked")
	/**
	 * Evaluate the condition based on a context
	 * @param context a map with a key-value pairs where key is the variable name and the value is a variable value
	 * @return true if the condition is true, otherwise false
	 */
	public boolean eval(Map<String, Object> context) {
		try{
			Map<String, Object> contextOgnl = Ognl.createDefaultContext(null);
			contextOgnl.putAll(context);
				
			Object result = Ognl.getValue(this.conditionRoot, contextOgnl);
			if(result instanceof Boolean){
				return (Boolean) result;
			} else {
				return false;
			}
		} catch (Exception e) {
			// no log exception
			return false;
		}
	}
	
	/**
	 * Convert and normalize an user conditional expression to a Ognl expression.
	 * <P><B>Conversion Rules for String: = != < <= > >= </B><p/>
	 * varA=Brandsen  become varA.equals("Brandsen")<p/>
	 * varA!=Brandsen become not(varA.equals("Brandsen"))<p/>
	 * varA>=Brandsen become varA>="Brandsen"<p/>
	 * varA>Brandsen  become varA>"Brandsen"<p/>
	 * varA<=Brandsen become varA<="Brandsen"<p/>
	 * varA<Brandsen  become varA<"Brandsen"<p/>
	 * 
	 * <P><B>Conversion Rules for Date format as w3c: = != </B><p/>
	 * varA=2009-01-01  become varA.equals("2009-01-01")<p/>
	 * varA!=2009-01-01 become not(varA.equals("2009-01-01"))<p/>
	 * varA>=2009-01-01 become varA>="2009-01-01"<p/>
	 * varA>2009-01-01  become varA>"2009-01-01"<p/>
	 * varA<=2009-01-01 become varA<="2009-01-01"<p/>
	 * varA<2009-01-01  become varA<"2009-01-01"<p/>
	 * 
	 * <P><B>Conversion Rules for Timestamp format as w3c: = != </B><p/>
	 * varA=2009-01-01T10:01:20Z  become varA.equals("2009-01-01T10:01:20Z")<p/>
	 * varA!=2009-01-01T10:01:20Z become not(varA.equals("2009-01-01T10:01:20Z"))<p/>
	 * varA>=2009-01-01T10:01:20Z become varA>="2009-01-01T10:01:20Z"<p/>
	 * varA>2009-01-01T10:01:20Z  become varA>"2009-01-01T10:01:20Z"<p/>
	 * varA<=2009-01-01T10:01:20Z become varA<="2009-01-01T10:01:20Z"<p/>
	 * varA<2009-01-01T10:01:20Z  become varA<"2009-01-01T10:01:20Z"<p/>
	 * 
	 * <P><B>Conversion Rules for Integer/Long: = != </B><p/>
	 * varA=1  become varA.equals("1")<p/>
	 * varA!=500 become not(varA.equals("500"))<p/>
	 * varA>=5 become varA>=5<p/>
	 * varA>5  become varA>5<p/>
	 * varA<=5 become varA<=5<p/>
	 * varA<5  become varA<5<p/>
	 * 
	 * <P><B>Conversion Rules for Float/Double/BigDecimal: = != </B><p/>
	 * varA=20.456  become varA.equals("20.456")<p/>
	 * varA!=30.5 become not(varA.equals("30.5"))<p/>
	 * varA>=5.8 become varA>=5.8<p/>
	 * varA>5.8  become varA>5.8<p/>
	 * varA<=5.8 become varA<=5.8<p/>
	 * varA<5.8  become varA<5.8<p/>
	 * 
	 * @param conditionExpression an user conditional expression
	 * @return a valid ognl expression
	 */
	public static String convertToOgnlExpression(String conditionExpression) {
		Guard.argumentNotNullOrEmptyString(conditionExpression, "conditionExpression");

		String expr = conditionExpression + " ";	
		if(expr.matches(".*\\s+instanceof\\s+.*") ||					// e instanceof class
				expr.matches(".*\\S*\\.\\S+\\(.*\\).*") ||				// e.method(args)
				expr.matches(".*\\.\\D+.*\\s+") ||						// e.myProperty
				expr.matches(".*\\[\\s*.*\\s*\\].*") ||	 				// e1[ e2 ]
				expr.matches(".*\\#[\\w | \\{]+.*")										// #var
				//expr.matches(".*\\S+\\(.*\\).*")						// A(B)
		){ 
			Guard.throwsArgumentException("conditionExpression");
		}			
		
		// Conversion Rules for String: = != < <= > >=
		// varA=jmt  become varA.equals("jmt")
		// varA!=jmt become not(varA.equals("jmt"))
		// varA>=jmt become varA>="jmt"
		// varA>jmt  become varA>"jmt"
		// varA<=jmt become varA<="jmt"
		// varA<jmt  become varA<"jmt"
		expr = expr.replaceAll("([^><!])=((\\w)*)(\\s+|\\))", "$1.equals(\"$2\")$4");
		expr = expr.replaceAll("(\\w*[^><])(!=)((\\w)*)(\\s+|\\))", "not($1.equals(\"$3\"))$5");
		expr = expr.replaceAll("(\\w*[^><])(>=)(\\D(\\w)*)(\\s+|\\))", "$1>=\"$3\"");
		expr = expr.replaceAll("(\\w*[^><])(>)(([^=]\\D)(\\w)*)(\\s+|\\))", "$1>\"$3\"");
		expr = expr.replaceAll("(\\w*[^><])(<=)(\\D(\\w)*)(\\s+|\\))", "$1<=\"$3\"");
		expr = expr.replaceAll("(\\w*[^><])(<)(([^=]\\D)(\\w)*)(\\s+|\\))", "$1<\"$3\"");
		
		// Conversion Rules for Date format as w3c: = !=
		// varA=2009-01-01  become varA.equals("2009-01-01")
		// varA!=2009-01-01 become not(varA.equals("2009-01-01"))
		// varA>=2009-01-01 become varA>="2009-01-01"
		// varA>2009-01-01  become varA>"2009-01-01"
		// varA<=2009-01-01 become varA<="2009-01-01"
		// varA<2009-01-01  become varA<"2009-01-01"
		expr = expr.replaceAll("([^><!])=(\\d\\d\\d\\d\\-\\d\\d\\-\\d\\d)(\\s+|\\))", "$1.equals(\"$2\")$3");
		expr = expr.replaceAll("(\\w*[^><])(!=)(\\d\\d\\d\\d\\-\\d\\d\\-\\d\\d)(\\s+|\\))", "not($1.equals(\"$3\"))$4");
		expr = expr.replaceAll("(\\w*[^><])(>)(\\d\\d\\d\\d\\-\\d\\d\\-\\d\\d)(\\s+|\\))", "$1>\"$3\"$4");
		expr = expr.replaceAll("(\\w*[^><])(>=)(\\d\\d\\d\\d\\-\\d\\d\\-\\d\\d)(\\s+|\\))", "$1>=\"$3\"$4");
		expr = expr.replaceAll("(\\w*[^><])(<)(\\d\\d\\d\\d\\-\\d\\d\\-\\d\\d)(\\s+|\\))", "$1<\"$3\"$4");
		expr = expr.replaceAll("(\\w*[^><])(<=)(\\d\\d\\d\\d\\-\\d\\d\\-\\d\\d)(\\s+|\\))", "$1<=\"$3\"$4");
		
		// Conversion Rules for Timestamp format as w3c: = != 
		// varA=2009-01-01T10:01:20Z  become varA.equals("2009-01-01T10:01:20Z")
		// varA!=2009-01-01T10:01:20Z become not(varA.equals("2009-01-01T10:01:20Z"))
		// varA>=2009-01-01T10:01:20Z become varA>="2009-01-01T10:01:20Z"
		// varA>2009-01-01T10:01:20Z  become varA>"2009-01-01T10:01:20Z"
		// varA<=2009-01-01T10:01:20Z become varA<="2009-01-01T10:01:20Z"
		// varA<2009-01-01T10:01:20Z  become varA<"2009-01-01T10:01:20Z"
		expr = expr.replaceAll("([^><!])=(\\d\\d\\d\\d\\-\\d\\d\\-\\d\\dT\\d\\d\\:\\d\\d\\:\\d\\dZ)(\\s+|\\))", "$1.equals(\"$2\")$3");
		expr = expr.replaceAll("(\\w*[^><])(!=)(\\d\\d\\d\\d\\-\\d\\d\\-\\d\\dT\\d\\d\\:\\d\\d\\:\\d\\dZ)(\\s+|\\))", "not($1.equals(\"$3\"))$4");
		expr = expr.replaceAll("(\\w*[^><])(>)(\\d\\d\\d\\d\\-\\d\\d\\-\\d\\dT\\d\\d\\:\\d\\d\\:\\d\\dZ)(\\s+|\\))", "$1>\"$3\"$4");
		expr = expr.replaceAll("(\\w*[^><])(>=)(\\d\\d\\d\\d\\-\\d\\d\\-\\d\\dT\\d\\d\\:\\d\\d\\:\\d\\dZ)(\\s+|\\))", "$1>=\"$3\"$4");
		expr = expr.replaceAll("(\\w*[^><])(<)(\\d\\d\\d\\d\\-\\d\\d\\-\\d\\dT\\d\\d\\:\\d\\d\\:\\d\\dZ)(\\s+|\\))", "$1<\"$3\"$4");
		expr = expr.replaceAll("(\\w*[^><])(<=)(\\d\\d\\d\\d\\-\\d\\d\\-\\d\\dT\\d\\d\\:\\d\\d\\:\\d\\dZ)(\\s+|\\))", "$1<=\"$3\"$4");
		
		// Conversion Rules for Integer/Long: = !=
		// varA=1  become varA.equals("1")
		// varA!=500 become not(varA.equals("500"))
		expr = expr.replaceAll("([^><!])=(\\d*)(\\s+|\\))", "$1.equals(\"$2\")$3");
		expr = expr.replaceAll("(\\w*[^><])(!=)(\\d*)(\\s+|\\))", "not($1.equals(\"$3\"))$4");

		// Conversion Rules for Float/Double/BigDecimal: = !=
		// varA=20.456  become varA.equals("20.456")
		// varA!=30.5 become not(varA.equals("30.5"))
		expr = expr.replaceAll("([^><!])=((\\d+)*\\.(\\d+)*)(\\s+|\\))", "$1.equals(\"$3.$4\")$5");
		expr = expr.replaceAll("(\\w*[^><])(!=)((\\d+)*\\.(\\d+)*)(\\s+|\\))", "not($1.equals(\"$4.$5\"))$6");
		return expr.trim();
	}
}
