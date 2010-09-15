require 'machinist/active_record'
require 'sham'

Sham.define do
  name { Faker::Name.name }
  email { Faker::Internet.email }
  username { Faker::Internet.user_name }
  password { Faker::Name.name }
  guid { Guid.new.to_s }
end

Account.blueprint do
  email
  password
  password_confirmation { password }
end

Mesh.blueprint do
  account
  name { Sham.username }
end

Feed.blueprint do
  mesh
  name { Sham.username }
end

Item.blueprint do
  feed
  item_id { Sham.guid }
end
