package org.mesh4j.sync.servlet;

import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;

public enum Format {
	kml, rss20, atom10, plain_xml, xform, plain_xform;

	public static ISyndicationFormat getSyndicationFormat(Format format) {
		if(format == null){
			return RssSyndicationFormat.INSTANCE;
		}else if(format.isRSS()){
			return RssSyndicationFormat.INSTANCE;
		}else if(format.isAtom()){
			return AtomSyndicationFormat.INSTANCE;
		} else {
			return null;
		}
	}
	
	public boolean isKML(){
		return kml.equals(this);
	}
	
	public boolean isPlainXML(){
		return plain_xml.equals(this) || plain_xform.equals(this);
	}
	
	public boolean isXForm(){
		return xform.equals(this) || plain_xform.equals(this);
	}

	public boolean isRSS(){
		return rss20.equals(this);
	}
	
	public boolean isAtom(){
		return atom10.equals(this);
	}
	
	public boolean isSyndicationFormat(){
		return this.isRSS() || this.isAtom();
	}

	public static Format getFormat(String name) {
		if(name == null){
			return null;
		} else {
			try{
				return Format.valueOf(name);
			} catch(IllegalArgumentException e){
				// invalid format name
				return null;
			}
		}
	}
}
