require 'test_helper'

class AccountsControllerTest < ActionController::TestCase
  test "verify" do
    @account = Account.make :password => 'b'
    http_auth @account.email, 'b'
    get :verify
    
    assert_response :ok
  end
  
  test "verify fails" do
    @account = Account.make :password => 'b'
    http_auth @account.email, 'c'
    get :verify
    
    assert_response 401
  end
end
