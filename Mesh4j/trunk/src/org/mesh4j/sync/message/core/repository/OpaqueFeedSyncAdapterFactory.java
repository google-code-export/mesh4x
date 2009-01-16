package org.mesh4j.sync.message.core.repository;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.mesh4j.sync.adapters.feed.FeedSyncAdapterFactory;

public class OpaqueFeedSyncAdapterFactory extends FeedSyncAdapterFactory implements IOpaqueSyncAdapterFactory{

	public OpaqueFeedSyncAdapterFactory(String baseDirectory) {
		super(baseDirectory);
	}
	
	public static String createOriginalSourceIdFromFileName(String feedFileName){
		
		File file = new File(feedFileName);
		String name = file.getName();
		String[] elements = name.substring(0, name.length() - 4).split("_");
		String sourceType = elements[0];
		
		elements[0]="";
		String fileName = StringUtils.join(elements);
		
		//File file = new File(fileName);
		return sourceType + ":" + fileName;
	}

	@Override
	public String createSourceId(String source){
		return createSourceIdFromSource(source);
	}
		
	public static String createSourceIdFromSource(String source){
		StringBuffer sb = new StringBuffer();
		sb.append(SOURCE_TYPE);
		sb.append(":");
		
		sb.append(source.replace(":", "_"));
		
		if(!source.toUpperCase().endsWith(".XML")){
			sb.append(".xml");	
		}
		return sb.toString();
	}
	
}
