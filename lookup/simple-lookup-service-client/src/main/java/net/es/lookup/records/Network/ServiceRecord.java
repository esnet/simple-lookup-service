package net.es.lookup.records.Network;


import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.RecordException;
import net.es.lookup.records.DataValidator;
import net.es.lookup.records.Record;

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

        return (String) this.getValue(ReservedKeys.RECORD_SERVICE_NAME);
    }

    public void setServiceName(String serviceName) throws RecordException {

        if(serviceName ==null || serviceName.isEmpty()){
            throw new RecordException(ReservedKeys.RECORD_SERVICE_NAME+" is empty");
        }else{
            this.add(ReservedKeys.RECORD_SERVICE_NAME, serviceName);
        }

    }

    public String getServiceVersion() {

        return (String) this.getValue(ReservedKeys.RECORD_SERVICE_VERSION);
    }

    public void setServiceVersion(String serviceVersion) throws RecordException {

        if(serviceVersion !=null && !serviceVersion.isEmpty()){
            this.add(ReservedKeys.RECORD_SERVICE_VERSION, serviceVersion);
        }else{
            throw new RecordException(ReservedKeys.RECORD_SERVICE_VERSION+" is empty");
        }
    }

    public String getServiceType() {

        return (String) this.getValue(ReservedKeys.RECORD_SERVICE_TYPE);
    }

    public void setServiceType(String serviceType) throws RecordException {

        if(serviceType !=null && !serviceType.isEmpty()){
            this.add(ReservedKeys.RECORD_SERVICE_TYPE, serviceType);
        }else{
            throw new RecordException(ReservedKeys.RECORD_SERVICE_TYPE+" is empty");
        }
    }

    public List<String> getServiceLocator() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_SERVICE_LOCATOR);
    }

    public void setServiceLocator(List<String> serviceLocator) throws RecordException {

        if(serviceLocator !=null && !serviceLocator.isEmpty()){
            this.add(ReservedKeys.RECORD_SERVICE_LOCATOR, serviceLocator);
        }else{
            throw new RecordException(ReservedKeys.RECORD_SERVICE_LOCATOR+" is empty");
        }
    }

    public List<String> getDomains() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_GROUP_DOMAINS);
    }

    public void setDomains(List<String> domains) throws RecordException {

        if(domains !=null && !domains.isEmpty()){
            this.add(ReservedKeys.RECORD_GROUP_DOMAINS, domains);
        }else{
            throw new RecordException(ReservedKeys.RECORD_GROUP_DOMAINS+" is empty");
        }
    }

    public List<String> getCommunities() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_GROUP_COMMUNITIES);
    }

    public void setCommunities(List<String> communities) throws RecordException {

        if (communities != null && !communities.isEmpty()) {
            this.add(ReservedKeys.RECORD_GROUP_COMMUNITIES, communities);
        } else {
            throw new RecordException(ReservedKeys.RECORD_GROUP_COMMUNITIES + " is empty");
        }
    }

    public String getSiteName() {

        return (String) this.getValue(ReservedKeys.RECORD_LOCATION_SITENAME);
    }

    public void setSiteName(String siteName) throws RecordException {

        if(siteName !=null && !siteName.isEmpty()){
            this.add(ReservedKeys.RECORD_LOCATION_SITENAME, siteName);
        }else{
            throw new RecordException(ReservedKeys.RECORD_LOCATION_SITENAME+" is empty");
        }
    }

    public String getCity() {

        return (String) this.getValue(ReservedKeys.RECORD_LOCATION_CITY);
    }

    public void setCity(String city) throws RecordException {

        if(city !=null && !city.isEmpty()){
            this.add(ReservedKeys.RECORD_LOCATION_CITY, city);
        }else{
            throw new RecordException(ReservedKeys.RECORD_LOCATION_CITY+" is empty");
        }
    }

    public String getState() {

        return (String) this.getValue(ReservedKeys.RECORD_LOCATION_STATE);
    }

    @Deprecated
    public String getRegion() { return getState(); }

    public void setState(String state) throws RecordException {

        if(state !=null && !state.isEmpty()){
            this.add(ReservedKeys.RECORD_LOCATION_STATE, state);
        }else{
            throw new RecordException(ReservedKeys.RECORD_LOCATION_STATE +" is empty");
        }
    }

    @Deprecated
    public void setRegion(String state) throws RecordException { setState(state); }

    public String getCountry() {

        return (String) this.getValue(ReservedKeys.RECORD_LOCATION_COUNTRY);
    }

    public void setCountry(String country) throws RecordException {

        if(country != null && !country.isEmpty() && DataValidator.isValidCountry(country)){
            this.add(ReservedKeys.RECORD_LOCATION_COUNTRY, country);
        }else{
            throw new RecordException(ReservedKeys.RECORD_LOCATION_COUNTRY+" is invalid");
        }
    }

    public String getZipcode() {

        return (String) this.getValue(ReservedKeys.RECORD_LOCATION_ZIPCODE);
    }

    public void setZipcode(String zipcode) throws RecordException {

        if(zipcode != null && !zipcode.isEmpty()){
            this.add(ReservedKeys.RECORD_LOCATION_ZIPCODE, zipcode);
        }else{
            throw new RecordException(ReservedKeys.RECORD_LOCATION_ZIPCODE+" is empty");
        }
    }

    public double getLatitude() {

        return Double.parseDouble((String) this.getValue(ReservedKeys.RECORD_LOCATION_LATITUDE));
    }

    public void setLatitude(double latitude) throws RecordException {

        if(DataValidator.isValidLatitude(latitude)){
            this.add(ReservedKeys.RECORD_LOCATION_LATITUDE, Double.toString(latitude));
        }else{
            throw new RecordException(ReservedKeys.RECORD_LOCATION_LATITUDE+" is out of range (-90,90)");
        }
    }

    public double getLongitude() {

        return Double.parseDouble((String) this.getValue(ReservedKeys.RECORD_LOCATION_LONGITUDE));
    }

    public void setLongitude(double longitude) throws RecordException {

        if(DataValidator.isValidLongitude(longitude)){
            this.add(ReservedKeys.RECORD_LOCATION_LONGITUDE, Double.toString(longitude));
        }else{
            throw new RecordException(ReservedKeys.RECORD_LOCATION_LONGITUDE+" is out of range (-180,180)");
        }
    }

    public List<String> getAdministrators() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_SERVICE_ADMINISTRATORS);
    }

    public void setAdministrators(List<String> administrators) throws RecordException {

        if(administrators != null && !administrators.isEmpty()){
            this.add(ReservedKeys.RECORD_SERVICE_ADMINISTRATORS, administrators);
        }else{
            throw new RecordException(ReservedKeys.RECORD_SERVICE_ADMINISTRATORS+" is empty");
        }
    }

    public String getHost() {

        return (String) this.getValue(ReservedKeys.RECORD_SERVICE_HOST);
    }

    public void setHost(String host) throws RecordException {

        if(host != null && !host.isEmpty()){
            this.add(ReservedKeys.RECORD_SERVICE_HOST, host);
        }else{
            throw new RecordException(ReservedKeys.RECORD_SERVICE_HOST+" is empty");
        }
    }
}
