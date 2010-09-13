class Item < ActiveRecord::Base
  belongs_to :feed
  
  validates_presence_of :feed
  validates_presence_of :item_id
  
  serialize :sync
  
  def to_mesh4j
    payload = Mesh4j::XMLHelper.parseElement self.content
    content = Mesh4j::XMLContent.new self.item_id, self.item_id, self.item_id, payload
    sync = Mesh4j::Sync.new self.sync[:id]
    sync.setDeleted self.sync[:deleted]
    self.sync[:history].each do |history|
      mesh_history = Mesh4j::History.new history[:by], Mesh4j::RjbHelper.newDate(history[:when].to_s), history[:sequence]
      sync.getUpdatesHistory.add mesh_history
    end
    sync.setUpdatesWithLastUpdateSequence
    
    Mesh4j::Item.new content, sync
  
  rescue Exception => e
    p e
  end
end
