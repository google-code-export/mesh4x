require 'machinist/active_record'
require 'sham'

Sham.define do
  name { Faker::Name.name }
  username { Faker::Internet.user_name }
  password { Faker::Name.name }
end

Account.blueprint do
  name { Sham.username }
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
