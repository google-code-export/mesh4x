# Be sure to restart your server when you modify this file.

# Your secret key for verifying cookie session data integrity.
# If you change this key, all old sessions will become invalid!
# Make sure the secret is at least 30 characters and all random, 
# no regular words or you'll be exposed to dictionary attacks.
ActionController::Base.session = {
  :key         => '_MeshWebapp_session',
  :secret      => 'd8cf591e8eec99413aaa12422e955fb58f5b9752ad92874b814e9750429b9b04cbeb04d12ccd58d6f73dd0388c0122ca23685f9f5bbc0282624b4f61d9f6f891'
}

# Use the database for sessions instead of the cookie-based default,
# which shouldn't be used to store highly confidential information
# (create the session table with "rake db:sessions:create")
# ActionController::Base.session_store = :active_record_store
