package org.mesh4j.sync.web;

import java.io.File;
import java.util.Properties;

import org.mesh4j.sync.servlet.FeedServlet;

public class FeedRepositoryFactory {

	// CONSTANTS
	private static final String FEEDS_REPOSITORY_CLASS_NAME = "feeds.repository";

	private static final String FEEDS_BASE_DIRECTORY = "feeds.base.directory";

	private static final String FEEDS_S3_BUCKET = "feeds.s3.bucket";
	private static final String FEEDS_S3_SECRET_ACCESS_KEY = "feeds.s3.secret.access.key";
	private static final String FEEDS_S3_ACCESS_KEY = "feeds.s3.access.key";

	// BUSINESS METHODS
	public static IFeedRepository createFeedRepository(FeedServlet feedServlet, Properties prop) throws Exception {
		String repositoryClassName = prop
				.getProperty(FEEDS_REPOSITORY_CLASS_NAME);
		if (FileFeedRepository.class.getName().equals(repositoryClassName)) {
			return new FileFeedRepository(getRootPath(feedServlet, prop));
		}

		if (S3FeedRepository.class.getName().equals(repositoryClassName)) {
			String bucket = prop.getProperty(FEEDS_S3_BUCKET);
			String accessKey = prop.getProperty(FEEDS_S3_ACCESS_KEY);
			String secretAccessKey = prop
					.getProperty(FEEDS_S3_SECRET_ACCESS_KEY);
			return new S3FeedRepository(bucket, accessKey, secretAccessKey);
		}

		return null;
	}

	private static String getRootPath(FeedServlet feedServlet, Properties prop) {
		String feedsPath = prop.getProperty(FEEDS_BASE_DIRECTORY);
		if (feedsPath == null || feedsPath.length() == 0) {
			feedsPath = feedServlet.getServletContext().getRealPath("/feeds");
		}
		return feedsPath + File.separator;
	}
}
