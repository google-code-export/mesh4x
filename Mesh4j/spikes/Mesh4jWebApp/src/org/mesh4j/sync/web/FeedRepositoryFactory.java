package org.mesh4j.sync.web;

import java.io.File;

import org.mesh4j.sync.servlet.FeedServlet;

public class FeedRepositoryFactory {

	// CONSTANTS
	private static final String FEEDS_REPOSITORY_CLASS_NAME = "feeds.repository";

	private static final String FEEDS_BASE_DIRECTORY = "feeds.base.directory";
	
	private static final String FEEDS_S3_SECRET_ACCESS_KEY = "feeds.s3.secret.access.key";
	private static final String FEEDS_S3_ACCESS_KEY = "feeds.s3.access.key";

	// BUSINESS METHODS
	public static IFeedRepository createFeedRepository(FeedServlet feedServlet) {
		 String repositoryClassName = feedServlet.getInitParameter(FEEDS_REPOSITORY_CLASS_NAME);
		 if(FileFeedRepository.class.getName().equals(repositoryClassName)){
			 return new FileFeedRepository(getRootPath(feedServlet));
		 }
		 
		 if(S3FeedRepository.class.getName().equals(repositoryClassName)){
			 String accessKey = feedServlet.getInitParameter(FEEDS_S3_ACCESS_KEY);;
			 String secretAccessKey = feedServlet.getInitParameter(FEEDS_S3_SECRET_ACCESS_KEY);;
			 return new S3FeedRepository(accessKey, secretAccessKey);
		 } 
		 
		 return null;
	}
	
	private static String getRootPath(FeedServlet feedServlet) {
		String feedsPath = feedServlet.getInitParameter(FEEDS_BASE_DIRECTORY);
		if(feedsPath == null || feedsPath.length() == 0){
			feedsPath = feedServlet.getServletContext().getRealPath("/feeds");
		}
		return feedsPath + File.separator;
	}
}
