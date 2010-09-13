ActionController::Routing::Routes.draw do |map|
  # The priority is based upon order of creation: first created -> highest priority.
  map.root :controller => :home, :action => :index
  
  map.sync_get  '/feed/:guid', :conditions => {:method => :get},  :controller => :feed, :action => :index
  map.sync_post '/feed/:guid', :conditions => {:method => :post}, :controller => :feed, :action => :sync
  map.schema '/feed/:guid/schema', :controller => :feed, :action => :schema

  # Install the default routes as the lowest priority.
  # Note: These default routes make all actions in every controller accessible via GET requests. You should
  # consider removing or commenting them out if you're using named routes and resources.
  map.connect ':controller/:action/:id'
  map.connect ':controller/:action/:id.:format'
end
