package org.mesh4j.sync.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
