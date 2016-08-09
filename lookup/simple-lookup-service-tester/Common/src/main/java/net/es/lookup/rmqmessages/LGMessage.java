package net.es.lookup.rmqmessages;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by kamala on 6/2/16.
 */
public class LGMessage implements Serializable
{
    private String uri;
    private Date timestamp;
    private int messageId;
    private Date expiresDate;
    private String messageType;
    private boolean isStored;

    public static final String REGISTER = "REGISTER";
    public static final String RENEW = "RENEW";

    public boolean getIsStored()
    {
        return this.isStored;
    }
    public void setIsStored(boolean storeFlag)
    {
        this.isStored = storeFlag;
    }

    public void setMessageType(String messageType)
    {
        this.messageType = messageType;
    }

    public String getMessageType()
    {
        return this.messageType;
    }

    public void setExpiresDate(Date expiresDate)
    {
        this.expiresDate = expiresDate;
    }

    public Date getExpiresDate()
    {
        return expiresDate;
    }

    public int getMessageId()
    {
        return messageId;
    }

    public void setMessageId(int messageId)
    {
        this.messageId = messageId;
    }

    public String getUri()
    {
        return uri;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Date timestamp)
    {
        this.timestamp = timestamp;
    }
}
