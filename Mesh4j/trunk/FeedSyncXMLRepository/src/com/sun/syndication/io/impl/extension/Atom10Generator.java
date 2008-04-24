package com.sun.syndication.io.impl.extension;

import org.jdom.Element;

import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.io.FeedException;

public class Atom10Generator extends com.sun.syndication.io.impl.Atom10Generator {
 
    public Atom10Generator() {
        this("atom_1.0","1.0");
    }

    protected Atom10Generator(String type, String version) {
        super(type, version);
    }

     public void populateEntry(Entry entry, Element eEntry) throws FeedException {
        super. populateEntry(entry, eEntry);
    }

}
