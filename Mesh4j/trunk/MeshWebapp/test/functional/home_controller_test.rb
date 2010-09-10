require 'test_helper'

class HomeControllerTest < ActionController::TestCase
  test "login succeeds" do
    account = Account.make :password => 'account_pass'
    
    get :login, :account => {:name => account.name, :password => 'account_pass'}
    
    # Go to account home page
    assert_redirected_to(:controller => 'home', :action => 'index')
    
    # Account id was saved in session
    assert_equal account.id, session[:account_id]
  end
  
  test "create account succeeds" do
    attrs = Account.plan :password => 'account_pass'
  
    get :create_account, :new_account => attrs
    
    # Go to account home page
    assert_redirected_to(:controller => 'home', :action => 'index')
    
    # The account was created
    accounts = Account.all
    assert_equal 1, accounts.length
    
    account = accounts[0]
    assert_equal attrs[:name], accounts[0].name
    assert accounts[0].authenticate(attrs[:password]) 
    
    # Account was saved in session
    assert_equal account.id, session[:account_id]
  end

  test "home" do
    account = Account.make
    get :index, {}, {:account_id => account.id}
    assert_template 'home/home.html.erb'
  end
  
  # ------------------------ #
  # Validations tests follow #
  # ------------------------ #
  
  test "login fails wrong name" do
    account = Account.make
    get :login, :account => {:name => 'wrong_account', :password => 'account_pass'}
    assert_template 'index'
  end
  
  test "login fails wrong pass" do
    account = Account.make
    get :login, :account => {:name => account.name, :password => 'wrong_pass'}
    assert_template 'index'
  end
  
  test "create account fails name is empty" do
    account = Account.make
    get :create_account, :new_account => {:name => '   ', :password=> 'foo'}
    assert_template 'index'
  end
  
end
