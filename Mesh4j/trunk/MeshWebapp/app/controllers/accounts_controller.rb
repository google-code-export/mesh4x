class AccountsController < AccountAuthenticatedController

  before_filter :authenticate, :only => [:verify]
  
  def verify
    head :ok
  end
  
  def create
    Account.create! :email => params[:email], :password => params[:password]
    head :ok
  end

end
