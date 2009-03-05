package org.mesh4j.sync.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;

public class DiffTests {

	
	@Test
	public void shouldEmptyBlocksWhenTextIsEmpty(){
		List<String> blocks = DiffUtils.split("", 2);
		Assert.assertEquals(0, blocks.size());
	}
	
	@Test
	public void shouldEmptyBlocksWhenTextIsNull(){
		List<String> blocks = DiffUtils.split(null, 2);
		Assert.assertEquals(0, blocks.size());
	}
	
	@Test
	public void shouldCreateBlocksWhenSizeBlockIsEqualsToTextLenght(){
		List<String> blocks = DiffUtils.split("ab", 2);
		Assert.assertEquals(1, blocks.size());
		Assert.assertEquals("ab", blocks.get(0));
	}
	
	@Test
	public void shouldCreateBlocksWhenSizeBlockIsGreaterThanTextLenght(){
		List<String> blocks = DiffUtils.split("ab", 3);
		Assert.assertEquals(1, blocks.size());
		Assert.assertEquals("ab", blocks.get(0));
	}
	
	@Test
	public void shouldCreateBlocksWhenSizeBlockIsLessThanTextLenght(){
		List<String> blocks = DiffUtils.split("abcdefgh", 3);
		Assert.assertEquals(3, blocks.size());
		Assert.assertEquals("abc", blocks.get(0));
		Assert.assertEquals("def", blocks.get(1));
		Assert.assertEquals("gh", blocks.get(2));
		
		blocks = DiffUtils.split("abcdefghi", 3);
		Assert.assertEquals(3, blocks.size());
		Assert.assertEquals("abc", blocks.get(0));
		Assert.assertEquals("def", blocks.get(1));
		Assert.assertEquals("ghi", blocks.get(2));
	}

	@Test
	public void shouldEmptyHashsWhenTextIsNull(){
		int[] hashs = DiffUtils.calculateBlockHashCodes(null, 2);
		Assert.assertEquals(0, hashs.length);
	}
	
	@Test
	public void shouldEmptyHashsWhenTextIsEmpty(){
		int[] hashs = DiffUtils.calculateBlockHashCodes("", 2);
		Assert.assertEquals(0, hashs.length);
	}	

	@Test
	public void shouldGenerateHashsWhenTextHasOneBlock(){
		int[] hashs = DiffUtils.calculateBlockHashCodes("a", 2);
		Assert.assertEquals(1, hashs.length);
		Assert.assertEquals("a".hashCode(), hashs[0]);
	}
	
	@Test
	public void shouldGenerateHashsWhenTextHasTwoBlocks(){
		int[] hashs = DiffUtils.calculateBlockHashCodes("abc", 2);
		Assert.assertEquals(2, hashs.length);
		Assert.assertEquals("ab".hashCode(), hashs[0]);
		Assert.assertEquals("c".hashCode(), hashs[1]);
	}
	
	@Test
	public void shouldObtainsFirstDiffs(){
		
		int[] hashs = DiffUtils.calculateBlockHashCodes("abc", 2);

		Map<Integer, String> diffs = DiffUtils.obtainsDiff("a2c", 2, hashs);
		Assert.assertEquals(1, diffs.size());
		Assert.assertEquals("a2", diffs.get(0));
	}
	
	@Test
	public void shouldObtainsDiffsWhenTextLenghtIsLessThanHashsLenght(){
		
		int[] hashs = DiffUtils.calculateBlockHashCodes("abcdef", 2);

		Map<Integer, String> diffs = DiffUtils.obtainsDiff("a2c", 2, hashs);
		Assert.assertEquals(3, diffs.size());
		Assert.assertEquals("a2", diffs.get(0));
		Assert.assertEquals("c", diffs.get(1));
		Assert.assertEquals("", diffs.get(2));
	}
	
	@Test
	public void shouldObtainsDiffsWhenTextLenghtIsGreaterThanHashsLenght(){
		
		int[] hashs = DiffUtils.calculateBlockHashCodes("abcdef", 2);

		Map<Integer, String> diffs = DiffUtils.obtainsDiff("a2cdyhgt", 2, hashs);
		Assert.assertEquals(3, diffs.size());
		Assert.assertEquals("a2", diffs.get(0));
		Assert.assertEquals("yh", diffs.get(2));
		Assert.assertEquals("gt", diffs.get(3));
	}
	
	@Test
	public void shouldAppliesDiffsUpdateText(){
				
		Map<Integer, String> diffs = new HashMap<Integer, String>();
		diffs.put(0, "zz");
		
		String result = DiffUtils.appliesDiff("abc", 2, diffs);		
		Assert.assertEquals("zzc", result);
		
		diffs = new HashMap<Integer, String>();
		diffs.put(1, "zz");
		
		result = DiffUtils.appliesDiff("abc", 2, diffs);		
		Assert.assertEquals("abzz", result);
		
		diffs = new HashMap<Integer, String>();
		diffs.put(1, "z");
		
		result = DiffUtils.appliesDiff("abc", 2, diffs);		
		Assert.assertEquals("abz", result);
	}
	
