class FeedsController < AccountAuthenticatedController

  before_filter :authenticate, :only => [:create]
  before_filter :check_login, :only => [:delete]

  IdentityProvider = Mesh4j::NullIdentityProvider.INSTANCE
  SyndicationFormat = Mesh4j::RssSyndicationFormat.INSTANCE

  def index
    guid = params[:guid]
    last_modified = request.headers['If-Modified-Since']
    
    feed = Feed.find_by_guid guid
    
    if last_modified
      feed_items = Item.all :conditions => ['feed_id = ? AND updated_at > ?', feed.id, DateTime.parse(last_modified)]
    else
      feed_items = feed.items
    end
    
    items = Rjb::import('java.util.ArrayList').new
    feed_items.each do |item|
      items.add item.to_mesh4j
    end
    
    write_feed feed, items, 'conflicts'
  end

  def sync
    guid = params[:guid]
    feed = Feed.find_by_guid guid
  
    reader = Mesh4j::FeedReader.new SyndicationFormat, IdentityProvider, Mesh4j::IdGenerator.INSTANCE, Mesh4j::ContentReader.INSTANCE
    feed_loaded = reader.read request.raw_post
    in_memory_adapter = Mesh4j::InMemorySyncAdapter.new guid, IdentityProvider, feed_loaded.items
    adapter = SyncAdapter.new feed
    
    sync_engine = Mesh4j::SyncEngine.new adapter, in_memory_adapter
    conflicts = sync_engine.synchronize nil, Mesh4j::SyncDirection.TargetToSource
    
    write_feed feed, conflicts, 'conflicts'
  end
  
  def schema
    head :not_found
  end
  
  def create
    mesh = Mesh.find_by_account_id_and_name @account.id, params[:mesh_name]
    return head :not_found unless mesh
    
    feed = Feed.create! :mesh_id => mesh.id, :name => params[:feed_name]
    render :text => sync_get_url(:guid => feed.guid)
  end
  
  def delete
    mesh = Mesh.find_by_account_id_and_name @account.id, params[:mesh_name]
    return redirect_to_home unless mesh
    
    feed = Feed.find_by_mesh_id_and_name mesh.id, params[:feed_name]
    return redirect_to_home unless feed
    
    feed.destroy
    
    redirect_to_home "Feed '#{feed.name}' of mesh '#{mesh.name}' was deleted"
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
