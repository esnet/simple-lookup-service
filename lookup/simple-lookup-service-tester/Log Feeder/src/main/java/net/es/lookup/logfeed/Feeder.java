package net.es.lookup.logfeed; /**
 * Feeder
 * Starts reading periodically from the log elastic search server from a randomly
 * generated time. This will feed the number of register and renew
 * requests to the Load Generator.
 */
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import org.elasticsearch.index.query.QueryBuilders;


public class Feeder implements Runnable
{
    private Calendar calendar;
    private Date seedTimeStamp;
    private Date currentTimeStamp;
    private Date nextTimeStamp;

    private static long difference; // for every second / minute. This will differ.
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private int period;  /*In Milliseconds*/
    private Client esClient; /*The Elastic search client*/
    private long lastExecutionTime = -1;
    private long interval=1100; // 1 second


    /*getters and setters*/

    public Date getSeedTimeStamp()
    {
        return seedTimeStamp;
    }


    public void setSeedTimeStamp(Date seedTimeStamp)
    {
        this.seedTimeStamp = seedTimeStamp;
    }

    public Date getcurrentTimeStamp()
    {
        return currentTimeStamp;
    }

    public void setCurrentTimeStamp(Date currentTimeStamp)
    {
        this.currentTimeStamp = currentTimeStamp;
    }

    public int getPeriod()
    {
        return period;
    }

    public void setPeriod(int period)
    {
        this.period = period;
    }
    /*END - getters and setters*/

    public Feeder()
    {
        calendar = Calendar.getInstance();


    }

    public void run()
    {
       // on startup - get client for elastic search
        TimeZone tz = TimeZone.getTimeZone("UTC");
        dateFormat.setTimeZone(tz);
        try
        {
            esClient = TransportClient.builder().build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(Constants.LOGHOSTNAME), 9300));
        }
        catch(UnknownHostException e)
        {
            System.err.println("Error in getting client object from elastic search");
            System.err.println("Check firewall, maybe?");
            e.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.exit(-1);
        }

        currentTimeStamp = seedTimeStamp;

        while(true)
        {

            try
            {
                Runner.feederSemaphore.acquire();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            /*Sleep if one second hasn't passed*/
            long currentTime = System.currentTimeMillis();
            long difference = currentTime - lastExecutionTime;

            if(difference < interval && lastExecutionTime != -1)
            {
                try
                {
                    Thread.sleep(interval- difference);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }



            calendar.setTime(currentTimeStamp);

            //once every second.
            calendar.add(Calendar.SECOND,1);

            nextTimeStamp = calendar.getTime();//2016-05-10T05:15:00

            SearchResponse renewSearch = esClient.prepareSearch()
                    .setTypes("logs")
                    .setSearchType(SearchType.QUERY_AND_FETCH)
                    .setQuery(  QueryBuilders.andQuery(QueryBuilders.rangeQuery("@timestamp")
                                                    .gte(dateFormat.format(currentTimeStamp))
                                                    .lt(dateFormat.format(nextTimeStamp)),
                                QueryBuilders.matchQuery("message","renewService")))
                    .execute()
                    .actionGet();

            SearchResponse registerSearch = esClient.prepareSearch()
                    .setTypes("logs")
                    .setSearchType(SearchType.QUERY_AND_FETCH)
                    .setQuery(QueryBuilders.andQuery(QueryBuilders.rangeQuery("@timestamp")
                                                      .gte(dateFormat.format(currentTimeStamp))
                                                       .lt(dateFormat.format(nextTimeStamp)),
                                                    QueryBuilders.matchQuery("message","queryService")))
                    .execute()
                    .actionGet();

            System.out.println("Time:" + dateFormat.format(currentTimeStamp)
                                + " Register:"+ registerSearch.getHits().totalHits()
                                + " Renew:" + renewSearch.getHits().totalHits());

            currentTimeStamp = nextTimeStamp;
            Runner.currentRegisterRequests = registerSearch.getHits().totalHits();
            Runner.currentRenewRequests = renewSearch.getHits().totalHits();
            Runner.kvgSemaphore.release();

            lastExecutionTime = System.currentTimeMillis();



        }

    }
}
