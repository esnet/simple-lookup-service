package net.es.lookup.protocol.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.es.lookup.common.Message;
import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonBulkRegisterRequest extends Message {

  private static final int VALID = 1;
  private static final int INCORRECT_FORMAT = 2;

  public List<Message> parseJson(String message) {

    List<Message> messages = new ArrayList<Message>();
    message = message.replace("{", "");
    message = message.substring(0, message.lastIndexOf("}"));
    message = message.substring(message.indexOf("[") + 1);
    String[] items =
        message.replaceAll("\\s", "").split("},");

    try {

      for (int i = 0; i < items.length; i++) {
        Map<String, Object> messageMap = new HashMap<>();
        // System.out.println(items[i].replace("{", "").replace("}", "")+ "\n");

        String keys[] = items[i].split(",");

        String lastKey = "";
        String lastValue = "";
        for (String item : keys) {
          //System.out.println(item);
          String innerKeys[] = item.split(",");


          for (String inner : innerKeys) {
            //System.out.println(inner);
            String keyVal[] = new String[2];
            if(!inner.contains(":")){
              lastValue += ","+inner;
              if(inner.contains("]"));{
                messageMap.put(lastKey, lastValue);
              }
            } else {
              keyVal = inner.split(":");
              lastKey = keyVal[0].replace("\"", "");
              lastValue = keyVal[1].replace("\"", "");
              messageMap.put(lastKey, lastValue);
            }
          }

        }
        //System.out.println(messageMap.get("type"));
        messages.add(new Message(messageMap));

      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    // return stringArray;

    return messages;
  }
}
