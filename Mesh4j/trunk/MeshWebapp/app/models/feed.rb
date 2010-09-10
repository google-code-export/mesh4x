class Feed < ActiveRecord::Base
  belongs_to :mesh
  
  validates_presence_of :mesh
  validates_presence_of :name
  
  validates_uniqueness_of :name, :scope => :mesh_id
end
