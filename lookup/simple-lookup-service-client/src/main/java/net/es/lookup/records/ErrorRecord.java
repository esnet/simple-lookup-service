package net.es.lookup.records;

import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.RecordException;

/**
 * Author: sowmya
 * Date: 4/30/13
 * Time: 6:07 PM
 */
public class ErrorRecord extends Record {

    public ErrorRecord() {

        super(ReservedValues.RECORD_VALUE_TYPE_ERROR);
    }

    public void setErrorMessage(String errorMessage) throws RecordException {
        if(errorMessage != null){
            this.add(ReservedKeys.ERROR_MESSAGE, errorMessage);
        } else {
            throw new RecordException("Error message is null");
        }

    }

    public String getErrorMessage(){
        return (String) this.getValue(ReservedKeys.ERROR_MESSAGE);
    }

    public void setErrorCode(int responseCode) {
        this.add(ReservedKeys.ERROR_CODE, responseCode);
    }

    public int getErrorCode() {
        return (Integer) this.getValue(ReservedKeys.ERROR_CODE);
    }


}
