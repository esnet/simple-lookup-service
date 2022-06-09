package net.es.lookup.queries.Network;

import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.QueryException;
import net.es.lookup.queries.Query;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bmah on 7/30/14.
 */
public class PSMetadataQuery extends Query {

    public PSMetadataQuery() {
        super();
        List<String> tmp = new LinkedList<String>();
        tmp.add(ReservedValues.RECORD_VALUE_TYPE_PSMETADATA);
        try {
            this.add(ReservedKeys.RECORD_TYPE, tmp);
        }
        catch (QueryException ignored) {}
    }

    public List<InetAddress> getDstAddress() throws QueryException {
        List<InetAddress> addresses = new LinkedList<InetAddress>();
        for (String a : (List<String>) this.getValue(ReservedKeys.RECORD_PSMETADATA_DST_ADDRESS)) {
            try {
                addresses.add(InetAddress.getByName(a));
            } catch (UnknownHostException e) {

                throw new QueryException(ReservedKeys.RECORD_PSMETADATA_DST_ADDRESS + " could not be converted back to InetAddress");
            }
        }
        return addresses;
    }

    public void setDstAddress(List<InetAddress> addresses) throws QueryException {
        List<String> addressList = new LinkedList<String>();
        if (addresses != null && !addresses.isEmpty()) {
            for (InetAddress a : addresses) {
                addressList.add(a.toString());
            }
            this.add(ReservedKeys.RECORD_PSMETADATA_DST_ADDRESS, addressList);
        } else {
            throw new QueryException(ReservedKeys.RECORD_PSMETADATA_DST_ADDRESS + " is empty");
        }
    }

    public List<String> getEventTypes() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_PSMETADATA_EVENTTYPES);
    }

    public void setEventTypes(List<String> et) throws QueryException {

        if (et != null && !et.isEmpty()) {
            this.add(ReservedKeys.RECORD_PSMETADATA_EVENTTYPES, et);
        } else {
            throw new QueryException(ReservedKeys.RECORD_PSMETADATA_EVENTTYPES + " is empty");
        }
    }

    public List<String> getMALocator() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_PSMETADATA_MA_LOCATOR);
    }

    public void setMALocator(List<String> ma) throws QueryException {

        if (ma != null && !ma.isEmpty()) {
            this.add(ReservedKeys.RECORD_PSMETADATA_MA_LOCATOR, ma);
        } else {
            throw new QueryException(ReservedKeys.RECORD_PSMETADATA_MA_LOCATOR + " is empty");
        }
    }

    public List<InetAddress> getMeasurementAgent() throws QueryException {
        List<InetAddress> ma = new LinkedList<InetAddress>();
        for (String a : (List<String>) this.getValue(ReservedKeys.RECORD_PSMETADATA_MEASUREMENT_AGENT)) {
            try {
                ma.add(InetAddress.getByName(a));
            } catch (UnknownHostException e) {

                throw new QueryException(ReservedKeys.RECORD_PSMETADATA_MEASUREMENT_AGENT + " could not be converted back to InetAddress");
            }
        }
        return ma;
    }

    public void setMeasurementAgent(List<InetAddress> ma) throws QueryException {
        List<String> addressList = new LinkedList<String>();
        if (ma != null && !ma.isEmpty()) {
            for (InetAddress a : ma) {
                addressList.add(a.toString());
            }
            this.add(ReservedKeys.RECORD_PSMETADATA_MEASUREMENT_AGENT, addressList);
        } else {
            throw new QueryException(ReservedKeys.RECORD_PSMETADATA_MEASUREMENT_AGENT + " is empty");
        }
    }

    public List<InetAddress> getSrcAddress() throws QueryException {
        List<InetAddress> addresses = new LinkedList<InetAddress>();
        for (String a : (List<String>) this.getValue(ReservedKeys.RECORD_PSMETADATA_SRC_ADDRESS)) {
            try {
                addresses.add(InetAddress.getByName(a));
            } catch (UnknownHostException e) {

                throw new QueryException(ReservedKeys.RECORD_PSMETADATA_SRC_ADDRESS + " could not be converted back to InetAddress");
            }
        }
        return addresses;
    }

    public void setSrcAddress(List<InetAddress> addresses) throws QueryException {
        List<String> addressList = new LinkedList<String>();
        if (addresses != null && !addresses.isEmpty()) {
            for (InetAddress a : addresses) {
                addressList.add(a.toString());
            }
            this.add(ReservedKeys.RECORD_PSMETADATA_SRC_ADDRESS, addressList);
        } else {
            throw new QueryException(ReservedKeys.RECORD_PSMETADATA_SRC_ADDRESS + " is empty");
        }
    }

    public List<String> getToolName() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_PSMETADATA_TOOL_NAME);
    }

    public void setToolName(List<String> ma) throws QueryException {

        if (ma != null && !ma.isEmpty()) {
            this.add(ReservedKeys.RECORD_PSMETADATA_TOOL_NAME, ma);
        } else {
            throw new QueryException(ReservedKeys.RECORD_PSMETADATA_TOOL_NAME + " is empty");
        }
    }

    public List<String> getUri() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_PSMETADATA_URI);
    }

    public void setUri(List<String> uri) throws QueryException {

        if (uri != null && !uri.isEmpty()) {
            this.add(ReservedKeys.RECORD_PSMETADATA_URI, uri);
        } else {
            throw new QueryException(ReservedKeys.RECORD_PSMETADATA_URI + " is empty");
        }
    }


}
