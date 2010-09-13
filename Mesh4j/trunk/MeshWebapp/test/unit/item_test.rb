require 'test_helper'

class ItemTest < ActiveSupport::TestCase
  def setup
    @item = Item.make
  end
  
  [:item_id, :feed_id].each do |field|
    test "should not save if #{field} is blank" do
      @item.send("#{field}=", nil)
      assert !@item.save
    end
  end
end
