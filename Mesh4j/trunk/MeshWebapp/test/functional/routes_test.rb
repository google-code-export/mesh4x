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
  
  test "show mesh" do
    assert_routing({:path => "/meshes/foo", :method => :get }, { :controller => "meshes", :action => "show", :name => "foo" })
  end
  
  test "delete mesh" do
    assert_routing({:path => "/meshes/foo/delete", :method => :get }, { :controller => "meshes", :action => "delete", :name => "foo" })
  end
  
  test "create feed" do
    assert_routing({:path => "/meshes/foo/feeds/bar", :method => :post }, { :controller => "feeds", :action => "create", :mesh_name => "foo", :feed_name => "bar" })
  end
  
  test "delete feed" do
    assert_routing({:path => "/meshes/foo/feeds/bar/delete", :method => :get }, { :controller => "feeds", :action => "delete", :mesh_name => "foo", :feed_name => "bar" })
  end
  
  test "verify account" do
    assert_routing({:path => "/accounts/verify", :method => :get }, { :controller => "accounts", :action => "verify" })
  end
end
