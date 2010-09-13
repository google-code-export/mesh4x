require 'test_helper'

class FeedControllerTest < ActionController::TestCase
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
  
  test "schema" do
    get :schema, :guid => '123'
    assert_response :not_found
  end
end
