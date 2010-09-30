require 'test_helper'

class FeedsControllerTest < ActionController::TestCase
  test "sync" do
    feed = Feed.make
  
    @request.env['RAW_POST_DATA'] = File.read("#{RAILS_ROOT}/test/fixtures/item.rss")
    post :sync, :guid => feed.guid
    
    items = Item.all
    assert_equal 1, items.length
    assert_equal feed.id, items[0].feed_id
    assert_equal '<foo>Get milk, eggs, butter and bread</foo>', items[0].content
    assert_not_nil items[0].sync
  end
  
  test "sync twice" do
    feed = Feed.make
  
    @request.env['RAW_POST_DATA'] = File.read("#{RAILS_ROOT}/test/fixtures/item.rss")
    post :sync, :guid => feed.guid
    post :sync, :guid => feed.guid
    
    items = Item.all
    assert_equal 1, items.length
    assert_equal feed.id, items[0].feed_id
    assert_equal '<foo>Get milk, eggs, butter and bread</foo>', items[0].content
    assert_not_nil items[0].sync
  end
  
  test "save schema" do
    feed = Feed.make
    schema = File.read("#{RAILS_ROOT}/test/fixtures/schema1.xml")
    
    @request.env['RAW_POST_DATA'] = schema
    post :save_schema, :guid => feed.guid
    
    feed = Feed.find_by_guid(feed.guid)
    assert_equal schema, feed.schema
  end
  
  test "save schema returns not found with invalid feed id" do
    post :save_schema, :guid => Guid.new.to_s
    assert_response :not_found
  end
  
  test "merge schema 1" do
    feed = Feed.make
    schema1 = File.read("#{RAILS_ROOT}/test/fixtures/schema1.xml")
    schema2 = File.read("#{RAILS_ROOT}/test/fixtures/schema2.xml")
    
    @request.env['RAW_POST_DATA'] = schema1
    post :save_schema, :guid => feed.guid

    @request.env['RAW_POST_DATA'] = schema2
    post :save_schema, :guid => feed.guid

    feed = Feed.find_by_guid(feed.guid)
    assert_xml_equal schema2, feed.schema
  end

  test "merge schema 2" do
    feed = Feed.make
    schema1 = File.read("#{RAILS_ROOT}/test/fixtures/schema1.xml")
    schema2 = File.read("#{RAILS_ROOT}/test/fixtures/schema2.xml")
    
    @request.env['RAW_POST_DATA'] = schema2
    post :save_schema, :guid => feed.guid

    @request.env['RAW_POST_DATA'] = schema1
    post :save_schema, :guid => feed.guid

    feed = Feed.find_by_guid(feed.guid)
    assert_xml_equal schema2, feed.schema
  end
  
  test "schema" do
    schema = File.read("#{RAILS_ROOT}/test/fixtures/schema1.xml")
    feed = Feed.make :schema => schema
    get :schema, :guid => feed.guid
    assert_equal schema, @response.body
  end
  
  test "schema not found" do
    get :schema, :guid => Guid.new.to_s
    assert_response :not_found
  end
  
  test "create" do
    @account = Account.make :password => 'b'
    http_auth @account.email, 'b'
    
    mesh = Mesh.make :account => @account
    
    post :create, :mesh_name => mesh.name, :feed_name => 'bar'
    
    feeds = Feed.all
    assert_equal 1, feeds.length
    assert_equal mesh.id, feeds[0].mesh_id
    assert_equal 'bar', feeds[0].name
    
    assert_equal url_for(:controller => :feeds, :action => :index, :guid => feeds[0].guid), @response.body
  end
end
