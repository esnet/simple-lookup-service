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

    private long hostHardwareMemory;
    private long hostHardwareProcessorSpeed;
    private int hostHardwareProcessorCount;
    private int hostHardwareProcessorCore;

    public HostRecord() {

        super(ReservedValues.RECORD_VALUE_TYPE_HOST);
    }

    public String getHostName() {

        return (String) this.getValue(ReservedKeys.RECORD_HOST_NAME);
    }

    public void setHostName(String hostName) throws RecordException {

        if (hostName == null || hostName.isEmpty()) {
            this.add(ReservedKeys.RECORD_HOST_NAME, hostName);
        } else {
            throw new RecordException(ReservedKeys.RECORD_HOST_NAME + " is empty");
        }

    }

    public long getHostMemory() {

        return (Long) this.getValue(ReservedKeys.RECORD_HOST_HARDWARE_MEMORY);
    }

    public void setHostMemory(long hostHardwareMemory) {

        this.add(ReservedKeys.RECORD_HOST_HARDWARE_MEMORY,hostHardwareMemory);
    }

    public long getHostProcessorSpeed() {

        return (Long) this.getValue(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORSPEED);
    }

    public void setHostProcessorSpeed(long processorSpeed) {

        this.add(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORSPEED,processorSpeed);
    }

    public int getHostProcessorCount() {

        return (Integer) this.getValue(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORCOUNT);
    }

    public void setHosProcessorCount(int processorCount) {

        this.add(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORCOUNT,processorCount);
    }

    public int getHostProcessorCore() {

        return (Integer) this.getValue(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORCORE);
    }

    public void setHostProcessorCore(int processorCore) {

        this.add(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORCORE,processorCore);
    }

    public List<String> getOSName() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_HOST_OS_NAME);
    }

    public void setOSName(List<String> osName) throws RecordException {

        if (osName == null || osName.isEmpty()) {
            this.add(ReservedKeys.RECORD_HOST_OS_NAME, osName);
        } else {
            throw new RecordException(ReservedKeys.RECORD_HOST_OS_NAME + " is empty");
        }
    }

    public List<String> getOSVersion() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_HOST_OS_VERSION);
    }

    public void setOSVersion(List<String> osVersion) throws RecordException {

        if (osVersion == null || osVersion.isEmpty()) {
            this.add(ReservedKeys.RECORD_HOST_OS_VERSION, osVersion);
        } else {
            throw new RecordException(ReservedKeys.RECORD_HOST_OS_VERSION + " is empty");
        }
    }

    public List<String> getOSKernel() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_HOST_OS_KERNEL);
    }

    public void setOSKernel(List<String> osKernel) throws RecordException {

        if (osKernel == null || osKernel.isEmpty()) {
            this.add(ReservedKeys.RECORD_HOST_OS_KERNEL, osKernel);
        } else {
            throw new RecordException(ReservedKeys.RECORD_HOST_OS_KERNEL + " is empty");
        }
    }

    public String getTcpCongestionAlgorithm() {

        return (String) this.getValue(ReservedKeys.RECORD_HOST_NET_TCP_CONGESTIONALGORITHM);
    }

    public void setTcpCongestionAlgorithm(String congestionAlgorithm) throws RecordException {

        if (congestionAlgorithm == null || congestionAlgorithm.isEmpty()) {
            this.add(ReservedKeys.RECORD_HOST_NET_TCP_CONGESTIONALGORITHM, congestionAlgorithm);
        } else {
            throw new RecordException(ReservedKeys.RECORD_HOST_NET_TCP_CONGESTIONALGORITHM + " is empty");
        }

    }

    public int getSendTcpMaxBuffer() {

        return (Integer) this.getValue(ReservedKeys.RECORD_HOST_NET_TCP_MAXBUFFER_SEND);
    }

    public void setSendTcpMaxBuffer(int maxbuf) {

        this.add(ReservedKeys.RECORD_HOST_NET_TCP_MAXBUFFER_SEND, maxbuf);
    }

    public int getReceiveTcpMaxBuffer() {

        return (Integer) this.getValue(ReservedKeys.RECORD_HOST_NET_TCP_MAXBUFFER_RECV);
    }

    public void setReceiveTcpMaxBuffer(int maxbuf) {

        this.add(ReservedKeys.RECORD_HOST_NET_TCP_MAXBUFFER_RECV, maxbuf);
    }

    public int getSendTcpAutotuneMaxBuffer() {

        return (Integer) this.getValue(ReservedKeys.RECORD_HOST_NET_TCP_AUTOTUNEMAXBUFFER_SEND);
    }

    public void setSendTcpAutotuneMaxBuffer(int autotune) {

        this.add(ReservedKeys.RECORD_HOST_NET_TCP_AUTOTUNEMAXBUFFER_SEND, autotune);
    }

    public int getReceiveTcpAutotuneMaxBuffer() {

        return (Integer) this.getValue(ReservedKeys.RECORD_HOST_NET_TCP_AUTOTUNEMAXBUFFER_RECV);
    }

    public void setReceiveTcpAutotuneMaxBuffer(int autotune) {

        this.add(ReservedKeys.RECORD_HOST_NET_TCP_AUTOTUNEMAXBUFFER_RECV, autotune);
    }


    public int getTcpMaxBackLog() {

        return (Integer) this.getValue(ReservedKeys.RECORD_HOST_NET_TCP_MAXBACKLOG);
    }

    public void setgetTcpMaxBackLog(int maxbacklog) {


        this.add(ReservedKeys.RECORD_HOST_NET_TCP_MAXBACKLOG, maxbacklog);

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

    public String getRegion() {

        return (String) this.getValue(ReservedKeys.RECORD_LOCATION_REGION);
    }

    public void setRegion(String region) throws RecordException {

        if (region != null && !region.isEmpty()) {
            this.add(ReservedKeys.RECORD_LOCATION_REGION, region);
        } else {
            throw new RecordException(ReservedKeys.RECORD_LOCATION_REGION + " is empty");
        }
    }

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

        return (Double) this.getValue(ReservedKeys.RECORD_LOCATION_LATITUDE);
    }

    public void setLatitude(double latitude) throws RecordException {

        if (DataValidator.isValidLatitude(latitude)) {
            this.add(ReservedKeys.RECORD_LOCATION_LATITUDE, latitude);
        } else {
            throw new RecordException(ReservedKeys.RECORD_LOCATION_LATITUDE + " is out of range (-90,90)");
        }
    }

    public double getLongitude() {

        return (Double) this.getValue(ReservedKeys.RECORD_LOCATION_LONGITUDE);
    }

    public void setLongitude(double longitude) throws RecordException {

        if (DataValidator.isValidLongitude(longitude)) {
            this.add(ReservedKeys.RECORD_LOCATION_LONGITUDE, longitude);
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
