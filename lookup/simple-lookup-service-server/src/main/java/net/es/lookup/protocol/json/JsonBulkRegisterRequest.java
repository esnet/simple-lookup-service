package net.es.lookup.protocol.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.es.lookup.common.BulkRegisterRequest;
import net.es.lookup.common.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonBulkRegisterRequest extends BulkRegisterRequest {

  public static final int VALID = 1;
  public static final int INCORRECT_FORMAT = 2;

  /**
   * Parses Json from String and returns a list of available messages
   *
   * @param message json message to be parsed
   * @return list of parsed messages from Json
   */
  public List<Message> parseJson(String message) {

    List<Message> messages = new ArrayList<Message>();

    Map<String, Object> allMessageMap =
        new Gson().fromJson(message, new TypeToken<HashMap<String, Object>>() {}.getType());

    ArrayList messageList = (ArrayList) allMessageMap.get("records");
    Object ttl = allMessageMap.get("ttl");

    try {
      ObjectMapper oMapper = new ObjectMapper();
      for (Object list : messageList) {
        Map<String, Object> map = oMapper.convertValue(list, Map.class);
        if (ttl != null) {
          map.put("ttl", ttl);
        }
        Message m = new Message(map);
        messages.add(m);
      }
    } catch (Exception e) {
      this.status = INCORRECT_FORMAT;
    }
    this.status = VALID;
    return messages;
  }
}
