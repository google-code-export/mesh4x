class Feed < ActiveRecord::Base
  belongs_to :mesh
  has_many :items
  
  validates_presence_of :mesh
  validates_presence_of :name
  
  validates_uniqueness_of :name, :scope => :mesh_id
  
  before_create :generate_guid
  
  private
  
  def generate_guid
    self.guid ||= Guid.new.to_s
  end
end
