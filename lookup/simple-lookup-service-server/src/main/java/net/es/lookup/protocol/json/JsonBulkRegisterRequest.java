package net.es.lookup.protocol.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.es.lookup.common.Message;
import org.json.JSONArray;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;

public class JsonBulkRegisterRequest extends Message {

  private static final int VALID = 1;
  private static final int INCORRECT_FORMAT = 2;

  public List<Message> parseJson(String message) {

    List<Message> messages = new ArrayList<Message>();

    Map<String, Object> allMessageMap =
        new Gson().fromJson(message, new TypeToken<HashMap<String, Object>>() {}.getType());
    ArrayList messageList = (ArrayList) allMessageMap.get("items");
    try {
      ObjectMapper oMapper = new ObjectMapper();
      for (Object list : messageList) {
        Map<String, Object> map = oMapper.convertValue(list, Map.class);

        Message m = new Message(map);
        messages.add(m);
      }
    } catch (Exception e) {
      this.status = INCORRECT_FORMAT;
      //throw new JSONException("Json not formatted correctly:" + e.getMessage());
    }
    this.status = VALID;
    return messages;
  }
}
