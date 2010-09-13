class CreateItems < ActiveRecord::Migration
  def self.up
    create_table :items do |t|
      t.integer :feed_id
      t.string :item_id
      t.text :content
      t.text :sync

      t.timestamps
    end
  end

  def self.down
    drop_table :items
  end
end
