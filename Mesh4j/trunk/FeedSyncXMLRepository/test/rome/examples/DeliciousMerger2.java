package rome.examples;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;
import com.sun.syndication.io.XmlReader;

public class DeliciousMerger2
{
    public static void main(final String[] args)
    {
        final DeliciousMerger2 merger = new DeliciousMerger2();
        final String urlBase = "http://del.icio.us/rss/tag/";
        final SyndFeedOutput output = new SyndFeedOutput();
        final SyndFeed newFeed = new SyndFeedImpl();
        final List entries = new ArrayList();
        final SyndFeedInput input = new SyndFeedInput();
        final Map seenUrls = new HashMap();
        URL feedUrl;
        SyndFeed feed;

        if (args.length < 2)
        {
            throw new IllegalArgumentException("Specify two or more del.icio.us tags to merge");
        }

        final StringBuffer tagList = new StringBuffer(args[0]);
        for (int argidx = 1; argidx < args.length; argidx++)
        {
            tagList.append(", ");
            tagList.append(args[argidx]);
        }
        newFeed.setTitle("Combined del.icio.us Tags: " + tagList);
        newFeed.setDescription("Aggregation of tags: " + tagList);
        newFeed.setFeedType("rss_1.0");
        newFeed.setAuthor("DeliciousMerger2");
        newFeed.setLink("http://del.icio.us");

        for (int idx = 0; idx < args.length; idx++)
        {
            try
            {
                feedUrl = new URL(urlBase + args[idx]);
                feed = input.build(new XmlReader(feedUrl));
                for (final Iterator iter = feed.getEntries().listIterator();
                     iter.hasNext(); )
                {
                    final SyndEntry entry = (SyndEntry)iter.next();
                    if (! seenUrls.containsKey(entry.getLink()))
                    {
                        entries.add(entry);
                        seenUrls.put(entry.getLink(), entry);
                    }
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                System.exit(-1);
            }
        }

        SyndEntry[] entriesArray = new SyndEntry[entries.size()];
        entriesArray = (SyndEntry[])entries.toArray(entriesArray);
        Arrays.sort(entriesArray, merger.new OrderByDate());
        newFeed.setEntries(Arrays.asList(entriesArray));
        try
        {
            output.output(newFeed, new PrintWriter(System.out));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    private class OrderByDate implements Comparator
    {
        public int compare(final Object aObj, final Object bObj)
        {
            final Date aDate = ((SyndEntry)aObj).getPublishedDate();
            final Date bDate = ((SyndEntry)bObj).getPublishedDate();
            return (aDate == null) ? -1 : aDate.compareTo(bDate);
        }
    }
}
