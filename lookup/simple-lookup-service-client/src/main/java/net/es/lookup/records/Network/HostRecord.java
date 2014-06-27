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
 * Time: 1:17 PM
 */
public class HostRecord extends Record {

    public HostRecord() {

        super(ReservedValues.RECORD_VALUE_TYPE_HOST);
    }

    public List<String> getHostName() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_HOST_NAME);
    }

    public void setHostName(String hostName) throws RecordException {

        if (hostName == null || hostName.isEmpty()) {
            throw new RecordException(ReservedKeys.RECORD_HOST_NAME + " is empty");
        } else {
            this.add(ReservedKeys.RECORD_HOST_NAME, hostName);
        }

    }

    public long getHostMemory() {

        return Long.decode((String) this.getValue(ReservedKeys.RECORD_HOST_HARDWARE_MEMORY));
    }

    public void setHostMemory(long hostHardwareMemory) {

        this.add(ReservedKeys.RECORD_HOST_HARDWARE_MEMORY, Long.toString(hostHardwareMemory));
    }

    public long getHostProcessorSpeed() {

        return Long.decode((String) this.getValue(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORSPEED));
    }

    public void setHostProcessorSpeed(long processorSpeed) {

        this.add(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORSPEED, Long.toString(processorSpeed));
    }

    public int getHostProcessorCount() {

        return Integer.decode((String) this.getValue(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORCOUNT));
    }

    public void setHostProcessorCount(int processorCount) {

        this.add(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORCOUNT, Integer.toString(processorCount));
    }

    public int getHostProcessorCore() {

        return Integer.decode((String) this.getValue(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORCORE));
    }

    public void setHostProcessorCore(int processorCore) {

        this.add(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORCORE, Integer.toString(processorCore));
    }

    public List<String> getOSName() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_HOST_OS_NAME);
    }

    public void setOSName(List<String> osName) throws RecordException {

        if (osName == null || osName.isEmpty()) {
            throw new RecordException(ReservedKeys.RECORD_HOST_OS_NAME + " is empty");
        } else {
            this.add(ReservedKeys.RECORD_HOST_OS_NAME, osName);
        }
    }

    public List<String> getOSVersion() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_HOST_OS_VERSION);
    }

    public void setOSVersion(List<String> osVersion) throws RecordException {

        if (osVersion == null || osVersion.isEmpty()) {
            throw new RecordException(ReservedKeys.RECORD_HOST_OS_VERSION + " is empty");
        } else {
            this.add(ReservedKeys.RECORD_HOST_OS_VERSION, osVersion);
        }
    }

    public List<String> getOSKernel() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_HOST_OS_KERNEL);
    }

    public void setOSKernel(List<String> osKernel) throws RecordException {

        if (osKernel == null || osKernel.isEmpty()) {
            throw new RecordException(ReservedKeys.RECORD_HOST_OS_KERNEL + " is empty");
        } else {
            this.add(ReservedKeys.RECORD_HOST_OS_KERNEL, osKernel);
        }
    }

    public String getTcpCongestionAlgorithm() {

        return (String) this.getValue(ReservedKeys.RECORD_HOST_NET_TCP_CONGESTIONALGORITHM);
    }

    public void setTcpCongestionAlgorithm(String congestionAlgorithm) throws RecordException {

        if (congestionAlgorithm == null || congestionAlgorithm.isEmpty()) {
            throw new RecordException(ReservedKeys.RECORD_HOST_NET_TCP_CONGESTIONALGORITHM + " is empty");
        } else {
            this.add(ReservedKeys.RECORD_HOST_NET_TCP_CONGESTIONALGORITHM, congestionAlgorithm);
        }

    }

    public int getSendTcpMaxBuffer() {

        return Integer.decode((String) this.getValue(ReservedKeys.RECORD_HOST_NET_TCP_MAXBUFFER_SEND));
    }

    public void setSendTcpMaxBuffer(int maxbuf) {

        this.add(ReservedKeys.RECORD_HOST_NET_TCP_MAXBUFFER_SEND, Integer.toString(maxbuf));
    }

    public int getReceiveTcpMaxBuffer() {

        return Integer.decode((String) this.getValue(ReservedKeys.RECORD_HOST_NET_TCP_MAXBUFFER_RECV));
    }

    public void setReceiveTcpMaxBuffer(int maxbuf) {

        this.add(ReservedKeys.RECORD_HOST_NET_TCP_MAXBUFFER_RECV, Integer.toString(maxbuf));
    }

    public int getSendTcpAutotuneMaxBuffer() {

        return Integer.decode((String) this.getValue(ReservedKeys.RECORD_HOST_NET_TCP_AUTOTUNEMAXBUFFER_SEND));
    }

    public void setSendTcpAutotuneMaxBuffer(int autotune) {

        this.add(ReservedKeys.RECORD_HOST_NET_TCP_AUTOTUNEMAXBUFFER_SEND, Integer.toString(autotune));
    }

    public int getReceiveTcpAutotuneMaxBuffer() {

        return Integer.decode((String) this.getValue(ReservedKeys.RECORD_HOST_NET_TCP_AUTOTUNEMAXBUFFER_RECV));
    }

    public void setReceiveTcpAutotuneMaxBuffer(int autotune) {

        this.add(ReservedKeys.RECORD_HOST_NET_TCP_AUTOTUNEMAXBUFFER_RECV, Integer.toString(autotune));
    }


    public int getTcpMaxBackLog() {

        return Integer.decode((String) this.getValue(ReservedKeys.RECORD_HOST_NET_TCP_MAXBACKLOG));
    }

    public void setgetTcpMaxBackLog(int maxbacklog) {


        this.add(ReservedKeys.RECORD_HOST_NET_TCP_MAXBACKLOG, Integer.toString(maxbacklog));

    }

    public List<String> getInterfaces() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_HOST_NET_INTERFACES);
    }

    public void setInterfaces(List<String> interfaces) throws RecordException {

        if (interfaces != null && !interfaces.isEmpty()) {
            this.add(ReservedKeys.RECORD_HOST_NET_INTERFACES, interfaces);
        } else {
            throw new RecordException(ReservedKeys.RECORD_HOST_NET_INTERFACES + " is empty");
        }
    }

    public List<String> getDomains() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_GROUP_DOMAINS);
    }

    public void setDomains(List<String> domains) throws RecordException {

        if (domains != null && !domains.isEmpty()) {
            this.add(ReservedKeys.RECORD_GROUP_DOMAINS, domains);
        } else {
            throw new RecordException(ReservedKeys.RECORD_GROUP_DOMAINS + " is empty");
        }
    }

    public String getSiteName() {

        return (String) this.getValue(ReservedKeys.RECORD_LOCATION_SITENAME);
    }

    public void setSiteName(String siteName) throws RecordException {

        if (siteName != null && !siteName.isEmpty()) {
            this.add(ReservedKeys.RECORD_LOCATION_SITENAME, siteName);
        } else {
            throw new RecordException(ReservedKeys.RECORD_LOCATION_SITENAME + " is empty");
        }
    }

    public String getCity() {

        return (String) this.getValue(ReservedKeys.RECORD_LOCATION_CITY);
    }

    public void setCity(String city) throws RecordException {

        if (city != null && !city.isEmpty()) {
            this.add(ReservedKeys.RECORD_LOCATION_CITY, city);
        } else {
            throw new RecordException(ReservedKeys.RECORD_LOCATION_CITY + " is empty");
        }
    }

    public String getState() {

        return (String) this.getValue(ReservedKeys.RECORD_LOCATION_STATE);
    }

    @Deprecated
    public String getRegion() { return getState(); }

    public void setState(String state) throws RecordException {

        if (state != null && !state.isEmpty()) {
            this.add(ReservedKeys.RECORD_LOCATION_STATE, state);
        } else {
            throw new RecordException(ReservedKeys.RECORD_LOCATION_STATE + " is empty");
        }
    }

    @Deprecated
    public void setRegion(String state) throws RecordException { setState(state); }

    public String getCountry() {

        return (String) this.getValue(ReservedKeys.RECORD_LOCATION_COUNTRY);
    }

    public void setCountry(String country) throws RecordException {

        if (country != null && !country.isEmpty() && DataValidator.isValidCountry(country)) {
            this.add(ReservedKeys.RECORD_LOCATION_COUNTRY, country);
        } else {
            throw new RecordException(ReservedKeys.RECORD_LOCATION_COUNTRY + " is invalid");
        }
    }

    public String getZipcode() {

        return (String) this.getValue(ReservedKeys.RECORD_LOCATION_ZIPCODE);
    }

    public void setZipcode(String zipcode) throws RecordException {

        if (zipcode != null && !zipcode.isEmpty()) {
            this.add(ReservedKeys.RECORD_LOCATION_ZIPCODE, zipcode);
        } else {
            throw new RecordException(ReservedKeys.RECORD_LOCATION_ZIPCODE + " is empty");
        }
    }

    public double getLatitude() {

        return Double.parseDouble((String) this.getValue(ReservedKeys.RECORD_LOCATION_LATITUDE));
    }

    public void setLatitude(double latitude) throws RecordException {

        if (DataValidator.isValidLatitude(latitude)) {
            this.add(ReservedKeys.RECORD_LOCATION_LATITUDE, Double.toString(latitude));
        } else {
            throw new RecordException(ReservedKeys.RECORD_LOCATION_LATITUDE + " is out of range (-90,90)");
        }
    }

    public double getLongitude() {

        return Double.parseDouble((String) this.getValue(ReservedKeys.RECORD_LOCATION_LONGITUDE));
    }

    public void setLongitude(double longitude) throws RecordException {

        if (DataValidator.isValidLongitude(longitude)) {
            this.add(ReservedKeys.RECORD_LOCATION_LONGITUDE, Double.toString(longitude));
        } else {
            throw new RecordException(ReservedKeys.RECORD_LOCATION_LONGITUDE + " is out of range (-180,180)");
        }
    }

    public List<String> getAdministrators() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_SERVICE_ADMINISTRATORS);
    }

    public void setAdministrators(List<String> administrators) throws RecordException {

        if (administrators != null && !administrators.isEmpty()) {
            this.add(ReservedKeys.RECORD_SERVICE_ADMINISTRATORS, administrators);
        } else {
            throw new RecordException(ReservedKeys.RECORD_SERVICE_ADMINISTRATORS + " is empty");
        }
    }


}
