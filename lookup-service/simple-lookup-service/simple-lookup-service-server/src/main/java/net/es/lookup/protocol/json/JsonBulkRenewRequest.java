package net.es.lookup.protocol.json;

import java.util.List;
import net.es.lookup.common.BulkRenewRequest;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;

public class JsonBulkRenewRequest extends BulkRenewRequest {

  public static final int VALID = 1;
  public static final int INCORRECT_FORMAT = 2;

  public JsonBulkRenewRequest(String message) {

    this.parseJson(message);
  }

  private void parseJson(String message) {

    try {

      JSONTokener tokener = new JSONTokener(message);

      Object obj = tokener.nextValue();

      for (Object o : ((JSONObject) obj).keySet()) {

        this.add(o.toString(), ((JSONObject) obj).get(o));
      }

      this.status = VALID;

    } catch (JSONException e) {

      this.status = INCORRECT_FORMAT;
    }
  }
}
