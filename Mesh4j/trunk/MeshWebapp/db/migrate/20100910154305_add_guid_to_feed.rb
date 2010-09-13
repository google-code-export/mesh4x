class AddGuidToFeed < ActiveRecord::Migration
  def self.up
    add_column :feeds, :guid, :string
  end

  def self.down
    remove_column :feeds, :guid
  end
end
