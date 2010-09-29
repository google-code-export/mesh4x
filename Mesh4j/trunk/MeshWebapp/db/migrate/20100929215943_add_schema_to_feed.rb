class AddSchemaToFeed < ActiveRecord::Migration
  def self.up
    add_column :feeds, :schema, :text
  end

  def self.down
    remove_column :feeds, :schema
  end
end
