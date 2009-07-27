package org.mesh4j.sync.parsers.ognl;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.validations.MeshException;

public class OgnlConditionTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsWhenConditionExpressionIsNull(){
		new OgnlCondition(null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsWhenConditionExpressionIsEmpty(){
		new OgnlCondition("");
	}
	
	@Test(expected=MeshException.class)
	public void shouldCreateFailsWhenConditionExpressionIsInvalidOgnlExpression(){
		new OgnlCondition("1 cmkwe	n1241!@4343654 #!$`");	
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldConvertFailsWhenExpressionIsNull(){
		OgnlCondition.convertToOgnlExpression(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldConvertFailsWhenExpressionIsEmpty(){
		OgnlCondition.convertToOgnlExpression("");
	}
	
	@Test
	public void shouldEvaluateConditionalExpression(){
		OgnlCondition condition = new OgnlCondition("Country=Argentine");
		
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Country", "Argentine");
		Assert.assertTrue(condition.eval(context));
	}
	
	@Test
	public void shouldEvaluateReturnsFalseWhenVariablesIsNotInContext(){
		OgnlCondition condition = new OgnlCondition("Country=Argentine");
		
		HashMap<String, Object> context = new HashMap<String, Object>();
		Assert.assertFalse(condition.eval(context));
	}
		
	@Test
	public void shouldEvaluateReturnsFalseWhenExpressionIsNotACondition(){
		OgnlCondition condition = new OgnlCondition("Country");
		
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Country", "Argentine");
		Assert.assertFalse(condition.eval(context));
	}
	
	// STRING
	@Test
	public void souldAcceptsEqString(){  
		Assert.assertEquals("Country.equals(\"Argentine\")", OgnlCondition.convertToOgnlExpression("Country=Argentine"));
		
		OgnlCondition condition = new OgnlCondition("Country=Argentine");
		
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Country", "Argentine");
		Assert.assertTrue(condition.eval(context));
		
		HashMap<String, Object> context2 = new HashMap<String, Object>();
		context2.put("Country", "Cambodia");
		Assert.assertFalse(condition.eval(context2));
	}

	@Test
	public void souldAcceptsNoEqString(){
		Assert.assertEquals("not(Country.equals(\"Argentine\"))", OgnlCondition.convertToOgnlExpression("Country!=Argentine"));
		
		OgnlCondition condition = new OgnlCondition("Country!=Argentine");
		
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Country", "Argentine");
		Assert.assertFalse(condition.eval(context));
		
		HashMap<String, Object> context2 = new HashMap<String, Object>();
		context2.put("Country", "Cambodia");
		Assert.assertTrue(condition.eval(context2));
	}
	
	@Test
	public void souldAcceptsEqStringWithParenthesis(){  
		Assert.assertEquals("(Country.equals(\"Argentine\"))", OgnlCondition.convertToOgnlExpression("(Country=Argentine)"));
		
		OgnlCondition condition = new OgnlCondition("(Country=Argentine)");
		
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Country", "Argentine");
		Assert.assertTrue(condition.eval(context));
		
		HashMap<String, Object> context2 = new HashMap<String, Object>();
		context2.put("Country", "Cambodia");
		Assert.assertFalse(condition.eval(context2));
	}

	@Test
	public void souldAcceptsNoEqStringWithParenthesis(){
		Assert.assertEquals("(not(Country.equals(\"Argentine\")))", OgnlCondition.convertToOgnlExpression("(Country!=Argentine)"));
		
		OgnlCondition condition = new OgnlCondition("(Country!=Argentine)");
		
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Country", "Argentine");
		Assert.assertFalse(condition.eval(context));
		
		HashMap<String, Object> context2 = new HashMap<String, Object>();
		context2.put("Country", "Cambodia");
		Assert.assertTrue(condition.eval(context2));
	}
	
	@Test
	public  void souldAcceptsGreaterThanWithString(){
		Assert.assertEquals("Name>\"Jose\"", OgnlCondition.convertToOgnlExpression("Name>Jose"));

		OgnlCondition condition = new OgnlCondition("Name>Jose");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Name", "Jefe");
		Assert.assertFalse(condition.eval(context));
		
		context.put("Name", "Jose");
		Assert.assertFalse(condition.eval(context));
		
		context.put("Name", "Juan");
		Assert.assertTrue(condition.eval(context));

	}
	
	@Test
	public  void souldAcceptsLesserThanWithString(){
		Assert.assertEquals("Name<\"Jose\"", OgnlCondition.convertToOgnlExpression("Name<Jose"));

		OgnlCondition condition = new OgnlCondition("Name<Jose");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Name", "Jefe");
		Assert.assertTrue(condition.eval(context));
		
		context.put("Name", "Jose");
		Assert.assertFalse(condition.eval(context));
		
		context.put("Name", "Juan");
		Assert.assertFalse(condition.eval(context));

	}
	
	@Test
	public  void souldAcceptsGreaterOrEqualThanWithString(){
		Assert.assertEquals("Name>=\"Jose\"", OgnlCondition.convertToOgnlExpression("Name>=Jose"));

		OgnlCondition condition = new OgnlCondition("Name>=Jose");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Name", "Jefe");
		Assert.assertFalse(condition.eval(context));
		
		context.put("Name", "Jose");
		Assert.assertTrue(condition.eval(context));
		
		context.put("Name", "Juan");
		Assert.assertTrue(condition.eval(context));

	}
	
	@Test
	public  void souldAcceptsLesserOrEqualsThanWithString(){
		Assert.assertEquals("Name<=\"Jose\"", OgnlCondition.convertToOgnlExpression("Name<=Jose"));

		OgnlCondition condition = new OgnlCondition("Name<=Jose");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Name", "Jefe");
		Assert.assertTrue(condition.eval(context));
		
		context.put("Name", "Jose");
		Assert.assertTrue(condition.eval(context));
		
		context.put("Name", "Juan");
		Assert.assertFalse(condition.eval(context));

	}
	
	// TIMESTAMP
	@Test
	public void souldAcceptsEqTimestamp(){  
		Assert.assertEquals("DateOnSet.equals(\"2009-01-01T12:01:01Z\")", OgnlCondition.convertToOgnlExpression("DateOnSet=2009-01-01T12:01:01Z"));
		
		OgnlCondition condition = new OgnlCondition("DateOnSet=2009-01-01T12:01:01Z");
		
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("DateOnSet", "2009-01-01T12:01:01Z");
		Assert.assertTrue(condition.eval(context));
		
		HashMap<String, Object> context2 = new HashMap<String, Object>();
		context2.put("DateOnSet", "2007-03-01T12:01:01Z");
		Assert.assertFalse(condition.eval(context2));
	}

	@Test
	public void souldAcceptsNoEqTimestamp(){
		Assert.assertEquals("not(DateOnSet.equals(\"2009-01-01T12:01:01Z\"))", OgnlCondition.convertToOgnlExpression("DateOnSet!=2009-01-01T12:01:01Z"));

		OgnlCondition condition = new OgnlCondition("DateOnSet!=2009-01-01T12:01:01Z");

		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("DateOnSet", "2009-01-01T12:01:01Z");
		Assert.assertFalse(condition.eval(context));
		
		HashMap<String, Object> context2 = new HashMap<String, Object>();
		context2.put("DateOnSet", "2007-03-01T12:01:01Z");
		Assert.assertTrue(condition.eval(context2));
	
	}
	
	@Test
	public void souldAcceptsEqTimestampWithParenthesis(){  
		Assert.assertEquals("(DateOnSet.equals(\"2009-01-01T12:01:01Z\"))", OgnlCondition.convertToOgnlExpression("(DateOnSet=2009-01-01T12:01:01Z)"));
		
		OgnlCondition condition = new OgnlCondition("(DateOnSet=2009-01-01T12:01:01Z)");
		
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("DateOnSet", "2009-01-01T12:01:01Z");
		Assert.assertTrue(condition.eval(context));
		
		HashMap<String, Object> context2 = new HashMap<String, Object>();
		context2.put("DateOnSet", "2007-03-01T12:01:01Z");
		Assert.assertFalse(condition.eval(context2));
	}

	@Test
	public void souldAcceptsNoEqTimestampWithParenthesis(){
		Assert.assertEquals("(not(DateOnSet.equals(\"2009-01-01T12:01:01Z\")))", OgnlCondition.convertToOgnlExpression("(DateOnSet!=2009-01-01T12:01:01Z)"));

		OgnlCondition condition = new OgnlCondition("(DateOnSet!=2009-01-01T12:01:01Z)");

		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("DateOnSet", "2009-01-01T12:01:01Z");
		Assert.assertFalse(condition.eval(context));
		
		HashMap<String, Object> context2 = new HashMap<String, Object>();
		context2.put("DateOnSet", "2007-03-01T12:01:01Z");
		Assert.assertTrue(condition.eval(context2));
	
	}
	
	@Test
	public  void souldAcceptsGreaterThanWithTimestamp(){
		Assert.assertEquals("DateOnSet>\"2009-02-12T12:01:01Z\"", OgnlCondition.convertToOgnlExpression("DateOnSet>2009-02-12T12:01:01Z"));

		OgnlCondition condition = new OgnlCondition("DateOnSet>2009-02-12T12:01:01Z");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("DateOnSet", "2008-02-12T12:01:01Z");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2008-12-12T12:01:01Z");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2009-01-12T12:01:01Z");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-03T12:01:01Z");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-11T12:01:01Z");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-12T12:01:01Z");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-13T12:01:01Z");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-22T12:01:01Z");
		Assert.assertTrue(condition.eval(context));
	
		context.put("DateOnSet", "2009-03-12T12:01:01Z");
		Assert.assertTrue(condition.eval(context));

		context.put("DateOnSet", "2009-11-22T12:01:01Z");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2019-01-12T12:01:01Z");
		Assert.assertTrue(condition.eval(context));

	}
	
	@Test
	public  void souldAcceptsLesserThanWithTimestamp(){
		Assert.assertEquals("DateOnSet<\"2009-02-12T12:01:01Z\"", OgnlCondition.convertToOgnlExpression("DateOnSet<2009-02-12T12:01:01Z"));

		OgnlCondition condition = new OgnlCondition("DateOnSet<2009-02-12T12:01:01Z");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("DateOnSet", "2008-02-12T12:01:01Z");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2008-12-12T12:01:01Z");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2009-01-12T12:01:01Z");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-03T12:01:01Z");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-11T12:01:01Z");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-12T12:01:01Z");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-13T12:01:01Z");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-22T12:01:01Z");
		Assert.assertFalse(condition.eval(context));
	
		context.put("DateOnSet", "2009-03-12T12:01:01Z");
		Assert.assertFalse(condition.eval(context));

		context.put("DateOnSet", "2009-11-22T12:01:01Z");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2019-01-12T12:01:01Z");
		Assert.assertFalse(condition.eval(context));

	}
	
	@Test
	public  void souldAcceptsGreaterOrEqualThanWithTimestamp(){
		Assert.assertEquals("DateOnSet>=\"2009-02-12T12:01:01Z\"", OgnlCondition.convertToOgnlExpression("DateOnSet>=2009-02-12T12:01:01Z"));

		OgnlCondition condition = new OgnlCondition("DateOnSet>=2009-02-12T12:01:01Z");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("DateOnSet", "2008-02-12T12:01:01Z");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2008-12-12T12:01:01Z");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2009-01-12T12:01:01Z");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-03T12:01:01Z");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-11T12:01:01Z");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-12T12:01:01Z");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-13T12:01:01Z");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-22T12:01:01Z");
		Assert.assertTrue(condition.eval(context));
	
		context.put("DateOnSet", "2009-03-12T12:01:01Z");
		Assert.assertTrue(condition.eval(context));

		context.put("DateOnSet", "2009-11-22T12:01:01Z");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2019-01-12T12:01:01Z");
		Assert.assertTrue(condition.eval(context));

	}
	
	@Test
	public  void souldAcceptsLesserOrEqualsThanWithTimestamp(){
		Assert.assertEquals("DateOnSet<=\"2009-02-12T12:01:01Z\"", OgnlCondition.convertToOgnlExpression("DateOnSet<=2009-02-12T12:01:01Z"));

		OgnlCondition condition = new OgnlCondition("DateOnSet<=2009-02-12T12:01:01Z");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("DateOnSet", "2008-02-12T12:01:01Z");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2008-12-12T12:01:01Z");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2009-01-12T12:01:01Z");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-03T12:01:01Z");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-11T12:01:01Z");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-12T12:01:01Z");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-13T12:01:01Z");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-22T12:01:01Z");
		Assert.assertFalse(condition.eval(context));
	
		context.put("DateOnSet", "2009-03-12T12:01:01Z");
		Assert.assertFalse(condition.eval(context));

		context.put("DateOnSet", "2009-11-22T12:01:01Z");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2019-01-12T12:01:01Z");
		Assert.assertFalse(condition.eval(context));
	}

	// DATE
	@Test
	public void souldAcceptsEqDate(){  
		Assert.assertEquals("DateOnSet.equals(\"2009-01-01\")", OgnlCondition.convertToOgnlExpression("DateOnSet=2009-01-01"));
		
		OgnlCondition condition = new OgnlCondition("DateOnSet=2009-01-01");

		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("DateOnSet", "2009-01-01");
		Assert.assertTrue(condition.eval(context));
		
		HashMap<String, Object> context2 = new HashMap<String, Object>();
		context2.put("DateOnSet", "2006-01-01");
		Assert.assertFalse(condition.eval(context2));
	}

	@Test
	public void souldAcceptsNoEqDate(){
		Assert.assertEquals("not(DateOnSet.equals(\"2009-01-01\"))", OgnlCondition.convertToOgnlExpression("DateOnSet!=2009-01-01"));

		OgnlCondition condition = new OgnlCondition("DateOnSet!=2009-01-01");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("DateOnSet", "2009-01-01");
		Assert.assertFalse(condition.eval(context));
		
		HashMap<String, Object> context2 = new HashMap<String, Object>();
		context2.put("DateOnSet", "2007-03-01");
		Assert.assertTrue(condition.eval(context2));
	}
	
	@Test
	public void souldAcceptsEqDateWithParenthesis(){  
		Assert.assertEquals("(DateOnSet.equals(\"2009-01-01\"))", OgnlCondition.convertToOgnlExpression("(DateOnSet=2009-01-01)"));
		
		OgnlCondition condition = new OgnlCondition("(DateOnSet=2009-01-01)");

		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("DateOnSet", "2009-01-01");
		Assert.assertTrue(condition.eval(context));
		
		HashMap<String, Object> context2 = new HashMap<String, Object>();
		context2.put("DateOnSet", "2006-01-01");
		Assert.assertFalse(condition.eval(context2));
	}

	@Test
	public void souldAcceptsNoEqDateWithParenthesis(){
		Assert.assertEquals("(not(DateOnSet.equals(\"2009-01-01\")))", OgnlCondition.convertToOgnlExpression("(DateOnSet!=2009-01-01)"));

		OgnlCondition condition = new OgnlCondition("(DateOnSet!=2009-01-01)");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("DateOnSet", "2009-01-01");
		Assert.assertFalse(condition.eval(context));
		
		HashMap<String, Object> context2 = new HashMap<String, Object>();
		context2.put("DateOnSet", "2007-03-01");
		Assert.assertTrue(condition.eval(context2));
	}
	
	@Test
	public  void souldAcceptsGreaterThanWithDate(){
		Assert.assertEquals("DateOnSet>\"2009-02-12\"", OgnlCondition.convertToOgnlExpression("DateOnSet>2009-02-12"));

		OgnlCondition condition = new OgnlCondition("DateOnSet>2009-02-12");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("DateOnSet", "2008-02-12");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2008-12-12");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2009-01-12");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-03");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-11");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-12");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-13");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-22");
		Assert.assertTrue(condition.eval(context));
	
		context.put("DateOnSet", "2009-03-12");
		Assert.assertTrue(condition.eval(context));

		context.put("DateOnSet", "2009-11-22");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2019-01-12");
		Assert.assertTrue(condition.eval(context));

	}
	
	@Test
	public  void souldAcceptsLesserThanWithDate(){
		Assert.assertEquals("DateOnSet<\"2009-02-12\"", OgnlCondition.convertToOgnlExpression("DateOnSet<2009-02-12"));

		OgnlCondition condition = new OgnlCondition("DateOnSet<2009-02-12");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("DateOnSet", "2008-02-12");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2008-12-12");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2009-01-12");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-03");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-11");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-12");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-13");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-22");
		Assert.assertFalse(condition.eval(context));
	
		context.put("DateOnSet", "2009-03-12");
		Assert.assertFalse(condition.eval(context));

		context.put("DateOnSet", "2009-11-22");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2019-01-12");
		Assert.assertFalse(condition.eval(context));

	}
	
	@Test
	public  void souldAcceptsGreaterOrEqualThanWithDate(){
		Assert.assertEquals("DateOnSet>=\"2009-02-12\"", OgnlCondition.convertToOgnlExpression("DateOnSet>=2009-02-12"));

		OgnlCondition condition = new OgnlCondition("DateOnSet>=2009-02-12");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("DateOnSet", "2008-02-12");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2008-12-12");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2009-01-12");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-03");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-11");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-12");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-13");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-22");
		Assert.assertTrue(condition.eval(context));
	
		context.put("DateOnSet", "2009-03-12");
		Assert.assertTrue(condition.eval(context));

		context.put("DateOnSet", "2009-11-22");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2019-01-12");
		Assert.assertTrue(condition.eval(context));

	}
	
	@Test
	public  void souldAcceptsLesserOrEqualsThanWithDate(){
		Assert.assertEquals("DateOnSet<=\"2009-02-12\"", OgnlCondition.convertToOgnlExpression("DateOnSet<=2009-02-12"));

		OgnlCondition condition = new OgnlCondition("DateOnSet<=2009-02-12");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("DateOnSet", "2008-02-12");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2008-12-12");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2009-01-12");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-03");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-11");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-12");
		Assert.assertTrue(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-13");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2009-02-22");
		Assert.assertFalse(condition.eval(context));
	
		context.put("DateOnSet", "2009-03-12");
		Assert.assertFalse(condition.eval(context));

		context.put("DateOnSet", "2009-11-22");
		Assert.assertFalse(condition.eval(context));
		
		context.put("DateOnSet", "2019-01-12");
		Assert.assertFalse(condition.eval(context));
	}

	// INTEGER/LONG
	@Test
	public void souldAcceptsEqInteger(){  
		Assert.assertEquals("Age.equals(\"10\")", OgnlCondition.convertToOgnlExpression("Age=10"));

		OgnlCondition condition = new OgnlCondition("Age=10");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Age", "10");
		Assert.assertTrue(condition.eval(context));
		
		HashMap<String, Object> context2 = new HashMap<String, Object>();
		context2.put("Age", "20");
		Assert.assertFalse(condition.eval(context2));
	}

	@Test
	public void souldAcceptsNoEqInteger(){
		Assert.assertEquals("not(Age.equals(\"10\"))", OgnlCondition.convertToOgnlExpression("Age!=10"));

		OgnlCondition condition = new OgnlCondition("Age!=10");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Age", "20");
		Assert.assertTrue(condition.eval(context));
		
		HashMap<String, Object> context2 = new HashMap<String, Object>();
		context2.put("Age", "10");
		Assert.assertFalse(condition.eval(context2));
	}
	
	@Test
	public void souldAcceptsEqIntegerWithParenthesis(){  
		Assert.assertEquals("(Age.equals(\"10\"))", OgnlCondition.convertToOgnlExpression("(Age=10)"));

		OgnlCondition condition = new OgnlCondition("Age=10");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Age", "10");
		Assert.assertTrue(condition.eval(context));
		
		HashMap<String, Object> context2 = new HashMap<String, Object>();
		context2.put("Age", "20");
		Assert.assertFalse(condition.eval(context2));
	}

	@Test
	public void souldAcceptsNoEqIntegerWithParenthesis(){
		Assert.assertEquals("(not(Age.equals(\"10\")))", OgnlCondition.convertToOgnlExpression("(Age!=10)"));

		OgnlCondition condition = new OgnlCondition("(Age!=10)");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Age", "20");
		Assert.assertTrue(condition.eval(context));
		
		HashMap<String, Object> context2 = new HashMap<String, Object>();
		context2.put("Age", "10");
		Assert.assertFalse(condition.eval(context2));
	}
	
	@Test
	public  void souldAcceptsGreaterThanWithInteger(){
		Assert.assertEquals("Age>111", OgnlCondition.convertToOgnlExpression("Age>111"));

		OgnlCondition condition = new OgnlCondition("Age>111");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Age", "10");
		Assert.assertFalse(condition.eval(context));
		
		context.put("Age", "111");
		Assert.assertFalse(condition.eval(context));
		
		context.put("Age", "121");
		Assert.assertTrue(condition.eval(context));

	}
	
	@Test
	public  void souldAcceptsLesserThanWithInteger(){
		Assert.assertEquals("Age<111", OgnlCondition.convertToOgnlExpression("Age<111"));

		OgnlCondition condition = new OgnlCondition("Age<111");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Age", "10");
		Assert.assertTrue(condition.eval(context));
		
		context.put("Age", "111");
		Assert.assertFalse(condition.eval(context));
		
		context.put("Age", "121");
		Assert.assertFalse(condition.eval(context));

	}
	
	@Test
	public  void souldAcceptsGreaterOrEqualThanWithInteger(){
		Assert.assertEquals("Age>=111", OgnlCondition.convertToOgnlExpression("Age>=111"));

		OgnlCondition condition = new OgnlCondition("Age>=111");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Age", "10");
		Assert.assertFalse(condition.eval(context));
		
		context.put("Age", "111");
		Assert.assertTrue(condition.eval(context));
		
		context.put("Age", "121");
		Assert.assertTrue(condition.eval(context));

	}
	
	@Test
	public  void souldAcceptsLesserOrEqualsThanWithInteger(){
		Assert.assertEquals("Age<=111", OgnlCondition.convertToOgnlExpression("Age<=111"));

		OgnlCondition condition = new OgnlCondition("Age<=111");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Age", "10");
		Assert.assertTrue(condition.eval(context));
		
		context.put("Age", "111");
		Assert.assertTrue(condition.eval(context));
		
		context.put("Age", "121");
		Assert.assertFalse(condition.eval(context));

	}
	
	// FLOAT/DOUBLE/BigDecimal
	@Test
	public void souldAcceptsEqDouble(){  
		Assert.assertEquals("Age.equals(\"10.5\")", OgnlCondition.convertToOgnlExpression("Age=10.5"));

		OgnlCondition condition = new OgnlCondition("Age=10.5");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Age", "10.5");
		Assert.assertTrue(condition.eval(context));
		
		context.put("Age", "8.58");
		Assert.assertFalse(condition.eval(context));

		context.put("Age", "12.58");
		Assert.assertFalse(condition.eval(context));
	}

	@Test
	public void souldAcceptsNoEqDouble(){
		Assert.assertEquals("not(Age.equals(\"10.567\"))", OgnlCondition.convertToOgnlExpression("Age!=10.567"));

		OgnlCondition condition = new OgnlCondition("Age!=10.567");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Age", "20.6");
		Assert.assertTrue(condition.eval(context));
		
		context.put("Age", "10.567");
		Assert.assertFalse(condition.eval(context));

		context.put("Age", "8.58");
		Assert.assertTrue(condition.eval(context));
	}
	
	@Test
	public void souldAcceptsEqDoubleWithParenthesis(){  
		Assert.assertEquals("(Age.equals(\"10.5\"))", OgnlCondition.convertToOgnlExpression("(Age=10.5)"));

		OgnlCondition condition = new OgnlCondition("(Age=10.5)");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Age", "10.5");
		Assert.assertTrue(condition.eval(context));
		
		context.put("Age", "20.73428");
		Assert.assertFalse(condition.eval(context));

		context.put("Age", "5.73428");
		Assert.assertFalse(condition.eval(context));
	}

	@Test
	public void souldAcceptsNoEqDoubleWithParenthesis(){
		Assert.assertEquals("(not(Age.equals(\"10.5\")))", OgnlCondition.convertToOgnlExpression("(Age!=10.5)"));

		OgnlCondition condition = new OgnlCondition("(Age!=10.5)");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Age", "20.543");
		Assert.assertTrue(condition.eval(context));
		
		context.put("Age", "10.5");
		Assert.assertFalse(condition.eval(context));

		context.put("Age", "3.5");
		Assert.assertTrue(condition.eval(context));
	}
	
	@Test
	public  void souldAcceptsGreaterThanWithDouble(){
		Assert.assertEquals("Age>111", OgnlCondition.convertToOgnlExpression("Age>111"));

		OgnlCondition condition = new OgnlCondition("Age>111");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Age", "10");
		Assert.assertFalse(condition.eval(context));
		
		context.put("Age", "111");
		Assert.assertFalse(condition.eval(context));
		
		context.put("Age", "121");
		Assert.assertTrue(condition.eval(context));

	}
	
	@Test
	public  void souldAcceptsLesserThanWithDouble(){
		Assert.assertEquals("Age<111", OgnlCondition.convertToOgnlExpression("Age<111"));

		OgnlCondition condition = new OgnlCondition("Age<111");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Age", "10");
		Assert.assertTrue(condition.eval(context));
		
		context.put("Age", "111");
		Assert.assertFalse(condition.eval(context));
		
		context.put("Age", "121");
		Assert.assertFalse(condition.eval(context));

	}
	
	@Test
	public  void souldAcceptsGreaterOrEqualThanWithDouble(){
		Assert.assertEquals("Age>=111", OgnlCondition.convertToOgnlExpression("Age>=111"));

		OgnlCondition condition = new OgnlCondition("Age>=111");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Age", "10");
		Assert.assertFalse(condition.eval(context));
		
		context.put("Age", "111");
		Assert.assertTrue(condition.eval(context));
		
		context.put("Age", "121");
		Assert.assertTrue(condition.eval(context));

	}
	
	@Test
	public  void souldAcceptsLesserOrEqualsThanWithDouble(){
		Assert.assertEquals("Age<=111", OgnlCondition.convertToOgnlExpression("Age<=111"));

		OgnlCondition condition = new OgnlCondition("Age<=111");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Age", "10");
		Assert.assertTrue(condition.eval(context));
		
		context.put("Age", "111");
		Assert.assertTrue(condition.eval(context));
		
		context.put("Age", "121");
		Assert.assertFalse(condition.eval(context));

	}
	
	// BOOLEAN
	@Test
	public void souldAcceptsEqBool(){  
		Assert.assertEquals("Ill", OgnlCondition.convertToOgnlExpression("Ill"));

		OgnlCondition condition = new OgnlCondition("Ill");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Ill", true);  // TODO (JMT)
		Assert.assertTrue(condition.eval(context));
		
		HashMap<String, Object> context2 = new HashMap<String, Object>();
		context2.put("Ill", false);
		Assert.assertFalse(condition.eval(context2));
	}

	@Test
	public void souldAcceptsNoEqBool(){
		Assert.assertEquals("not Ill", OgnlCondition.convertToOgnlExpression("not Ill"));

		OgnlCondition condition = new OgnlCondition("not Ill");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Ill", true);
		Assert.assertFalse(condition.eval(context));
		
		HashMap<String, Object> context2 = new HashMap<String, Object>();
		context2.put("Ill", false);
		Assert.assertTrue(condition.eval(context2));
	}
	
	
	@Test
	public void souldAcceptsEqBoolAsString(){  
		Assert.assertEquals("Ill.equals(\"true\")", OgnlCondition.convertToOgnlExpression("Ill=true"));

		OgnlCondition condition = new OgnlCondition("Ill=true");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Ill", "true");  // TODO (JMT)
		Assert.assertTrue(condition.eval(context));
		
		HashMap<String, Object> context2 = new HashMap<String, Object>();
		context2.put("Ill", "false");
		Assert.assertFalse(condition.eval(context2));
	}

	@Test
	public void souldAcceptsNoEqBoolAsString(){
		Assert.assertEquals("not(Ill.equals(\"true\"))", OgnlCondition.convertToOgnlExpression("Ill!=true"));

		OgnlCondition condition = new OgnlCondition("Ill!=true");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Ill", "true");
		Assert.assertFalse(condition.eval(context));
		
		HashMap<String, Object> context2 = new HashMap<String, Object>();
		context2.put("Ill", "false");
		Assert.assertTrue(condition.eval(context2));
	}
	
	@Test
	public void souldAcceptsEqBoolAsStringWithParenthesis(){  
		Assert.assertEquals("(Ill.equals(\"true\"))", OgnlCondition.convertToOgnlExpression("(Ill=true)"));

		OgnlCondition condition = new OgnlCondition("(Ill=true)");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Ill", "true"); 
		Assert.assertTrue(condition.eval(context));
		
		HashMap<String, Object> context2 = new HashMap<String, Object>();
		context2.put("Ill", "false");
		Assert.assertFalse(condition.eval(context2));
	}

	@Test
	public void souldAcceptsNoEqBoolAsStringWithParenthesis(){
		Assert.assertEquals("(not(Ill.equals(\"true\")))", OgnlCondition.convertToOgnlExpression("(Ill!=true)"));

		OgnlCondition condition = new OgnlCondition("(Ill!=true)");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Ill", "true");
		Assert.assertFalse(condition.eval(context));
		
		HashMap<String, Object> context2 = new HashMap<String, Object>();
		context2.put("Ill", "false");
		Assert.assertTrue(condition.eval(context2));
	}
	// AND/OR/NOT
	
	@Test
	public void souldAcceptsAnd(){  
		Assert.assertEquals("Ill and Age.equals(\"10\")", OgnlCondition.convertToOgnlExpression("Ill and Age=10"));

		OgnlCondition condition = new OgnlCondition("Ill and Age=10");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Ill", true);
		context.put("Age", "10");
		Assert.assertTrue(condition.eval(context));
		
		HashMap<String, Object> context2 = new HashMap<String, Object>();
		context2.put("Ill", false);
		context2.put("Age", "10");
		Assert.assertFalse(condition.eval(context2));

		HashMap<String, Object> context3 = new HashMap<String, Object>();
		context3.put("Ill", false);
		context3.put("Age", "11");
		Assert.assertFalse(condition.eval(context3));
		
		HashMap<String, Object> context4 = new HashMap<String, Object>();
		context4.put("Ill", true);
		context4.put("Age", "11");
		Assert.assertFalse(condition.eval(context4));
	}
	
	@Test
	public void souldAcceptsOr(){  
		Assert.assertEquals("Ill or Age.equals(\"10\")", OgnlCondition.convertToOgnlExpression("Ill or Age=10"));

		OgnlCondition condition = new OgnlCondition("Ill or Age=10");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Ill", true);
		context.put("Age", "10");
		Assert.assertTrue(condition.eval(context));
		
		HashMap<String, Object> context2 = new HashMap<String, Object>();
		context2.put("Ill", false);
		context2.put("Age", "10");
		Assert.assertTrue(condition.eval(context2));

		HashMap<String, Object> context3 = new HashMap<String, Object>();
		context3.put("Ill", false);
		context3.put("Age", "11");
		Assert.assertFalse(condition.eval(context3));
		
		HashMap<String, Object> context4 = new HashMap<String, Object>();
		context4.put("Ill", true);
		context4.put("Age", "11");
		Assert.assertTrue(condition.eval(context4));
	}
	
	@Test
	public void souldAcceptsNotAnd(){  
		Assert.assertEquals("not(Ill and Age.equals(\"10\"))", OgnlCondition.convertToOgnlExpression("not(Ill and Age=10)"));

		OgnlCondition condition = new OgnlCondition("not(Ill and Age=10)");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Ill", true);
		context.put("Age", "10");
		Assert.assertFalse(condition.eval(context));
		
		HashMap<String, Object> context2 = new HashMap<String, Object>();
		context2.put("Ill", false);
		context2.put("Age", "10");
		Assert.assertTrue(condition.eval(context2));

		HashMap<String, Object> context3 = new HashMap<String, Object>();
		context3.put("Ill", false);
		context3.put("Age", "11");
		Assert.assertTrue(condition.eval(context3));
		
		HashMap<String, Object> context4 = new HashMap<String, Object>();
		context4.put("Ill", true);
		context4.put("Age", "11");
		Assert.assertTrue(condition.eval(context4));
	}
	
	@Test
	public void souldAcceptsNotOr(){  
		Assert.assertEquals("not(Ill or Age.equals(\"10\"))", OgnlCondition.convertToOgnlExpression("not(Ill or Age=10)"));

		OgnlCondition condition = new OgnlCondition("not(Ill or Age=10)");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Ill", true);
		context.put("Age", "10");
		Assert.assertFalse(condition.eval(context));
		
		HashMap<String, Object> context2 = new HashMap<String, Object>();
		context2.put("Ill", false);
		context2.put("Age", "10");
		Assert.assertFalse(condition.eval(context2));

		HashMap<String, Object> context3 = new HashMap<String, Object>();
		context3.put("Ill", false);
		context3.put("Age", "11");
		Assert.assertTrue(condition.eval(context3));
		
		HashMap<String, Object> context4 = new HashMap<String, Object>();
		context4.put("Ill", true);
		context4.put("Age", "11");
		Assert.assertFalse(condition.eval(context4));
	}
	
	@Test
	public  void souldAcceptsSubExpressionsWithAndOrNotWithParenthesis(){  
		Assert.assertEquals("not(Ill) and (Age.equals(\"11\") or Age.equals(\"10\"))", OgnlCondition.convertToOgnlExpression("not(Ill) and (Age=11 or Age=10)"));

		OgnlCondition condition = new OgnlCondition("not(Ill) and (Age=11 or Age=10)");
	
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("Ill", true);
		context.put("Age", "10");
		Assert.assertFalse(condition.eval(context));
		
		HashMap<String, Object> context1 = new HashMap<String, Object>();
		context1.put("Ill", true);
		context1.put("Age", "8");
		Assert.assertFalse(condition.eval(context));
		
		HashMap<String, Object> context2 = new HashMap<String, Object>();
		context2.put("Ill", false);
		context2.put("Age", "10");
		Assert.assertTrue(condition.eval(context2));

		HashMap<String, Object> context3 = new HashMap<String, Object>();
		context3.put("Ill", false);
		context3.put("Age", "11");
		Assert.assertTrue(condition.eval(context3));
		
		HashMap<String, Object> context4 = new HashMap<String, Object>();
		context4.put("Ill", false);
		context4.put("Age", "8");
		Assert.assertFalse(condition.eval(context4));
	}
	
	// INVALIDATE OGNL EXPRESSIONS
		
	@Test(expected=IllegalArgumentException.class)
	public  void souldNotAcceptsOgnlClassMembership(){
		//e instanceof class
		OgnlCondition.convertToOgnlExpression("A instanceof java.lang.String");
	}

	@Test(expected=IllegalArgumentException.class)
	public  void souldNotAcceptsOgnlMethodCall(){
		//e.method(args)
		OgnlCondition.convertToOgnlExpression("A.myMethod(B, 5, \"abc\", true)");
	}

	@Test(expected=IllegalArgumentException.class)
	public  void souldNotAcceptsOgnlObjectPropertyAccess(){
		//e.property
		OgnlCondition.convertToOgnlExpression("A.myProperty");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public  void souldNotAcceptsOgnlIndexAccess(){
		//e1[ e2 ]
		OgnlCondition.convertToOgnlExpression("A[ 1 ]");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public  void souldNotAcceptsOgnlSelection(){
		//e1.{? e2 }
		OgnlCondition.convertToOgnlExpression("A.{? B}");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public  void souldNotAcceptsOgnlProjection(){
		//e1.{ e2 }
		OgnlCondition.convertToOgnlExpression("A.{B}");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public  void souldNotAcceptsOgnlSubexpressionEvaluation(){
		//e1.(e2)
		OgnlCondition.convertToOgnlExpression("A.(B)");
	}
	
	//@Test(expected=IllegalArgumentException.class)
	@Test
	public  void souldNotAcceptsOgnlExpressionEvaluation(){
		//e1(e2)
		//OgnlCondition.convertToOgnlExpression("A(B)");    // TODO (JMT) problems with not(...)
		Assert.fail("pending tasks, problem with not(...)");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public  void souldNotAcceptsOgnlContextVariableReference(){
		//#var
		OgnlCondition.convertToOgnlExpression("#A");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public  void souldNotAcceptsOgnlStaticMethodReference(){
		//@class@method(args)
		OgnlCondition.convertToOgnlExpression("@org.mesh4j.sync.parsers.ognl.OgnlCondition@MyMethod(B)");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public  void souldNotAcceptsOgnlStaticFieldReference(){
		//@class@field
		OgnlCondition.convertToOgnlExpression("@org.mesh4j.sync.parsers.ognl.OgnlCondition@MyProperty");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public  void souldNotAcceptsOgnlArrayCreation(){
		//new array-component-class[] { e, ... }
		OgnlCondition.convertToOgnlExpression("new java.lanf.String[] {\"A\", \"B\"}");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public  void souldNotAcceptsOgnlMapCreation(){
		//#{ e1 : e2, ... }
		OgnlCondition.convertToOgnlExpression("#{\"A\" : 5, \"B\" : 6}");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public  void souldNotAcceptsOgnlMapCreationWithSpecificSubclass(){
		//#@classname@{ e1 : e2, ...}
		OgnlCondition.convertToOgnlExpression("@java.util.HashMap{\"A\" : 5, \"B\" : 6}");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public  void souldNotAcceptsOgnlLambdaExpressionDefinition(){
		//:[ e ]
		OgnlCondition.convertToOgnlExpression(":[B]");
	}
	
	
}
