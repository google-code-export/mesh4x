require 'test_helper'

class RoutesTest < ActionController::TestCase
  test "sync" do
    assert_routing({:path => "/feed/12345", :method => :get }, { :controller => "feed", :action => "index", :guid => "12345" })
    assert_routing({:path => "/feed/12345", :method => :post }, { :controller => "feed", :action => "sync", :guid => "12345" })
    assert_routing({:path => "/feed/12345/schema", :method => :post }, { :controller => "feed", :action => "schema", :guid => "12345" })
  end
end