	@Test
	public void shouldAppliesDiffsAddText(){
		
		Map<Integer, String> diffs = new HashMap<Integer, String>();
		diffs.put(1, "zz");
		diffs.put(2, "zz");
		
		String result = DiffUtils.appliesDiff("abc", 2, diffs);
		
		Assert.assertEquals("abzzzz", result);
	}

	@Test
	public void shouldAppliesDiffsDeleteText(){
				
		Map<Integer, String> diffs = new HashMap<Integer, String>();
		diffs.put(1, "");
		
		String result = DiffUtils.appliesDiff("abc", 2, diffs);
		
		Assert.assertEquals("ab", result);
	}
	
	@Test
	public void shouldAppliesDiffs(){
				
		Map<Integer, String> diffs = new HashMap<Integer, String>();
		diffs.put(0, "j");
		diffs.put(1, "");
		
		String result = DiffUtils.appliesDiff("abc", 2, diffs);
		
		Assert.assertEquals("j", result);
	}
	
	@Test
	public void shouldDiff(){
		String oswegoXml1 = 
			"<Oswego>" +
			"      <Code>P10</Code>" +
			"      <Name>Patient10</Name>" +
			"      <AGE>60</AGE>" +
			"      <SEX>Female</SEX>" +
			"      <ILL>1</ILL>" +
			"      <BAKEDHAM>1</BAKEDHAM>" +
			"      <SPINACH>1</SPINACH>" +
			"      <MASHEDPOTA>1</MASHEDPOTA>" +
			"      <CABBAGESAL>0</CABBAGESAL>" +
			"      <JELLO>0</JELLO>" +
			"      <ROLLS>1</ROLLS>" +
			"      <BROWNBREAD>1</BROWNBREAD>" +
			"      <MILK>0</MILK>" +
			"      <COFFEE>0</COFFEE>" +
			"      <WATER>1</WATER>" +
			"      <CAKES>0</CAKES>" +
			"      <VANILLA>1</VANILLA>" +
			"      <CHOCOLATE>1</CHOCOLATE>" +
			"      <FRUITSALAD>0</FRUITSALAD>" +
			"      <TimeSupper>1940-04-18 19:00:00</TimeSupper>" +
			"      <DateOnset>1940-04-18 23:00:00</DateOnset>" +
			"      <RecStatus>1</RecStatus>" +
			"      <Address>20700 State Route 411. La Fargeville, NY, 13656</Address>" +
			"      <County>Jefferson</County>"+
			"</Oswego>";
		
		String oswegoXml = 
			"<Oswego>" +
			"      <Code>P10</Code>" +
			"      <Name>Patient10</Name>" +
			"      <AGE>50</AGE>" +
			"      <SEX>Female</SEX>" +
			"      <ILL>1</ILL>" +
			"      <BAKEDHAM>1</BAKEDHAM>" +
			"      <SPINACH>1</SPINACH>" +
			"      <MASHEDPOTA>1</MASHEDPOTA>" +
			"      <CABBAGESAL>0</CABBAGESAL>" +
			"      <JELLO>0</JELLO>" +
			"      <ROLLS>1</ROLLS>" +
			"      <BROWNBREAD>1</BROWNBREAD>" +
			"      <MILK>0</MILK>" +
			"      <COFFEE>0</COFFEE>" +
			"      <WATER>1</WATER>" +
			"      <CAKES>0</CAKES>" +
			"      <VANILLA>1</VANILLA>" +
			"      <CHOCOLATE>1</CHOCOLATE>" +
			"      <FRUITSALAD>0</FRUITSALAD>" +
			"      <TimeSupper>1940-04-18 19:00:00</TimeSupper>" +
			"      <DateOnset>1940-04-18 23:00:00</DateOnset>" +
			"      <RecStatus>1</RecStatus>" +
			"      <Address>20700 State Route 411. La Fargeville, NY, 13656</Address>" +
			"      <County>Jefferson</County>"+
			"</Oswego>";
		
		Element element = XMLHelper.parseElement(oswegoXml);
		String xml = XMLHelper.canonicalizeXML(element);
		
		Element element1 = XMLHelper.parseElement(oswegoXml1);
		String xml1 = XMLHelper.canonicalizeXML(element1);
		int[] diffHashCodes = DiffUtils.calculateBlockHashCodes(xml1, 100);
		Map<Integer, String> diffs = DiffUtils.obtainsDiff(xml, 100, diffHashCodes);
		System.out.println(diffs);
	}
}
