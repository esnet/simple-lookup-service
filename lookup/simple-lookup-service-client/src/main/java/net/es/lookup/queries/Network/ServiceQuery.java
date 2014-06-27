package net.es.lookup.queries.Network;

import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.QueryException;
import net.es.lookup.queries.Query;
import net.es.lookup.records.DataValidator;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: luke
 * Date: 6/17/13
 * Time: 8:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class ServiceQuery extends Query {

    public ServiceQuery() {

        super();
        List<String> tmp = new LinkedList<String>();
        tmp.add(ReservedValues.RECORD_VALUE_TYPE_SERVICE);

        try {

            this.add(ReservedKeys.RECORD_TYPE, tmp);
        } catch (QueryException ignored) { }
    }

    public List<String> getServiceName() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_SERVICE_NAME);
    }

    public void setServiceName(List<String> serviceName) throws QueryException {

        if (serviceName == null || serviceName.isEmpty()) {
            throw new QueryException(ReservedKeys.RECORD_SERVICE_NAME + " is empty");
        } else {
            this.add(ReservedKeys.RECORD_SERVICE_NAME, serviceName);
        }
    }

    public List<String> getServiceVersion() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_SERVICE_VERSION);
    }

    public void setServiceVersion(List<String> serviceVersion) throws QueryException {

        if (serviceVersion !=null && !serviceVersion.isEmpty()){
            this.add(ReservedKeys.RECORD_SERVICE_VERSION, serviceVersion);
        } else {
            throw new QueryException(ReservedKeys.RECORD_SERVICE_VERSION + " is empty");
        }
    }

    public List<String> getServiceType() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_SERVICE_TYPE);
    }

    public void setServiceType(List<String> serviceType) throws QueryException {

        if (serviceType !=null && !serviceType.isEmpty()) {
            this.add(ReservedKeys.RECORD_SERVICE_TYPE, serviceType);
        } else {
            throw new QueryException(ReservedKeys.RECORD_SERVICE_TYPE + " is empty");
        }
    }

    public List<String> getServiceLocator() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_SERVICE_LOCATOR);
    }

    public void setServiceLocator(List<String> serviceLocator) throws QueryException {

        if (serviceLocator !=null && !serviceLocator.isEmpty()) {
            this.add(ReservedKeys.RECORD_SERVICE_LOCATOR, serviceLocator);
        } else {
            throw new QueryException(ReservedKeys.RECORD_SERVICE_LOCATOR+" is empty");
        }
    }

    public List<String> getDomains() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_GROUP_DOMAINS);
    }

    public void setDomains(List<String> domains) throws QueryException {

        if (domains !=null && !domains.isEmpty()) {
            this.add(ReservedKeys.RECORD_GROUP_DOMAINS, domains);
        } else {
            throw new QueryException(ReservedKeys.RECORD_GROUP_DOMAINS+" is empty");
        }
    }

    public List<String> getCommunities() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_GROUP_COMMUNITIES);
    }

    public void setCommunities(List<String> communities) throws QueryException {

        if (communities != null && !communities.isEmpty()) {
            this.add(ReservedKeys.RECORD_GROUP_COMMUNITIES, communities);
        } else {
            throw new QueryException(ReservedKeys.RECORD_GROUP_COMMUNITIES + " is empty");
        }
    }

    public List<String> getSiteName() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_LOCATION_SITENAME);
    }

    public void setSiteName(List<String> siteName) throws QueryException {

        if (siteName !=null && !siteName.isEmpty()) {
            this.add(ReservedKeys.RECORD_LOCATION_SITENAME, siteName);
        } else {
            throw new QueryException(ReservedKeys.RECORD_LOCATION_SITENAME+" is empty");
        }
    }

    public List<String> getCity() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_LOCATION_CITY);
    }

    public void setCity(List<String> city) throws QueryException {

        if (city !=null && !city.isEmpty()) {
            this.add(ReservedKeys.RECORD_LOCATION_CITY, city);
        } else {
            throw new QueryException(ReservedKeys.RECORD_LOCATION_CITY+" is empty");
        }
    }

    public List<String> getState() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_LOCATION_STATE);
    }

    @Deprecated
    public List<String> getRegion() { return getState(); }

    public void setState(List<String> state) throws QueryException {

        if (state !=null && !state.isEmpty()) {
            this.add(ReservedKeys.RECORD_LOCATION_STATE, state);
        } else {
            throw new QueryException(ReservedKeys.RECORD_LOCATION_STATE +" is empty");
        }
    }

    @Deprecated
    public void setRegion(List<String> state) throws QueryException { setState(state); }

    public List<String> getCountry() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_LOCATION_COUNTRY);
    }

    public void setCountry(List<String> country) throws QueryException {

        if (country != null) {
            List<String> countries = new LinkedList<String>();
            for (String c : country) {
                if (c != null && !c.isEmpty() && DataValidator.isValidCountry(c)) {
                    countries.add(c);
                } else {
                    throw new QueryException(ReservedKeys.RECORD_LOCATION_COUNTRY + " is invalid");
                }
                this.add(ReservedKeys.RECORD_LOCATION_COUNTRY, countries);
            }
        }
    }

    public List<String> getZipcode() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_LOCATION_ZIPCODE);
    }

    public void setZipcode(List<String> zipcode) throws QueryException {

        if (zipcode != null && !zipcode.isEmpty()) {
            this.add(ReservedKeys.RECORD_LOCATION_ZIPCODE, zipcode);
        } else {
            throw new QueryException(ReservedKeys.RECORD_LOCATION_ZIPCODE+" is empty");
        }
    }

    public List<Double> getLatitude() {

        List<Double> latitudes = new LinkedList<Double>();
        for (String l : (List<String>) this.getValue(ReservedKeys.RECORD_LOCATION_LATITUDE)) {
            latitudes.add(Double.parseDouble(l));
        }
        return latitudes;
    }

    public void setLatitude(List<Double> latitude) throws QueryException {

        if (latitude != null) {
            List<String> latitudes = new LinkedList<String>();
            for (Double l : latitude) {
                if (DataValidator.isValidLatitude(l)) {
                    latitudes.add(l.toString());
                } else {
                    throw new QueryException(ReservedKeys.RECORD_LOCATION_LATITUDE + " is out of range (-90,90)");
                }
            }
            this.add(ReservedKeys.RECORD_LOCATION_LATITUDE, latitudes);
        }
    }

    public List<Double> getLongitude() {

        List<Double> longitudes = new LinkedList<Double>();
        for (String l : (List<String>) this.getValue(ReservedKeys.RECORD_LOCATION_LATITUDE)) {
            longitudes.add(Double.parseDouble(l));
        }
        return longitudes;
    }

    public void setLongitude(List<Double> longitude) throws QueryException {

        if (longitude != null) {
            List<String> longitudes = new LinkedList<String>();
            for (Double l : longitude) {
                if (DataValidator.isValidLongitude(l)) {
                    longitudes.add(l.toString());
                } else {
                    throw new QueryException(ReservedKeys.RECORD_LOCATION_LONGITUDE + " is out of range (-180,180)");
                }
            }
            this.add(ReservedKeys.RECORD_LOCATION_LONGITUDE, longitudes);
        }
    }

    public List<String> getAdministrators() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_SERVICE_ADMINISTRATORS);
    }

    public void setAdministrators(List<String> administrators) throws QueryException {

        if (administrators != null && !administrators.isEmpty()) {
            this.add(ReservedKeys.RECORD_SERVICE_ADMINISTRATORS, administrators);
        } else {
            throw new QueryException(ReservedKeys.RECORD_SERVICE_ADMINISTRATORS+" is empty");
        }
    }

    public List<String> getHost() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_SERVICE_HOST);
    }

    public void setHost(List<String> host) throws QueryException {

        if (host != null && !host.isEmpty()) {
            this.add(ReservedKeys.RECORD_SERVICE_HOST, host);
        } else {
            throw new QueryException(ReservedKeys.RECORD_SERVICE_HOST+" is empty");
        }
    }

    @Override
    public void setRecordType(List<String> types) throws QueryException {

        if (types.size() != 1 || !types.contains(ReservedValues.RECORD_VALUE_TYPE_SERVICE)) {
            throw new QueryException(ReservedKeys.RECORD_TYPE + " is restricted to \"" + ReservedValues.RECORD_VALUE_TYPE_SERVICE + "\" for ServiceQuery");
        }
    }
}
