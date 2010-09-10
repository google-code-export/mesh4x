class Mesh < ActiveRecord::Base
  belongs_to :account
  has_many :feeds
  
  validates_presence_of :account
  validates_presence_of :name
  
  validates_uniqueness_of :name, :scope => :account_id
end
