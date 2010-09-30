Rjb::load("#{RAILS_ROOT}/lib:" + Dir["#{RAILS_ROOT}/lib/*.jar"].join(':'))

module Mesh4j
  def self.import(string)
    index = string.rindex '.'
    name = index ? string[index + 1 .. -1] : string
    self.class_eval "#{name} = Rjb::import('org.mesh4j.sync.#{string}')"
  end
  
  import 'adapters.feed.Feed'
  import 'adapters.feed.ContentReader'
  import 'adapters.feed.ContentWriter'
  import 'adapters.feed.FeedReader'
  import 'adapters.feed.FeedWriter'
  import 'adapters.feed.rss.RssSyndicationFormat'
  import 'adapters.feed.XMLContent'
  import 'adapters.InMemorySyncAdapter'
  import 'id.generator.IdGenerator'
  import 'model.Item'
  import 'model.History'
  import 'model.Sync'
  import 'payload.schema.rdf.RDFSchema'
  import 'security.NullIdentityProvider'
  import 'SyncDirection'
  import 'SyncEngine'
  import 'utils.XMLHelper'
  
  def InMemorySyncAdapter.new(*args)
    InMemorySyncAdapter.new_with_sig 'Ljava.lang.String;Lorg.mesh4j.sync.security.IIdentityProvider;Ljava.util.List;', *args
  end    
end
