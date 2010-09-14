class MeshesController < AccountAuthenticatedController
  before_filter :authenticate
  
  def create
    Mesh.create! :account_id => @account.id, :name => params[:name]
    head :ok
  end
end
