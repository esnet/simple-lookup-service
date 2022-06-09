package net.es.lookup.queries.Directory;

import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.QueryException;
import net.es.lookup.queries.Query;
import net.es.lookup.records.DataValidator;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: luke
 * Date: 6/17/13
 * Time: 8:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class PersonQuery extends Query {

    public PersonQuery() {

        super();
        List<String> tmp = new LinkedList<String>();
        tmp.add(ReservedValues.RECORD_VALUE_TYPE_PERSON);

        try {

            this.add(ReservedKeys.RECORD_TYPE, tmp);
        } catch (QueryException ignored) { }
    }

    public List<String> getPersonName() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_PERSON_NAME);
    }

    public void setPersonName(List<String> PersonName) throws QueryException {

        if (PersonName == null || PersonName.isEmpty()) {
            throw new QueryException(ReservedKeys.RECORD_PERSON_NAME + " is empty");
        } else {
            this.add(ReservedKeys.RECORD_PERSON_NAME, PersonName);
        }
    }

    public List<String> getPersonEmails() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_PERSON_EMAILIDS);
    }

    public void setPersonEmails(List<String> emails) throws QueryException {

        if (emails == null || emails.isEmpty()) {
            throw new QueryException(ReservedKeys.RECORD_PERSON_EMAILIDS + " is empty");
        } else {
            this.add(ReservedKeys.RECORD_PERSON_EMAILIDS, emails);
        }
    }

    public List<String> getPersonPhoneNumbers() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_PERSON_PHONENUMBERS);
    }

    public void setPersonPhoneNumbers(List<String> phoneNumbers) throws QueryException {

        if (phoneNumbers != null) {
            Pattern phoneNumberPattern = Pattern.compile("([\\d]+)");
            for (String pn : phoneNumbers) {
                if (phoneNumberPattern.matcher(pn).find()) {
                } else {
                    throw new QueryException(ReservedKeys.RECORD_PERSON_PHONENUMBERS + " contains non-digit characters");
                }
            }
            this.add(ReservedKeys.RECORD_PERSON_PHONENUMBERS, phoneNumbers);
        } else {
            throw new QueryException(ReservedKeys.RECORD_PERSON_PHONENUMBERS + "s empty");
        }
    }

    public List<String> getPersonOrganizations() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_PERSON_ORGANIZATION);
    }

    public void setPersonOrganizations(List<String> org) throws QueryException {

        if (org == null || org.isEmpty()) {
            throw new QueryException(ReservedKeys.RECORD_PERSON_ORGANIZATION + " is empty");
        } else {
            this.add(ReservedKeys.RECORD_PERSON_ORGANIZATION, org);
        }
    }

    public List<String> getDomains() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_GROUP_DOMAINS);
    }

    public void setDomains(List<String> domains) throws QueryException {

        if (domains == null || domains.isEmpty()) {
            throw new QueryException(ReservedKeys.RECORD_GROUP_DOMAINS + " is empty");
        } else {
            this.add(ReservedKeys.RECORD_GROUP_DOMAINS, domains);
        }
    }

    public List<String> getSiteName() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_LOCATION_SITENAME);
    }

    public void setSiteName(List<String> siteName) throws QueryException {

        if (siteName == null || siteName.isEmpty()) {
            throw new QueryException(ReservedKeys.RECORD_LOCATION_SITENAME + " is empty");
        } else {
            this.add(ReservedKeys.RECORD_LOCATION_SITENAME, siteName);
        }
    }

    public List<String> getCity() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_LOCATION_CITY);
    }

    public void setCity(List<String> city) throws QueryException {

        if (city != null && !city.isEmpty()) {
            this.add(ReservedKeys.RECORD_LOCATION_CITY, city);
        } else {
            throw new QueryException(ReservedKeys.RECORD_LOCATION_CITY + " is empty");
        }
    }

    public List<String> getState() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_LOCATION_STATE);
    }

    @Deprecated
    public List<String> getRegion() { return getState(); }

    public void setState(List<String> state) throws QueryException {

        if (state != null && !state.isEmpty()) {
            this.add(ReservedKeys.RECORD_LOCATION_STATE, state);
        } else {
            throw new QueryException(ReservedKeys.RECORD_LOCATION_STATE + " is empty");
        }
    }

    @Deprecated
    public void setRegion(List<String> state) throws QueryException { setState(state); }

    public List<String> getCountry() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_LOCATION_COUNTRY);
    }

    public void setCountry(List<String> country) throws QueryException {

        if (country != null) {
            for (String c : country) {
                if (country != null && !country.isEmpty() && DataValidator.isValidCountry(c)) {
                    this.add(ReservedKeys.RECORD_LOCATION_COUNTRY, country);
                } else {
                    throw new QueryException(ReservedKeys.RECORD_LOCATION_COUNTRY+" is invalid");
                }
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
            throw new QueryException(ReservedKeys.RECORD_LOCATION_ZIPCODE + " is empty");
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
                    throw new QueryException(ReservedKeys.RECORD_LOCATION_LONGITUDE+" is out of range (-180,180)");
                }
            }
            this.add(ReservedKeys.RECORD_LOCATION_LONGITUDE, longitudes);
        }
    }

    @Override
    public void setRecordType(List<String> types) throws QueryException {

        if (types.size() != 1 || !types.contains(ReservedValues.RECORD_VALUE_TYPE_PERSON)) {
            throw new QueryException(ReservedKeys.RECORD_TYPE + " is restricted to \"" + ReservedValues.RECORD_VALUE_TYPE_PERSON + "\" for PersonQuery");
        }
    }
}
