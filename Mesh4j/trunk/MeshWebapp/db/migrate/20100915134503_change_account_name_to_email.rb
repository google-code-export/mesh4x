class ChangeAccountNameToEmail < ActiveRecord::Migration
  def self.up
    rename_column :accounts, :name, :email
  end

  def self.down
    rename_column :accounts, :email, :name
  end
end
