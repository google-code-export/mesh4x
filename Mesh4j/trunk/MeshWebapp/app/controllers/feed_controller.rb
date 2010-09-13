class FeedController < ApplicationController

  IdentityProvider = Mesh4j::NullIdentityProvider.INSTANCE
  SyndicationFormat = Mesh4j::RssSyndicationFormat.INSTANCE

  def index
    guid = params[:guid]
    feed = Feed.find_by_guid guid
    
    items = Rjb::import('java.util.ArrayList').new
    feed.items.each do |item|
      items.add item.to_mesh4j
    end
    
    write_feed feed, items, 'conflicts'
  end

  def sync
    guid = params[:guid]
    feed = Feed.find_by_guid guid
  
    format = Mesh4j::RssSyndicationFormat.INSTANCE
    identity_provider = Mesh4j::NullIdentityProvider.INSTANCE
    reader = Mesh4j::FeedReader.new SyndicationFormat, IdentityProvider, Mesh4j::IdGenerator.INSTANCE, Mesh4j::ContentReader.INSTANCE
    feed_loaded = reader.read request.raw_post
    in_memory_adapter = Mesh4j::InMemorySyncAdapter.new guid, identity_provider, feed_loaded.items
    adapter = SyncAdapter.new feed
    
    sync_engine = Mesh4j::SyncEngine.new adapter, in_memory_adapter
    conflicts = sync_engine.synchronize
    
    write_feed feed, conflicts, 'conflicts'
  end
  
  def schema
    head :not_found
  end
  
  private
  
  def write_feed(feed, items, name)
    feed_result =  Mesh4j::Feed.new feed.name, name, request.url
    feed_result.addItems items
    
    feed_writer = Mesh4j::FeedWriter.new SyndicationFormat, IdentityProvider, Mesh4j::ContentWriter.INSTANCE
    feed_xml = feed_writer.writeAsXml feed_result
    
    render :text => feed_xml, :content_type => SyndicationFormat.getContentType
  end

end
