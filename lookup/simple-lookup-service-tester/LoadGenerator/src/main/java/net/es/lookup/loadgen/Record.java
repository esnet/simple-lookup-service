package net.es.lookup.loadgen;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Record
{
    private String uri;
    private Date expiresDate;
    private boolean isStored;
    public DateFormat dateFormat;
    public Record(String uri, String expiresString)
    {

        this.uri = uri;
        TimeZone tz = TimeZone.getTimeZone("UTC");
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateFormat.setTimeZone(tz);

        try
        {
            expiresDate = dateFormat.parse(expiresString);

        }
        catch(ParseException e)
        {
            e.printStackTrace();
        }
    }

    public boolean getIsStored()
    {
        return this.isStored;
    }

    public void setIsStored(boolean flag)
    {
        this.isStored = flag;
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
