package net.es.lookup.records.Directory;

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
public class PersonRecord extends Record {

    public PersonRecord(){
        super(ReservedValues.RECORD_VALUE_TYPE_PERSON);
    }


    public String getName() {

        return (String) this.getValue(ReservedKeys.RECORD_PERSON_NAME);

    }

    public void setName(String name) throws RecordException {

        if(name !=null && !name.isEmpty()){
            this.add(ReservedKeys.RECORD_PERSON_NAME, name);
        }else{
            throw new RecordException(ReservedKeys.RECORD_PERSON_NAME+" is empty");
        }
    }

    public List<String> getEmailAddresses() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_PERSON_EMAILIDS);

    }

    public void setEmailAddresses(List<String> emails) throws RecordException {

        if(emails !=null && !emails.isEmpty()){
            this.add(ReservedKeys.RECORD_PERSON_EMAILIDS, emails);
        }else{
            throw new RecordException(ReservedKeys.RECORD_PERSON_EMAILIDS+" is empty");
        }
    }

    public List<String> getPhoneNumbers() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_PERSON_PHONENUMBERS);

    }

    public void setPhoneNumbers(List<String> phoneNumbers) throws RecordException {

        if(phoneNumbers !=null && !phoneNumbers.isEmpty()){
            this.add(ReservedKeys.RECORD_PERSON_PHONENUMBERS, phoneNumbers);
        }else{
            throw new RecordException(ReservedKeys.RECORD_PERSON_PHONENUMBERS+" is empty");
        }
    }

    public String getOrganization() {

        return (String) this.getValue(ReservedKeys.RECORD_PERSON_ORGANIZATION);

    }

    public void setOrganization(String organization) throws RecordException {

        if(organization !=null && !organization.isEmpty()){
            this.add(ReservedKeys.RECORD_PERSON_ORGANIZATION, organization);
        }else{
            throw new RecordException(ReservedKeys.RECORD_PERSON_ORGANIZATION+" is empty");
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
}
