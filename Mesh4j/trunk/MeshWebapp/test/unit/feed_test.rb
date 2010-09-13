require 'test_helper'

class FeedTest < ActiveSupport::TestCase
  def setup
    @feed = Feed.make
  end
  
  [:name, :mesh_id].each do |field|
    test "should not save if #{field} is blank" do
      @feed.send("#{field}=", nil)
      assert !@feed.save
    end
  end
  
  test "should not save if name is taken in mesh" do
    feed = Feed.make_unsaved :mesh => @feed.mesh, :name => @feed.name
    assert_false feed.save
  end
  
  test "should have guid" do
    assert_not_nil @feed.guid
  end
  
  test "should not change guid on save" do
    old_guid = @feed.guid
    @feed.name = 'lala'
    @feed.save!
    
    assert_equal old_guid, @feed.guid
  end
end
