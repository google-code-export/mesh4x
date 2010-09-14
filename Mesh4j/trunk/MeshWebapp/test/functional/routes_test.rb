require 'test_helper'

class RoutesTest < ActionController::TestCase
  test "feeds get" do
    assert_routing({:path => "/feeds/12345", :method => :get }, { :controller => "feeds", :action => "index", :guid => "12345" })
  end
  
  test "feeds sync" do
    assert_routing({:path => "/feeds/12345", :method => :post }, { :controller => "feeds", :action => "sync", :guid => "12345" })
  end
  
  test "feeds schema" do
    assert_routing({:path => "/feeds/12345/schema", :method => :post }, { :controller => "feeds", :action => "schema", :guid => "12345" })
  end
  
  test "create mesh" do
    assert_routing({:path => "/meshes/foo", :method => :post }, { :controller => "meshes", :action => "create", :name => "foo" })
  end
end
