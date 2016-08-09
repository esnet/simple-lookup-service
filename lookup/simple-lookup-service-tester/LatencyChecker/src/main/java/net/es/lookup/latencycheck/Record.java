package net.es.lookup.latencycheck;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Record
{
    private String uri;
    private Date expiresDate;
    public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    public Record(String uri, String expiresString)
    {

        this.uri = uri;
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
        df.setTimeZone(tz);

        try
        {
            expiresDate = df.parse(expiresString);
            System.out.println(expiresDate);
        }
        catch(ParseException e)
        {
            e.printStackTrace();
        }




    }


    /*Getters*/
    public String getUri()
    {
        return this.uri;
    }

    public Date getExpiresDate()
    {
        return this.expiresDate;
    }

    public boolean isDateUpdated(Date toCompareDate)
    {
        if(expiresDate.before(toCompareDate))
        {
            return true;
        }

        return false;
    }
}
