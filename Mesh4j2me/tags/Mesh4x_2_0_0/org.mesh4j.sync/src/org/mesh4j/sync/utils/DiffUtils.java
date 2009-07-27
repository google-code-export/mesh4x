package org.mesh4j.sync.utils;

import java.util.Iterator;
import java.util.Vector;

import de.enough.polish.util.HashMap;
import de.enough.polish.util.Map;

public class DiffUtils {

	public static Vector<String> split(String text, int blockSize){
		
		if(text == null || text.length() == 0){
			return new Vector<String>();
		}
		
		if(blockSize >= text.length()){
			Vector<String> blocks = new Vector<String>();
			blocks.addElement(text);
			return blocks;
		} else {
			String block = text.substring(0, blockSize);
	
			String substring = text.substring(blockSize, text.length());
			Vector<String> subBlocks = split(substring, blockSize);
			
			Vector<String> blocks = new Vector<String>();
			blocks.addElement(block);
			
			for (int i = 0; i < subBlocks.size(); i++) {
				blocks.addElement(subBlocks.elementAt(i));	
			}
			
			return blocks;
		}
	}
	
	public static int[] calculateBlockHashCodes(String text, int blockSize){
		Vector<String> blocks = split(text, blockSize);
		int[] hashCodes = new int[blocks.size()];
		
		int i = 0;
		for (String block : blocks) {
			hashCodes[i] = block.hashCode();
			i++;
		}		
		return hashCodes;
	}
	
	public static Map obtainsDiff(String text, int blockSize, int[] hashCodes){
		Map diffs = new HashMap();
		
		Vector<String> blocks = split(text, blockSize);
		int blocksLenght = blocks.size();
		int hashLenght = hashCodes.length;
		
		int i = 0;
		for (String block : blocks) {
			int blockHashCode = block.hashCode();
			if(i < hashLenght){
				if(blockHashCode != hashCodes[i]){
					diffs.put(new Integer(i), block);
				}
			} else {
				diffs.put(new Integer(i), block);
			}
			i++;
		}
		
		if(hashLenght > blocksLenght){			
			for (int j = blocksLenght; j < hashLenght; j++) {
				diffs.put(new Integer(j), "");
			}
		}		
		return diffs;
	}

	public static String appliesDiff(String text, int blockSize, Map diffs){
		StringBuilder sb = new StringBuilder();
		
		Vector<String> blocks = split(text, blockSize);
		int blocksLenght = blocks.size();
		
		for (int i = 0; i < blocksLenght; i++) {
			String diff = (String)diffs.get(new Integer(i));
			if(diff == null){
				sb.append(blocks.elementAt(i));
			} else {
				sb.append(diff);
			}
		}
		
		Integer i = null;
		for (Iterator iterator = (Iterator)diffs.keysIterator(); iterator.hasNext();) {
			i = (Integer) iterator.next();
			if(i >= blocksLenght){
				String diff = (String)diffs.get(new Integer(i));
				sb.append(diff);
			}
		}
		return sb.toString();
	}
	
   public static String[] split(String original, String delimiter) {
        Vector<String> nodes = new Vector<String>();
        // Parse nodes into vector
        int index = original.indexOf(delimiter);
        while(index>=0) {
            nodes.addElement( original.substring(0, index) );
            original = original.substring(index+delimiter.length());
            index = original.indexOf(delimiter);
        }
        // Get the last node
        nodes.addElement( original );
        
        // Create splitted string array
        String[] result = new String[ nodes.size() ];
        if( nodes.size()>0 ) {
            for(int loop=0; loop<nodes.size(); loop++) {
                result[loop] = (String)nodes.elementAt(loop);
            }
            
        }
        return result;
    }	
}
