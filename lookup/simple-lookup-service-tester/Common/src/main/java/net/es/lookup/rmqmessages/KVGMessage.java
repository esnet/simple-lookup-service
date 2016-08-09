package net.es.lookup.rmqmessages;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by kamala on 5/26/16.
 */
public class KVGMessage implements Serializable
{
    public static final String REGISTER= "REGISTER";
    public static final String RENEW = "RENEW";
    private String messageType;
    private String UUID;
    private HashMap<String,String> map;
    private int messageId;



    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getMessageType() {

        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public HashMap<String, String> getMap() {

        return map;
    }

    public void setMap(HashMap<String, String> map) {
        this.map = map;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }
}
