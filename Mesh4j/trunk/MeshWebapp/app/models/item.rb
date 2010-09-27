class Item < ActiveRecord::Base
  belongs_to :feed
  
  validates_presence_of :feed
  validates_presence_of :item_id
  
  serialize :sync
  
  def to_mesh4j
    Item.to_mesh4j(self)
  end
  
  private
  
  def self.to_mesh4j(item)
    sync_id = item[:sync][:id]
    payload = Mesh4j::XMLHelper.parseElement item[:content]
    content = Mesh4j::XMLContent.new sync_id, sync_id, sync_id, payload
    sync = Mesh4j::Sync.new sync_id
    sync.setDeleted item[:sync][:deleted]
    item[:sync][:history].each do |history|
      mesh_history = Mesh4j::History.new history[:by], Rjb::import('java.util.Date').new_with_sig('J', history[:when]), history[:sequence]
      sync.getUpdatesHistory.add mesh_history
    end
    sync.setUpdatesWithLastUpdateSequence
    
    item[:sync][:conflicts].each do |conflict|
      sync.getConflicts.add Item.to_mesh4j(conflict)
    end
    
    Mesh4j::Item.new content, sync
  end
end
