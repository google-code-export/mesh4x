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
  
  test "create" do
    plan = Account.plan
    get :create, plan
    
    accounts = Account.all
    assert_equal 1, accounts.length
    assert_equal plan[:email], accounts[0].email
    assert_true accounts[0].authenticate(plan[:password])
  end
end
