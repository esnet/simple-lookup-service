package net.es.lookup.records.Network;

import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.RecordException;
import net.es.lookup.records.Record;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by bmah on 7/30/14.
 */
public class PSMetadataRecord extends Record {

    public PSMetadataRecord() {
        super(ReservedValues.RECORD_VALUE_TYPE_PSMETADATA);
    }

    public InetAddress getDstAddress() throws RecordException {
        try {
            return (InetAddress.getByName(getStringFromListValue(ReservedKeys.RECORD_PSMETADATA_DST_ADDRESS)));
        } catch (UnknownHostException e) {
            throw new RecordException(ReservedKeys.RECORD_PSMETADATA_DST_ADDRESS + " could not be converted back to InetAddress");
        }
    }

    public void setDstAddress(InetAddress dstAddress) throws RecordException {
        addStringAsListValue(ReservedKeys.RECORD_PSMETADATA_DST_ADDRESS, dstAddress.toString());
    }

    public List<String> getEventTypes() {
        return (List<String>) getValue(ReservedKeys.RECORD_PSMETADATA_EVENTTYPES);
    }

    public void setEventTypes(List<String> et) {
        add(ReservedKeys.RECORD_PSMETADATA_EVENTTYPES, et);
    }

    public List<String> getMALocator() {
        return (List<String>) getValue(ReservedKeys.RECORD_PSMETADATA_MA_LOCATOR);
    }

    public void setMALocator(List<String> mal) {
        add(ReservedKeys.RECORD_PSMETADATA_MA_LOCATOR, mal);
    }

    public InetAddress getMeasurementAgent() throws RecordException {
        try {
            return (InetAddress.getByName(getStringFromListValue(ReservedKeys.RECORD_PSMETADATA_MEASUREMENT_AGENT)));
        } catch (UnknownHostException e) {
            throw new RecordException(ReservedKeys.RECORD_PSMETADATA_MEASUREMENT_AGENT + " could not be converted back to InetAddress");
        }
    }

    public void setMeasurementAgent(InetAddress ma) throws RecordException {
        addStringAsListValue(ReservedKeys.RECORD_PSMETADATA_MEASUREMENT_AGENT, ma.toString());
    }

    public InetAddress getSrcAddress() throws RecordException {
        try {
            return (InetAddress.getByName(getStringFromListValue(ReservedKeys.RECORD_PSMETADATA_SRC_ADDRESS)));
        } catch (UnknownHostException e) {
            throw new RecordException(ReservedKeys.RECORD_PSMETADATA_SRC_ADDRESS + " could not be converted back to InetAddress");
        }
    }

    public void setSrcAddress(InetAddress srcAddress) throws RecordException {
        addStringAsListValue(ReservedKeys.RECORD_PSMETADATA_SRC_ADDRESS, srcAddress.toString());
    }

    public String getToolName() {
        return getStringFromListValue(ReservedKeys.RECORD_PSMETADATA_TOOL_NAME);
    }

    public void setToolName(String tool) throws RecordException {
        addStringAsListValue(ReservedKeys.RECORD_PSMETADATA_TOOL_NAME, tool);
    }

    public String getUri() {
        return getStringFromListValue(ReservedKeys.RECORD_PSMETADATA_URI);
    }

    public void setUri(String uri) throws RecordException {
        addStringAsListValue(ReservedKeys.RECORD_PSMETADATA_URI, uri);
    }
}
