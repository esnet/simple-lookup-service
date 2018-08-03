package net.es.lookup.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class BulkRenewResponse extends Message {


  public BulkRenewResponse() {

    super();
  }

  public BulkRenewResponse(Map<String, Object> map) {

    super(map);
  }

  public void addTotalRecordsCount(int recordCount) {

    this.add(ReservedKeys.RECORD_BULKRENEW_TOTALRECORDS, String.valueOf(recordCount));
  }


  /**
   * This method populates the error codes and message for the failed uris
   * */
  public void updateFailures(Map<String, Message> failedUris) {

    this.add(ReservedKeys.RECORD_BULKRENEW_FAILURECOUNT, String.valueOf(failedUris.size()));

    List<String> uriList = new ArrayList<>();
    List<String> errorCodes = new ArrayList<>();
    List<String> errorMessages = new ArrayList<>();

    for (Entry<String, Message> uriEntry : failedUris.entrySet()) {

      uriList.add(uriEntry.getKey());
      errorCodes.add(String.valueOf(uriEntry.getValue().getKey(ReservedKeys.ERROR_CODE)));
      errorMessages.add((String) uriEntry.getValue().getKey(ReservedKeys.ERROR_MESSAGE));
    }

    this.add(ReservedKeys.RECORD_BULKRENEW_FAILUREURIS, uriList);
    this.add(ReservedKeys.ERROR_CODE, errorCodes);
    this.add(ReservedKeys.ERROR_MESSAGE, errorMessages);
  }

  public void updateRenewedCount(Message renewResponse) {

    this.add(ReservedKeys.RECORD_BULKRENEW_RENEWEDCOUNT, String.valueOf(renewResponse.getKey(ReservedKeys.RECORD_BULKRENEW_RENEWEDCOUNT)));
  }
}
