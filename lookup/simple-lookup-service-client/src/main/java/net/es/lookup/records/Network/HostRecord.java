package net.es.lookup.records.Network;


import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.RecordException;
import net.es.lookup.records.DataValidator;
import net.es.lookup.records.Record;

import java.util.LinkedList;
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

    /**
     * Get hostnames for a host.
     * Returns a list of hostnames because a host can register itself with multiple hostnames.
     * @return list of hostnames
     */
    public List<String> getHostName() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_HOST_NAME);
    }

    /**
     * Set hostnames for a host.
     * Takes a list of hostnames to cover the (very frequent) case that a host can have more
     * than one name.
     * @param hostName list of hostnames
     * @throws RecordException
     */
    public void setHostName(List<String> hostName) throws RecordException {
        if (hostName == null || hostName.isEmpty()) {
            throw new RecordException(ReservedKeys.RECORD_HOST_NAME + " is empty");
        } else {
            this.add(ReservedKeys.RECORD_HOST_NAME, hostName);
        }

    }

    /**
     * Return the amount of hardware memory in MB
     * @return the amount of memory in MB, 0 if an error or not available
     */
    public double getHostMemory() {
        try {
            // This grabs the first word of the hardware memory, which is usually of the form
            // "7869.51171875 MB".
            String s = ((List<String>) this.getValue(ReservedKeys.RECORD_HOST_HARDWARE_MEMORY)).get(0);
            return Double.parseDouble(s.split(" ")[0]);
        }
        catch (NullPointerException e) {
            return 0;
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setHostMemory(double hostHardwareMemory) {
        List<String> l = new LinkedList<String>();
        l.add(String.format("%s MB", Double.toString(hostHardwareMemory)));
        this.add(ReservedKeys.RECORD_HOST_HARDWARE_MEMORY, l);
    }

    /**
     * Return the processor speed in MHz
     * @return processor speed in MHz, 0 if an error or not available
     */
    public double getHostProcessorSpeed() {
        try {
            // Break apart a string of the form "2500.002 MHz" and return the numeric part.
            String s = ((List<String>) this.getValue(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORSPEED)).get(0);
            return Double.parseDouble(s.split(" ")[0]);
        }
        catch (NullPointerException e) {
            return 0;
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setHostProcessorSpeed(double processorSpeed) {
        List<String> l = new LinkedList<String>();
        l.add(String.format("%s MHz", Double.toString(processorSpeed)));
        this.add(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORSPEED, l);
    }

    /**
     *
     * @return number of processors (0 if not present or malformed)
     */
    public int getHostProcessorCount() {
        try {
            return Integer.decode(((List<String>) this.getValue(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORCOUNT)).get(0));
        }
        catch (NullPointerException e) {
            return 0;
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setHostProcessorCount(int processorCount) {
        List<String> l = new LinkedList<String>();
        l.add(Integer.toString(processorCount));
        this.add(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORCOUNT, l);
    }

    /**
     *
     * @return number of processor cores (0 if an error or malformed)
     */
    public int getHostProcessorCore() {
        try {
            return Integer.decode(((List<String>) this.getValue(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORCORE)).get(0));
        }
        catch (NullPointerException e) {
            return 0;
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setHostProcessorCore(int processorCore) {
        List<String> l = new LinkedList<String>();
        l.add(Integer.toString(processorCore));
        this.add(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORCORE, l);
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
        try {
            String s = ((List<String>) this.getValue(ReservedKeys.RECORD_HOST_NET_TCP_CONGESTIONALGORITHM)).get(0);
            return s;
        }
        catch (NullPointerException e) {
            return null;
        }
    }

    public void setTcpCongestionAlgorithm(String congestionAlgorithm) throws RecordException {
        if (congestionAlgorithm == null || congestionAlgorithm.isEmpty()) {
            throw new RecordException(ReservedKeys.RECORD_HOST_NET_TCP_CONGESTIONALGORITHM + " is empty");
        } else {
            List<String> l = new LinkedList<String>();
            l.add(congestionAlgorithm);
            this.add(ReservedKeys.RECORD_HOST_NET_TCP_CONGESTIONALGORITHM, l);
        }

    }

    /**
     *
     * @return TCP maximum send buffer size in bytes, 0 if an error
     */
    public int getSendTcpMaxBuffer() {
        try {
            String s = ((List<String>) this.getValue(ReservedKeys.RECORD_HOST_NET_TCP_MAXBUFFER_SEND)).get(0);
            return Integer.decode(s.split(" ")[0]);
        }
        catch (NullPointerException e) {
            return 0;
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setSendTcpMaxBuffer(int maxbuf) {
        List<String> l = new LinkedList<String>();
        l.add(String.format("%s bytes", Integer.toString(maxbuf)));
        this.add(ReservedKeys.RECORD_HOST_NET_TCP_MAXBUFFER_SEND, l);
    }

    /**
     *
     * @return TCP maximum receive buffer size in bytes, 0 if an error
     */
    public int getReceiveTcpMaxBuffer() {
        try {
            String s = ((List<String>) this.getValue(ReservedKeys.RECORD_HOST_NET_TCP_MAXBUFFER_RECV)).get(0);
            return Integer.decode(s.split(" ")[0]);
        }
        catch (NullPointerException e) {
            return 0;
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setReceiveTcpMaxBuffer(int maxbuf) {
        List<String> l = new LinkedList<String>();
        l.add(String.format("%s bytes", Integer.toString(maxbuf)));
        this.add(ReservedKeys.RECORD_HOST_NET_TCP_MAXBUFFER_RECV, l);
    }

    /**
     *
     * @return TCP maximum autotune send buffer size in bytes, 0 if an error
     */
    public int getSendTcpAutotuneMaxBuffer() {
        try {
            String s = ((List<String>) this.getValue(ReservedKeys.RECORD_HOST_NET_TCP_AUTOTUNEMAXBUFFER_SEND)).get(0);
            return Integer.decode(s.split(" ")[0]);
        }
        catch (NullPointerException e) {
            return 0;
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setSendTcpAutotuneMaxBuffer(int autotune) {
        List<String> l = new LinkedList<String>();
        l.add(String.format("%s bytes", Integer.toString(autotune)));
        this.add(ReservedKeys.RECORD_HOST_NET_TCP_AUTOTUNEMAXBUFFER_SEND, l);
    }

    /**
     *
     * @return TCP maximum autotune receive buffer size in bytes, 0 if an error
     */
    public int getReceiveTcpAutotuneMaxBuffer() {
        try {
            String s = ((List<String>) this.getValue(ReservedKeys.RECORD_HOST_NET_TCP_AUTOTUNEMAXBUFFER_RECV)).get(0);
            return Integer.decode(s.split(" ")[0]);
        }
        catch (NullPointerException e) {
            return 0;
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setReceiveTcpAutotuneMaxBuffer(int autotune) {
        List<String> l = new LinkedList<String>();
        l.add(String.format("%s bytes", Integer.toString(autotune)));
        this.add(ReservedKeys.RECORD_HOST_NET_TCP_AUTOTUNEMAXBUFFER_RECV, l);
    }


    public int getTcpMaxBackLog() {
        try {
            return Integer.decode(((List<String>) this.getValue(ReservedKeys.RECORD_HOST_NET_TCP_MAXBACKLOG)).get(0));
        }
        catch (NullPointerException e) {
            return 0;
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setgetTcpMaxBackLog(int maxbacklog) {
        List<String> l = new LinkedList<String>();
        l.add(Integer.toString(maxbacklog));
        this.add(ReservedKeys.RECORD_HOST_NET_TCP_MAXBACKLOG, l);
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
        try {
            return ((List<String>) this.getValue(ReservedKeys.RECORD_LOCATION_SITENAME)).get(0);
        }
        catch (Exception e) {
            return null;
        }
    }

    public void setSiteName(String siteName) throws RecordException {
        if (siteName != null && !siteName.isEmpty()) {
            List<String> l = new LinkedList<String>();
            l.add(siteName);
            this.add(ReservedKeys.RECORD_LOCATION_SITENAME, l);
        } else {
            throw new RecordException(ReservedKeys.RECORD_LOCATION_SITENAME + " is empty");
        }
    }

    public String getCity() {
        try {
            return ((List<String>) this.getValue(ReservedKeys.RECORD_LOCATION_CITY)).get(0);
        }
        catch (Exception e) {
            return null;
        }
    }

    public void setCity(String city) throws RecordException {

        if (city != null && !city.isEmpty()) {
            List<String> l = new LinkedList<String>();
            l.add(city);
            this.add(ReservedKeys.RECORD_LOCATION_CITY, l);
        } else {
            throw new RecordException(ReservedKeys.RECORD_LOCATION_CITY + " is empty");
        }
    }

    public String getState() {
        try {
            return ((List<String>) this.getValue(ReservedKeys.RECORD_LOCATION_STATE)).get(0);
        }
        catch (Exception e) {
            return null;
        }
    }

    @Deprecated
    public String getRegion() { return getState(); }

    public void setState(String state) throws RecordException {

        if (state != null && !state.isEmpty()) {
            List<String> l = new LinkedList<String>();
            l.add(state);
            this.add(ReservedKeys.RECORD_LOCATION_STATE, l);
        } else {
            throw new RecordException(ReservedKeys.RECORD_LOCATION_STATE + " is empty");
        }
    }

    @Deprecated
    public void setRegion(String state) throws RecordException { setState(state); }

    public String getCountry() {
        try {
            return ((List<String>) this.getValue(ReservedKeys.RECORD_LOCATION_COUNTRY)).get(0);
        }
        catch (Exception e) {
            return null;
        }
    }

    public void setCountry(String country) throws RecordException {

        if (country != null && !country.isEmpty() && DataValidator.isValidCountry(country)) {
            List<String> l = new LinkedList<String>();
            l.add(country);
            this.add(ReservedKeys.RECORD_LOCATION_COUNTRY, l);
        } else {
            throw new RecordException(ReservedKeys.RECORD_LOCATION_COUNTRY + " is invalid");
        }
    }

    public String getZipcode() {
        try {
            return ((List<String>) this.getValue(ReservedKeys.RECORD_LOCATION_ZIPCODE)).get(0);
        }
        catch (Exception e) {
            return null;
        }
    }

    public void setZipcode(String zipcode) throws RecordException {

        if (zipcode != null && !zipcode.isEmpty()) {
            List<String> l = new LinkedList<String>();
            l.add(zipcode);
            this.add(ReservedKeys.RECORD_LOCATION_ZIPCODE, l);
        } else {
            throw new RecordException(ReservedKeys.RECORD_LOCATION_ZIPCODE + " is empty");
        }
    }

    /**
     *
     * @return latitude, NaN if not available
     */
    public double getLatitude() {
        try {
            return Double.parseDouble(((List<String>) this.getValue(ReservedKeys.RECORD_LOCATION_LATITUDE)).get(0));
        }
        catch (NullPointerException e) {
            return Double.NaN;
        }
    }

    public void setLatitude(double latitude) throws RecordException {

        if (DataValidator.isValidLatitude(latitude)) {
            List<String> l = new LinkedList<String>();
            l.add(Double.toString(latitude));
            this.add(ReservedKeys.RECORD_LOCATION_LATITUDE, l);
        } else {
            throw new RecordException(ReservedKeys.RECORD_LOCATION_LATITUDE + " is out of range (-90,90)");
        }
    }

    /**
     *
     * @return longitude, NaN if not available
     */
    public double getLongitude() {
        try {
            return Double.parseDouble(((List<String>) this.getValue(ReservedKeys.RECORD_LOCATION_LONGITUDE)).get(0));
        }
        catch (NullPointerException e) {
            return Double.NaN;
        }
    }

    public void setLongitude(double longitude) throws RecordException {

        if (DataValidator.isValidLongitude(longitude)) {
            List<String> l = new LinkedList<String>();
            l.add(Double.toString(longitude));
            this.add(ReservedKeys.RECORD_LOCATION_LONGITUDE, l);
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
