class MeshesController < AccountAuthenticatedController
  before_filter :authenticate
  
  def create
    Mesh.create! :account_id => @account.id, :name => params[:name]
    head :ok
  end
  
  def show
    mesh = Mesh.find_by_account_id_and_name @account.id, params[:name]
    return head :not_found unless mesh
    
    head :ok
  end
end
