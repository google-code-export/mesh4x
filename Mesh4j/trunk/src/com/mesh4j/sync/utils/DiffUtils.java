package com.mesh4j.sync.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiffUtils {

	public static List<String> split(String text, int blockSize){
		
		if(text == null || text.length() == 0){
			return new ArrayList<String>();
		}
		
		if(blockSize >= text.length()){
			return Arrays.asList(text);
		} else {
			String block = text.substring(0, blockSize);
	
			String substring = text.substring(blockSize, text.length());
			List<String> subBlocks = split(substring, blockSize);
			
			ArrayList<String> blocks = new ArrayList<String>();
			blocks.add(block);
			blocks.addAll(subBlocks);
			return blocks;
		}
	}
	
	public static int[] calculateBlockHashCodes(String text, int blockSize){
		List<String> blocks = split(text, blockSize);
		int[] hashCodes = new int[blocks.size()];
		
		int i = 0;
		for (String block : blocks) {
			hashCodes[i] = block.hashCode();
			i++;
		}		
		return hashCodes;
	}
	
	public static Map<Integer, String> obtainsDiff(String text, int blockSize, int[] hashCodes){
		Map<Integer, String> diffs = new HashMap<Integer, String>();
		
		List<String> blocks = split(text, blockSize);
		int blocksLenght = blocks.size();
		int hashLenght = hashCodes.length;
		
		int i = 0;
		for (String block : blocks) {
			int blockHashCode = block.hashCode();
			if(i < hashLenght){
				if(blockHashCode != hashCodes[i]){
					diffs.put(i, block);
				}
			} else {
				diffs.put(i, block);
			}
			i++;
		}
		
		if(hashLenght > blocksLenght){			
			for (int j = blocksLenght; j < hashLenght; j++) {
				diffs.put(j, "");
			}
		}		
		return diffs;
	}

	public static String appliesDiff(String text, int blockSize, Map<Integer, String> diffs){
		StringBuilder sb = new StringBuilder();
		
		List<String> blocks = split(text, blockSize);
		int blocksLenght = blocks.size();
		
		for (int i = 0; i < blocksLenght; i++) {
			String diff = diffs.get(i);
			if(diff == null){
				sb.append(blocks.get(i));
			} else {
				sb.append(diff);
			}
		}
		
		for (Integer i : diffs.keySet()) {
			if(i >= blocksLenght){
				String diff = diffs.get(i);
				sb.append(diff);
			}
		}
		return sb.toString();
	}
}
