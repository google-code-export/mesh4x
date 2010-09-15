class AccountsController < AccountAuthenticatedController

  before_filter :authenticate, :only => [:verify]
  
  def verify
    head :ok
  end

end
