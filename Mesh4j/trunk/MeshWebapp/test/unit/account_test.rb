require 'test_helper'

class AccountTest < ActiveSupport::TestCase
  def setup
    @account = Account.make
  end

  [:email, :password].each do |field|
    test "should not save if #{field} is blank" do
      @account.send("#{field}=", nil)
      assert !@account.save
    end
  end
  
  test "should not save if password confirmation fails" do
    account = Account.make_unsaved :password => 'foo', :password_confirmation => 'foo2'
    assert_false account.save
  end
  
  test "should not save if email is taken" do
    account = Account.make_unsaved :email => @account.email
    assert_false account.save
  end
end

