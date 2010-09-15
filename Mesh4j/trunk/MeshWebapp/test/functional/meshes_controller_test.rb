require 'test_helper'

class MeshesControllerTest < ActionController::TestCase
  test "create" do
    @account = Account.make :password => 'b'
    http_auth @account.email, 'b'
    post :create, :name => 'foo'
    
    meshes = Mesh.all
    assert_equal 1, meshes.length
    assert_equal @account.id, meshes[0].account_id
    assert_equal 'foo', meshes[0].name  
  end
end
