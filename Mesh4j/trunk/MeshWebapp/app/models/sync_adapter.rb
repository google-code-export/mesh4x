class SyncAdapter
  def self.new(*args)
    Rjb::bind(super(*args), 'org.mesh4j.sync.ISyncAdapter')
  end
  
  def initialize(feed)
    @feed = feed
  end
  
  def getAll
    items = Rjb::import('java.util.ArrayList').new
    @feed.items.each do |item|
      items.add item.to_mesh4j
    end
    items
  end
  
  def get(id)
    item = Item.find_by_feed_id_and_item_id @feed.id, id
    return nil unless item
    
    item.to_mesh4j
  end
  
  def add(item)
    item_sync = item.sync
    history = item_sync.getUpdatesHistory().toArray().map{|h| {:when => h.getWhen.getTime, :by => h.by, :sequence => h.sequence}}
    
    sync = {
      :id => item_sync.getId,
      :deleted => item_sync.isDeleted,
      :no_conflicts => item_sync.isNoConflicts,
      :history => history,
      :conflicts => []
    } 
  
    Item.create!({
      :feed => @feed,
      :item_id => item.getContent.getId,
      :content => item.content.payload.asXML,
      :sync => sync
    })
    
    nil
  end
  
  def method_missing(name, *args)
    puts "SyncAdapter doesn't implement #{name}(#{args.join(',')})"
    super
  end
end
