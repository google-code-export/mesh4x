class HomeController < AccountAuthenticatedController

  before_filter :check_login, :except => [:login, :create_account]

  def index
    @meshes = Mesh.all :conditions => ['account_id = ?', @account.id], :order => :name, :include => :feeds
    render :template => 'home/home'
  end
  
  def login
    account = params[:account]
    return redirect_to_home if account.nil?
    
    @account = Account.find_by_email account[:email]
    if @account.nil? || !@account.authenticate(account[:password])
      @account = Account.new :email => account[:email]
      flash[:notice] = 'Invalid email/password'
      return render :index
    end
    
    flash[:notice] = nil
    session[:account_id] = @account.id
    redirect_to_home
  end
  
  def create_account
    account = params[:new_account]
    return redirect_to_home if account.nil?
    
    flash[:notice] = nil
    
    @new_account = Account.new(account)
    if !@new_account.save
      @new_account.clear_password
      return render :index
    end
    
    session[:account_id] = @new_account.id
    redirect_to_home
  end
  
  def logoff
    session[:account_id] = nil
    redirect_to :action => :index
  end

end
