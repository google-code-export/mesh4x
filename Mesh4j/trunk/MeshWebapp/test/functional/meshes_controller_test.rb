require 'test_helper'

class MeshesControllerTest < ActionController::TestCase
  def setup
    @account = Account.make :password => 'b'
    http_auth @account.email, 'b'
  end

  test "create" do
    post :create, :name => 'foo'
    
    meshes = Mesh.all
    assert_equal 1, meshes.length
    assert_equal @account.id, meshes[0].account_id
    assert_equal 'foo', meshes[0].name  
  end
  
  test "show" do
    mesh = Mesh.make :account => @account
    get :show, :name => mesh.name
    assert_response :ok
  end
  
  test "show not existent" do
    mesh = Mesh.make :account => @account
    get :show, :name => "#{mesh.name}_not_exists"
    assert_response :not_found
  end
end
