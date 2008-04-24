package com.sun.syndication.feed.synd.impl.extension;

import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.synd.SyndEntry;

public class ConverterForAtom10 extends
		com.sun.syndication.feed.synd.impl.ConverterForAtom10 {

	   public ConverterForAtom10() {
	        this("atom_1.0");
	    }

	    protected ConverterForAtom10(String type) {
	        super(type);
	    }
	    
		public SyndEntry createSyndEntry(Entry entry) {
			return super.createSyndEntry(null, entry);
		}
		
		public Entry createAtomEntry(SyndEntry sEntry){
			return super.createAtomEntry(sEntry);
		}
	
}
