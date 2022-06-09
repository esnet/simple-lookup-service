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
            String s = getStringFromListValue(ReservedKeys.RECORD_HOST_HARDWARE_MEMORY);
            return Double.parseDouble(s.split(" ")[0]);
        }
        catch (NumberFormatException e) {
            return 0.0;
        }
        catch (NullPointerException e) {
            return 0.0;
        }
    }

    public void setHostMemory(double hostHardwareMemory) throws RecordException {
        addStringAsListValue(ReservedKeys.RECORD_HOST_HARDWARE_MEMORY, String.format("%s MB", Double.toString(hostHardwareMemory)));
    }

    /**
     * Return the processor speed in MHz
     * @return processor speed in MHz, 0 if an error or not available
     */
    public double getHostProcessorSpeed() {
        try {
            // Break apart a string of the form "2500.002 MHz" and return the numeric part.
            String s = (getStringFromListValue(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORSPEED));
            return Double.parseDouble(s.split(" ")[0]);
        }
        catch (NumberFormatException e) {
            return 0.0;
        }
        catch (NullPointerException e) {
            return 0.0;
        }
    }

    public void setHostProcessorSpeed(double processorSpeed) throws RecordException {
        addStringAsListValue(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORSPEED, String.format("%s MHz", Double.toString(processorSpeed)));
    }

    /**
     *
     * @return number of processors (0 if not present or malformed)
     */
    public int getHostProcessorCount() {
        try {
            return Integer.decode(getStringFromListValue(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORCOUNT));
        }
        catch (NumberFormatException e) {
            return 0;
        }
        catch (NullPointerException e) {
            return 0;
        }
    }

    public void setHostProcessorCount(int processorCount) throws RecordException {
        addStringAsListValue(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORCOUNT, Integer.toString(processorCount));
    }

    /**
     *
     * @return number of processor cores (0 if an error or malformed)
     */
    public int getHostProcessorCore() {
        try {
            return Integer.decode(getStringFromListValue(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORCORE));
        }
        catch (NumberFormatException e) {
            return 0;
        }
        catch (NullPointerException e) {
            return 0;
        }
    }

    public void setHostProcessorCore(int processorCore) throws RecordException{
        addStringAsListValue(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORCORE, Integer.toString(processorCore));
    }

    public String getOSName() {
        return getStringFromListValue(ReservedKeys.RECORD_HOST_OS_NAME);
    }

    public void setOSName(String osName) throws RecordException {
        addStringAsListValue(ReservedKeys.RECORD_HOST_OS_NAME, osName);
    }

    public String getOSVersion() {
        return getStringFromListValue(ReservedKeys.RECORD_HOST_OS_VERSION);
    }

    public void setOSVersion(String osVersion) throws RecordException {
        addStringAsListValue(ReservedKeys.RECORD_HOST_OS_VERSION, osVersion);
    }

    public String getOSKernel() {
        return getStringFromListValue(ReservedKeys.RECORD_HOST_OS_KERNEL);
    }

    public void setOSKernel(String osKernel) throws RecordException {
        addStringAsListValue(ReservedKeys.RECORD_HOST_OS_KERNEL, osKernel);
    }

    public String getTcpCongestionAlgorithm() {
        return getStringFromListValue(ReservedKeys.RECORD_HOST_NET_TCP_CONGESTIONALGORITHM);
    }

    public void setTcpCongestionAlgorithm(String congestionAlgorithm) throws RecordException {
        addStringAsListValue(ReservedKeys.RECORD_HOST_NET_TCP_CONGESTIONALGORITHM, congestionAlgorithm);
    }

    /**
     *
     * @return TCP maximum send buffer size in bytes, 0 if an error
     */
    public int getSendTcpMaxBuffer() {
        try {
            String s = getStringFromListValue(ReservedKeys.RECORD_HOST_NET_TCP_MAXBUFFER_SEND);
            return Integer.decode(s.split(" ")[0]);
        }
        catch (NumberFormatException e) {
            return 0;
        }
        catch (NullPointerException e) {
            return 0;
        }
    }

    public void setSendTcpMaxBuffer(int maxbuf) throws RecordException {
        addStringAsListValue(ReservedKeys.RECORD_HOST_NET_TCP_MAXBUFFER_SEND, String.format("%s bytes", Integer.toString(maxbuf)));
    }

    /**
     *
     * @return TCP maximum receive buffer size in bytes, 0 if an error
     */
    public int getReceiveTcpMaxBuffer() {
        try {
            String s = (getStringFromListValue(ReservedKeys.RECORD_HOST_NET_TCP_MAXBUFFER_RECV));
            return Integer.decode(s.split(" ")[0]);
        }
        catch (NumberFormatException e) {
            return 0;
        }
        catch (NullPointerException e) {
            return 0;
        }
    }

    public void setReceiveTcpMaxBuffer(int maxbuf) throws RecordException {
        addStringAsListValue(ReservedKeys.RECORD_HOST_NET_TCP_MAXBUFFER_RECV, String.format("%s bytes", Integer.toString(maxbuf)));
    }

    /**
     *
     * @return TCP maximum autotune send buffer size in bytes, 0 if an error
     */
    public int getSendTcpAutotuneMaxBuffer() {
        try {
            String s = getStringFromListValue(ReservedKeys.RECORD_HOST_NET_TCP_AUTOTUNEMAXBUFFER_SEND);
            return Integer.decode(s.split(" ")[0]);
        }
        catch (NumberFormatException e) {
            return 0;
        }
        catch (NullPointerException e) {
            return 0;
        }
    }

    public void setSendTcpAutotuneMaxBuffer(int autotune) throws RecordException{
        addStringAsListValue(ReservedKeys.RECORD_HOST_NET_TCP_AUTOTUNEMAXBUFFER_SEND, String.format("%s bytes", Integer.toString(autotune)));
    }

    /**
     *
     * @return TCP maximum autotune receive buffer size in bytes, 0 if an error
     */
    public int getReceiveTcpAutotuneMaxBuffer() {
        try {
            String s = getStringFromListValue(ReservedKeys.RECORD_HOST_NET_TCP_AUTOTUNEMAXBUFFER_RECV);
            return Integer.decode(s.split(" ")[0]);
        }
        catch (NumberFormatException e) {
            return 0;
        }
        catch (NullPointerException e) {
            return 0;
        }
    }

    public void setReceiveTcpAutotuneMaxBuffer(int autotune) throws RecordException {
        addStringAsListValue(ReservedKeys.RECORD_HOST_NET_TCP_AUTOTUNEMAXBUFFER_RECV, String.format("%s bytes", Integer.toString(autotune)));
    }


    public int getTcpMaxBackLog() {
        try {
            return Integer.decode(getStringFromListValue(ReservedKeys.RECORD_HOST_NET_TCP_MAXBACKLOG));
        }
        catch (NumberFormatException e) {
            return 0;
        }
        catch (NullPointerException e) {
            return 0;
        }
    }

    public void setgetTcpMaxBackLog(int maxbacklog) throws RecordException {
        addStringAsListValue(ReservedKeys.RECORD_HOST_NET_TCP_MAXBACKLOG, Integer.toString(maxbacklog));
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

    /**
     *
     * @return latitude, NaN if not available
     */
    public double getLatitude() {
        try {
            return Double.parseDouble(getStringFromListValue(ReservedKeys.RECORD_LOCATION_LATITUDE));
        }
        catch (NumberFormatException e) {
            return Double.NaN;
        }
        catch (NullPointerException e) {
            return Double.NaN;
        }
    }

    public void setLatitude(double latitude) throws RecordException {
        if (DataValidator.isValidLatitude(latitude)) {
            addStringAsListValue(ReservedKeys.RECORD_LOCATION_LATITUDE, Double.toString(latitude));
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
            return Double.parseDouble(getStringFromListValue(ReservedKeys.RECORD_LOCATION_LONGITUDE));
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
        catch (NullPointerException e) {
            return Double.NaN;
        }
    }

    public void setLongitude(double longitude) throws RecordException {
        if (DataValidator.isValidLongitude(longitude)) {
            addStringAsListValue(ReservedKeys.RECORD_LOCATION_LONGITUDE, Double.toString(longitude));
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
