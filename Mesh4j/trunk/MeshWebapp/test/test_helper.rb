ENV["RAILS_ENV"] = "test"
require File.expand_path(File.dirname(__FILE__) + "/../config/environment")
require File.expand_path(File.dirname(__FILE__) + "/blueprints")
require 'test_help'
require 'base64'

class ActiveSupport::TestCase
  # Transactional fixtures accelerate your tests by wrapping each test method
  # in a transaction that's rolled back on completion.  This ensures that the
  # test database remains unchanged so your fixtures don't have to be reloaded
  # between every test method.  Fewer database queries means faster tests.
  #
  # Read Mike Clark's excellent walkthrough at
  #   http://clarkware.com/cgi/blosxom/2005/10/24#Rails10FastTesting
  #
  # Every Active Record database supports transactions except MyISAM tables
  # in MySQL.  Turn off transactional fixtures in this case; however, if you
  # don't care one way or the other, switching from MyISAM to InnoDB tables
  # is recommended.
  #
  # The only drawback to using transactional fixtures is when you actually 
  # need to test transactions.  Since your test is bracketed by a transaction,
  # any transactions started in your code will be automatically rolled back.
  self.use_transactional_fixtures = true
  
  include Mocha::API

  setup do
    Sham.reset
  end

  # Instantiated fixtures are slow, but give you @david where otherwise you
  # would need people(:david).  If you don't want to migrate your existing
  # test cases which use the @david style and don't mind the speed hit (each
  # instantiated fixtures translates to a database query per test method),
  # then set this back to true.
  self.use_instantiated_fixtures  = false

  # Setup all fixtures in test/fixtures/*.(yml|csv) for all tests in alphabetical order.
  #
  # Note: You'll currently still have to declare fixtures explicitly in integration tests
  # -- they do not yet inherit this setting
  fixtures :all

  # Add more helper methods to be used by all tests here...
  def http_auth(user, pass)
    @request.env['HTTP_AUTHORIZATION'] = 'Basic ' + Base64.encode64(user + ':' + pass)
  end
  
  def url_for(options)
    url = ActionController::UrlRewriter.new(@request, nil)
    url.rewrite(options)
  end
  
  def compare(e1, e2)
   return false if e1.class != e2.class
   if e1.class <= Hash
     ret = _compare_hashes e1, e2
     return false if not ret
     ret = _compare_hashes e2, e1
     return false if not ret
   elsif e1.class <= Array
     ret = _compare_arrays e1, e2
     return false if not ret
     ret = _compare_arrays e2, e1
     return false if not ret
   else
     return e1 == e2
   end
   true
  end

  def _compare_hashes(h1, h2)
   h1.each do |key, value|
     ret = compare value, h2[key]
     return false if not ret
   end
   true
  end

  def _compare_arrays(a1, a2)
   a1.each do |v1|
     ok = false
     a2.each do |v2|
       if compare v1, v2
         ok = true
         next
       end  
     end
     return false if not ok
   end
   true
  end
  
  def assert_xml_equal xml1, xml2
    xml1 = Hash.from_xml xml1
    xml2 = Hash.from_xml xml2
    assert compare(xml1, xml2)
  end
end
