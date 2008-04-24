package com.sun.syndication.feed.module.feedsync.modules;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jdom.Namespace;

import com.sun.syndication.feed.module.Module;

public abstract class FeedSyncModule implements Module {

	// CONSTANTS
	public static final String SCHEMA_URI = "http://www.microsoft.com/schemas/sse";
    public static final String PREFIX = "sx";
    public static final Namespace NAMESPACE;
    public static final Set<Namespace> NAMESPACES;

    static 
    {
        NAMESPACE = Namespace.getNamespace(PREFIX, SCHEMA_URI);
        Set<Namespace> nss = new HashSet<Namespace>();
        nss.add(NAMESPACE);
        NAMESPACES = Collections.unmodifiableSet(nss);
    }

	// BUSINESS METHODS
	@Override
	public String getUri() {
		return SCHEMA_URI;
	}


	@Override
	public Class getInterface() {
		return getClass();
	}
	
    public Object clone()
    {
        FeedSyncModule clone = null;
        try
        {
            clone = (FeedSyncModule)getClass().newInstance();
            clone.copyFrom(this);
        }
        catch(InstantiationException e)   // TODO Log exception
        {
            e.printStackTrace();
        }
        catch(IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return clone;
    }
   
    // SUBCLASS RESPONSABILITIES
	@Override
    public abstract void copyFrom(Object obj);
	
}
