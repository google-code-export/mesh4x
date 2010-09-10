class CreateMeshes < ActiveRecord::Migration
  def self.up
    create_table :meshes do |t|
      t.integer :account_id
      t.string :name

      t.timestamps
    end
  end

  def self.down
    drop_table :meshes
  end
end
