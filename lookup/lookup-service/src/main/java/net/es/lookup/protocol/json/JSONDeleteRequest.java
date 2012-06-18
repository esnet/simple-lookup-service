package net.es.lookup.protocol.json;

import net.es.lookup.common.exception.internal.DuplicateKeyException;
import net.es.lookup.common.DeleteRequest;
import net.es.lookup.common.Service;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.util.JSONTokener;
import net.sf.json.JSONObject;

import org.joda.time.Duration;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;
import net.sf.json.JSONException;

import net.es.lookup.common.ReservedKeywords;

public class JSONDeleteRequest extends DeleteRequest{
	static public final int VALID = 1;
	static public final int INCORRECT_FORMAT =  2;
	public JSONDeleteRequest (String message) throws DuplicateKeyException {
		this.parseJSON(message);
	}

	private void parseJSON (String message) throws DuplicateKeyException {
		try{
			if(!message.isEmpty()){
				JSONTokener tokener = new JSONTokener(message);

				Object obj = tokener.nextValue();

				JSONObject jsonObj = (JSONObject) obj;
				Set keyValues = jsonObj.entrySet();//
				for (Object o : ((JSONObject) obj).keySet()) {
					if (o.toString().equals(ReservedKeywords.RECORD_TTL)) {
						PeriodFormatter fmt = ISOPeriodFormat.standard();
						Duration duration = fmt.parsePeriod((String) ((JSONObject) obj).get(o)).toStandardDuration();
						this.add(o.toString(), new Long(duration.getStandardSeconds()));
					}
					else{
						this.add(o.toString(), ((JSONObject) obj).get(o));
					}	
				}
			}
			this.status = JSONDeleteRequest.VALID;
		}
		catch(JSONException e){
			this.status = JSONDeleteRequest.INCORRECT_FORMAT;
		}
	}
}