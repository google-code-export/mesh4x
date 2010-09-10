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
end
