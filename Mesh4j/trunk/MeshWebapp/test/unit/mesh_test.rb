require 'test_helper'

class MeshTest < ActiveSupport::TestCase
  def setup
    @mesh = Mesh.make
  end
  
  [:name, :account_id].each do |field|
    test "should not save if #{field} is blank" do
      @mesh.send("#{field}=", nil)
      assert !@mesh.save
    end
  end
  
  test "should not save if name is taken in account" do
    mesh = Mesh.make_unsaved :account => @mesh.account, :name => @mesh.name
    assert_false mesh.save
  end
end
