class MeshesController < AccountAuthenticatedController
  before_filter :authenticate, :only => [:create, :show]
  before_filter :check_login, :only => [:delete]
  
  def create
    Mesh.create! :account_id => @account.id, :name => params[:name]
    head :ok
  end
  
  def show
    mesh = Mesh.find_by_account_id_and_name @account.id, params[:name]
    return head :not_found unless mesh
    
    head :ok
  end
  
  def delete
    mesh = Mesh.find_by_account_id_and_name @account.id, params[:name]
    return redirect_to_home unless mesh
    
    mesh.destroy
    
    redirect_to_home "The mesh '#{mesh.name}' was deleted"
  end
end
