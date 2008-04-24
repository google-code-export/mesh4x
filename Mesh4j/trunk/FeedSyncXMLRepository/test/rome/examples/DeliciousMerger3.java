package rome.examples;
import java.io.FileReader;
import java.io.IOException;
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

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;
import com.sun.syndication.io.XmlReader;

public class DeliciousMerger3
{
    public static void main(final String[] args)
    {
        final String usage = "Specify your del.icio.us bookmark file, and one or more del.icio.us tags to merge";
        final DeliciousMerger3 merger = new DeliciousMerger3();
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
            throw new IllegalArgumentException(usage);
        }

        try
        {
            readDelBookmarkFile(args[0], seenUrls);
        }
        catch (IOException ex)
        {
            System.err.println("Error opening " + args[0] + " for reading:");
            ex.printStackTrace();
            System.exit(-1);
        }
        catch (JDOMException ex)
        {
            System.err.println("Error parsing content of " + args[0] + ":");
            ex.printStackTrace();
            System.exit(-1);
        }

        final StringBuffer tagList = new StringBuffer(args[1]);
        for (int argidx = 2; argidx < args.length; argidx++)
        {
            tagList.append(", ");
            tagList.append(args[argidx]);
        }
        newFeed.setTitle("Combined del.icio.us Tags: " + tagList);
        newFeed.setDescription("Aggregation of tags: " + tagList);
        newFeed.setFeedType("rss_1.0");
        newFeed.setAuthor("DeliciousMerger3");
        newFeed.setLink("http://del.icio.us");

        for (int idx = 1; idx < args.length; idx++)
        {
            try
            {
                feedUrl = new URL(urlBase + args[idx]);
                feed = input.build(new XmlReader(feedUrl));
                for (final Iterator iter = feed.getEntries().listIterator();
                     iter.hasNext();)
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

    private static void readDelBookmarkFile(final String file, final Map marks)
        throws IOException, JDOMException
    {
        final FileReader reader = new FileReader(file);
        final Document document = (new SAXBuilder()).build(reader);
        final Element rootElem = document.getRootElement();
        final List children = rootElem.getChildren("post");
        String key;
        Element element;

        for (final Iterator iter = children.iterator(); iter.hasNext(); )
        {
            element = (Element)iter.next();
            key = element.getAttributeValue("href");
            if ((key != null) && (key.length() > 0))
            {
                marks.put(key, key);
            }
        }
    }

    private class OrderByDate implements Comparator
    {
        public int compare(final Object aObj, final Object bObj)
        {
            final Date aDate = ((SyndEntry)aObj).getPublishedDate();
            final Date bDate = ((SyndEntry)bObj).getPublishedDate();
            return (aDate == null) ? -1 : (bDate == null) ? 1 :
                aDate.compareTo(bDate);
        }
    }
}
