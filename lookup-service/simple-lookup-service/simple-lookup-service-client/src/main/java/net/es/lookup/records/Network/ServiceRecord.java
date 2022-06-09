package net.es.lookup.records.Network;


import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.RecordException;
import net.es.lookup.records.DataValidator;
import net.es.lookup.records.Record;
import org.apache.commons.lang.ObjectUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * User: sowmya
 * Date: 12/25/12
 * Time: 1:18 PM
 */
public class ServiceRecord extends Record {

    public ServiceRecord(){
        super(ReservedValues.RECORD_VALUE_TYPE_SERVICE);
    }

    public String getServiceName() {
        return getStringFromListValue(ReservedKeys.RECORD_SERVICE_NAME);
    }

    public void setServiceName(String serviceName) throws RecordException {
        addStringAsListValue(ReservedKeys.RECORD_SERVICE_NAME, serviceName);
    }

    public String getServiceType() {
        return getStringFromListValue(ReservedKeys.RECORD_SERVICE_TYPE);
    }

    public void setServiceType(String serviceType) throws RecordException {
        addStringAsListValue(ReservedKeys.RECORD_SERVICE_TYPE, serviceType);
    }

    public String getServiceVersion() {
        return getStringFromListValue(ReservedKeys.RECORD_SERVICE_VERSION);
    }

    public void setServiceVersion(String serviceVersion) throws RecordException {
        addStringAsListValue(ReservedKeys.RECORD_SERVICE_VERSION, serviceVersion);
    }

    public List<String> getServiceLocator() {
        return (List<String>) this.getValue(ReservedKeys.RECORD_SERVICE_LOCATOR);
    }

    public void setServiceLocator(List<String> serviceLocator) throws RecordException {
        if (serviceLocator != null && !serviceLocator.isEmpty()) {
            this.add(ReservedKeys.RECORD_SERVICE_LOCATOR, serviceLocator);
        }
        else {
            throw new RecordException(ReservedKeys.RECORD_SERVICE_LOCATOR + " is empty");
        }
    }

    public String getEventTypes() {
        return getStringFromListValue(ReservedKeys.RECORD_SERVICE_EVENTTYPES);
    }

    public void setEventTypes(String t) throws RecordException {
        addStringAsListValue(ReservedKeys.RECORD_SERVICE_EVENTTYPES, t);
    }

    public List<String> getDomains() {
        return (List<String>) this.getValue(ReservedKeys.RECORD_GROUP_DOMAINS);
    }

    public void setDomains(List<String> domains) throws RecordException {
        if (domains != null && !domains.isEmpty()){
            this.add(ReservedKeys.RECORD_GROUP_DOMAINS, domains);
        }
        else {
            throw new RecordException(ReservedKeys.RECORD_GROUP_DOMAINS + " is empty");
        }
    }

    public List<String> getCommunities() {
        return (List<String>) this.getValue(ReservedKeys.RECORD_GROUP_COMMUNITIES);
    }

    public void setCommunities(List<String> communities) throws RecordException {
        if (communities != null && !communities.isEmpty()) {
            this.add(ReservedKeys.RECORD_GROUP_COMMUNITIES, communities);
        }
        else {
            throw new RecordException(ReservedKeys.RECORD_GROUP_COMMUNITIES + " is empty");
        }
    }

    public String getSiteName() {
        return getStringFromListValue(ReservedKeys.RECORD_LOCATION_SITENAME);
    }

    public void setSiteName(String siteName) throws RecordException {
        addStringAsListValue(ReservedKeys.RECORD_LOCATION_SITENAME, siteName);
    }

    public String getCity() {
        return getStringFromListValue(ReservedKeys.RECORD_LOCATION_CITY);
    }

    public void setCity(String city) throws RecordException {
        addStringAsListValue(ReservedKeys.RECORD_LOCATION_CITY, city);
    }

    public String getState() {
        return getStringFromListValue(ReservedKeys.RECORD_LOCATION_STATE);
    }

    @Deprecated
    public String getRegion() { return getState(); }

    public void setState(String state) throws RecordException {
        addStringAsListValue(ReservedKeys.RECORD_LOCATION_STATE, state);
    }

    @Deprecated
    public void setRegion(String state) throws RecordException { setState(state); }

    public String getCountry() {
        return getStringFromListValue(ReservedKeys.RECORD_LOCATION_COUNTRY);
    }

    public void setCountry(String country) throws RecordException {
        addStringAsListValue(ReservedKeys.RECORD_LOCATION_COUNTRY, country);
    }

    public String getZipcode() {
        return getStringFromListValue(ReservedKeys.RECORD_LOCATION_ZIPCODE);
    }

    public void setZipcode(String zipcode) throws RecordException {
        addStringAsListValue(ReservedKeys.RECORD_LOCATION_ZIPCODE, zipcode);
    }

    public double getLatitude() {
        try {
            return Double.parseDouble(getStringFromListValue(ReservedKeys.RECORD_LOCATION_LATITUDE));
        }
        catch (NullPointerException e) {
            return Double.NaN;
        }
    }

    public void setLatitude(double latitude) throws RecordException {
        if (DataValidator.isValidLatitude(latitude)) {
            addStringAsListValue(ReservedKeys.RECORD_LOCATION_LATITUDE, Double.toString(latitude));
        }
        else {
            throw new RecordException(ReservedKeys.RECORD_LOCATION_LATITUDE + " is out of range (-90,90)");
        }
    }

    public double getLongitude() {
        try {
            return Double.parseDouble(getStringFromListValue(ReservedKeys.RECORD_LOCATION_LONGITUDE));
        }
        catch (NullPointerException e) {
            return Double.NaN;
        }
    }

    public void setLongitude(double longitude) throws RecordException {
        if (DataValidator.isValidLongitude(longitude)) {
            addStringAsListValue(ReservedKeys.RECORD_LOCATION_LONGITUDE, Double.toString(longitude));
        }
        else {
            throw new RecordException(ReservedKeys.RECORD_LOCATION_LONGITUDE + " is out of range (-180,180)");
        }
    }

    public List<String> getAdministrators() {
        return (List<String>) this.getValue(ReservedKeys.RECORD_SERVICE_ADMINISTRATORS);
    }

    public void setAdministrators(List<String> administrators) throws RecordException {
        if (administrators != null && !administrators.isEmpty()) {
            this.add(ReservedKeys.RECORD_SERVICE_ADMINISTRATORS, administrators);
        }
        else {
            throw new RecordException(ReservedKeys.RECORD_SERVICE_ADMINISTRATORS+" is empty");
        }
    }

    public String getHost() {
        return getStringFromListValue(ReservedKeys.RECORD_SERVICE_HOST);
    }

    public void setHost(String host) throws RecordException {
        addStringAsListValue(ReservedKeys.RECORD_SERVICE_HOST, host);
    }
}
