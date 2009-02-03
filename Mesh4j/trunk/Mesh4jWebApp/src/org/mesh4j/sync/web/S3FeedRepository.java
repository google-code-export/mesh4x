package org.mesh4j.sync.web;

import java.util.List;

import org.dom4j.Element;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.S3.IS3Service;
import org.mesh4j.sync.adapters.S3.S3Adapter;
import org.mesh4j.sync.adapters.S3.amazon.S3Service;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.validations.Guard;

public class S3FeedRepository extends AbstractFeedRepository {

	private static final String FEED_PREFIX = "_feed_";
	private static final String MESH_PREFIX = "mesh_";
	private static final String MESH_OBJECT = "mesh";
	
	// MODEL VARIABLES
	private IS3Service s3;
	private String bucket;   // TODO (JMT) bucket pool?
	
	// BUSINESS METHODS

	public S3FeedRepository(String bucket, String accessKey, String secretAccessKey) {
		Guard.argumentNotNullOrEmptyString(bucket, "bucket");
		Guard.argumentNotNullOrEmptyString(accessKey, "accessKey");
		Guard.argumentNotNullOrEmptyString(secretAccessKey, "secretAccessKey");
		
		this.bucket = bucket;
		this.s3 = new S3Service(accessKey, secretAccessKey);
		
	}

	@Override
	protected void addNewFeed(String sourceID, Feed feed, ISyndicationFormat syndicationFormat) {
		// nothing to do		
	}

	@Override
	protected ISyncAdapter getParentSyncAdapter(String sourceID) {
		String parentID = this.getParentS3ID(sourceID);
		return new S3Adapter(this.bucket, parentID, this.s3, NullIdentityProvider.INSTANCE);
	}

	private String getParentS3ID(String sourceID) {
		if(sourceID == null){
			return null;
		} else {
			if(sourceID.indexOf("/") == -1 || sourceID.indexOf("/") == sourceID.length()){
				return MESH_OBJECT;
			} else {
				String[] sourceIds = sourceID.split("/");
				return MESH_PREFIX + sourceIds[0];
			}
		}
	}

	private String getFeedS3ID(String sourceID) {
		if(sourceID == null){
			return MESH_OBJECT;
		} else {
			if(sourceID.indexOf("/") == -1 || sourceID.indexOf("/") == sourceID.length()){
				String normalizedSourceID = sourceID.replaceAll("/", "");
				return MESH_PREFIX + normalizedSourceID;
			} else {
				String[] sourceIds = sourceID.split("/");
				return MESH_PREFIX + sourceIds[0] + FEED_PREFIX + sourceIds[1];
			}
		}
	}
	
	@Override
	protected ISyncAdapter getSyncAdapter(String sourceID) {
		String s3Id = this.getFeedS3ID(sourceID);
		return new S3Adapter(this.bucket, s3Id, this.s3, NullIdentityProvider.INSTANCE);
	}

	@Override
	public boolean existsFeed(String sourceID) {
		if(sourceID == null){
			return false;
		}
		
		String parentID = getParentS3ID(sourceID);
		if(parentID == null){
			return false;
		} else {
			String title = getFeedTitle(sourceID);
			S3Adapter adapter = new S3Adapter(this.bucket, parentID, this.s3, NullIdentityProvider.INSTANCE);
			List<Item> items = adapter.getAll();
			for (Item item : items) {
				Element titleElement = adapter.getSyndicationFormat().getFeedItemTitleElement(item.getContent().getPayload());
				if(titleElement != null && titleElement.getText().equals(title)){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void cleanFeed(String sourceID) {
		// Not supported operation
		// TODO (JMT) supports clean a feed
	}
	
}
