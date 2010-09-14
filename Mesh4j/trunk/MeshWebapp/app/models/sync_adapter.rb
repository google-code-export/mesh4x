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
    Item.create!({
      :feed => @feed,
      :item_id => item.getContent.getId,
      :content => item.content.payload.asXML,
      :sync => sync_for(item)
    })
    
    nil
  end
  
  def update(item)
    item_id = item.getContent.getId
    
    my_item = Item.find_by_item_id item_id
    return unless my_item
    
    my_item.content = item.content.payload.asXML
    my_item.sync = sync_for(item)
    my_item.save!
    
    nil
  end
  
  def method_missing(name, *args)
    puts "\033[31m" + "SyncAdapter doesn't implement #{name}(#{args.join(',')})" + "\033[0m"
    super
  end
  
  private
  
  def sync_for(item)
    item_sync = item.sync
    
    history = item_sync.getUpdatesHistory().toArray().map do |h| {
        :when => h.getWhen.getTime, 
        :by => h.by, 
        :sequence => h.sequence
      }
    end
    
    conflicts = item_sync.getConflicts().toArray().map do |i| {
        :content => i.content.payload.asXML,
        :sync => sync_for(i)
      }
    end
    
    sync = {
      :id => item_sync.getId,
      :deleted => item_sync.isDeleted,
      :no_conflicts => item_sync.isNoConflicts,
      :history => history,
      :conflicts => conflicts
    }
  end
end
