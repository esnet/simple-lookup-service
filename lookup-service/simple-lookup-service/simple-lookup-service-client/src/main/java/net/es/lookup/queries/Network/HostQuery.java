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
 * Time: 8:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class HostQuery extends Query {

    public HostQuery() {

        super();
        List<String> tmp = new LinkedList<String>();
        tmp.add(ReservedValues.RECORD_VALUE_TYPE_HOST);

        try {

            this.add(ReservedKeys.RECORD_TYPE, tmp);
        } catch (QueryException ignored) { }
    }

    public List<String> getHostName() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_HOST_NAME);
    }

    public void setHostName(List<String> hostName) throws QueryException {

        if (hostName == null || hostName.isEmpty()) {
            throw new QueryException(ReservedKeys.RECORD_HOST_NAME + " is empty");
        } else {
            this.add(ReservedKeys.RECORD_HOST_NAME, hostName);
        }
    }

    public List<Long> getHostMemory() {

        List<Long> mems = new LinkedList<Long>();
        for (String m : (List<String>) this.getValue(ReservedKeys.RECORD_HOST_HARDWARE_MEMORY)) {
            mems.add(Long.decode(m));
        }

        return mems;
    }

    public void setHostMemory(List<Long> hostHardwareMemory) throws QueryException {

        if (hostHardwareMemory != null && !hostHardwareMemory.isEmpty()) {

            List<String> hostHardwareMemories = new LinkedList<String>();
            for (Long hhm : hostHardwareMemory) {
                hostHardwareMemories.add(hhm.toString());
            }
            this.add(ReservedKeys.RECORD_HOST_HARDWARE_MEMORY, hostHardwareMemories);
        } else {
            throw new QueryException(ReservedKeys.RECORD_HOST_HARDWARE_MEMORY + " is empty");
        }
    }

    public List<Long> getHostProcessorSpeed() {

        List<Long> speeds = new LinkedList<Long>();
        for (String hps : (List<String>) this.getValue(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORSPEED)) {
            speeds.add(Long.decode(hps));
        }

        return speeds;
    }

    public void setHostProcessorSpeed(List<Long> processorSpeed) throws QueryException {

        if (processorSpeed != null && !processorSpeed.isEmpty()) {

            List<String> processorSpeeds = new LinkedList<String>();
            for (Long ps : processorSpeed) {
                processorSpeeds.add(ps.toString());
            }
            this.add(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORSPEED, processorSpeeds);
        } else {
            throw new QueryException(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORSPEED + " is empty");
        }
    }

    public List<Integer> getHostProcessorCount() {

        List<Integer> hpcs = new LinkedList<Integer>();
        for (String pc : (List<String>) this.getValue(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORCOUNT)) {
            hpcs.add(Integer.decode(pc));
        }
        return hpcs;
    }

    public void setHostProcessorCount(List<Integer> processorCount) throws QueryException {

        if (processorCount != null && !processorCount.isEmpty()) {

            List<String> processorCounts = new LinkedList<String>();
            for (Integer pc : processorCount) {
                processorCounts.add(pc.toString());
            }
            this.add(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORCOUNT, processorCounts);
        } else {
            throw new QueryException(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORCOUNT + " is empty");
        }
    }

    public List<Integer> getHostProcessorCore() {

        List<Integer> hpcs = new LinkedList<Integer>();
        for (String pc : (List<String>) this.getValue(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORCORE)) {
            hpcs.add(Integer.decode(pc));
        }
        return hpcs;
    }

    public void setHostProcessorCore(List<Integer> processorCore) throws QueryException {

        if (processorCore != null && !processorCore.isEmpty()) {

            List<String> processorCores = new LinkedList<String>();
            for (Integer pc : processorCore) {
                processorCores.add(pc.toString());
            }
            this.add(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORCORE, processorCores);
        } else {
            throw new QueryException(ReservedKeys.RECORD_HOST_HARDWARE_PROCESSORCORE + " is empty");
        }
    }

    public List<String> getOSName() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_HOST_OS_NAME);
    }

    public void setOSName(List<String> osName) throws QueryException {

        if (osName == null || osName.isEmpty()) {
            throw new QueryException(ReservedKeys.RECORD_HOST_OS_NAME + " is empty");
        } else {
            this.add(ReservedKeys.RECORD_HOST_OS_NAME, osName);
        }
    }

    public List<String> getOSVersion() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_HOST_OS_VERSION);
    }

    public void setOSVersion(List<String> osVersion) throws QueryException {

        if (osVersion == null || osVersion.isEmpty()) {
            throw new QueryException(ReservedKeys.RECORD_HOST_OS_VERSION + " is empty");
        } else {
            this.add(ReservedKeys.RECORD_HOST_OS_VERSION, osVersion);
        }
    }

    public List<String> getOSKernel() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_HOST_OS_KERNEL);
    }

    public void setOSKernel(List<String> osKernel) throws QueryException {

        if (osKernel == null || osKernel.isEmpty()) {
            throw new QueryException(ReservedKeys.RECORD_HOST_OS_KERNEL + " is empty");
        } else {
            this.add(ReservedKeys.RECORD_HOST_OS_KERNEL, osKernel);
        }
    }

    public List<String> getTcpCongestionAlgorithm() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_HOST_NET_TCP_CONGESTIONALGORITHM);
    }

    public void setTcpCongestionAlgorithm(List<String> congestionAlgorithm) throws QueryException {

        if (congestionAlgorithm == null || congestionAlgorithm.isEmpty()) {
            throw new QueryException(ReservedKeys.RECORD_HOST_NET_TCP_CONGESTIONALGORITHM + " is empty");
        } else {
            this.add(ReservedKeys.RECORD_HOST_NET_TCP_CONGESTIONALGORITHM, congestionAlgorithm);
        }

    }

    public List<Integer> getSendTcpMaxBuffer() {

        List<Integer> tmbs = new LinkedList<Integer>();
        for (String mb : (List<String>) this.getValue(ReservedKeys.RECORD_HOST_NET_TCP_MAXBUFFER_SEND)) {
            tmbs.add(Integer.decode(mb));
        }
        return tmbs;
    }

    public void setSendTcpMaxBuffer(List<Integer> maxbuf) throws QueryException {

        if (maxbuf != null && !maxbuf.isEmpty()) {

            List<String> maxbufs = new LinkedList<String>();
            for (Integer mb : maxbuf) {
                maxbufs.add(mb.toString());
            }
            this.add(ReservedKeys.RECORD_HOST_NET_TCP_MAXBUFFER_SEND, maxbufs);
        } else {
            throw new QueryException(ReservedKeys.RECORD_HOST_NET_TCP_MAXBUFFER_SEND + " is empty");
        }
    }

    public List<Integer> getReceiveTcpMaxBuffer() {

        List<Integer> tmbs = new LinkedList<Integer>();
        for (String mb : (List<String>) this.getValue(ReservedKeys.RECORD_HOST_NET_TCP_MAXBUFFER_RECV)) {
            tmbs.add(Integer.decode(mb));
        }
        return tmbs;
    }

    public void setReceiveTcpMaxBuffer(List<Integer> maxbuf) throws QueryException {

        if (maxbuf != null && !maxbuf.isEmpty()) {

            List<String> maxbufs = new LinkedList<String>();
            for (Integer mb : maxbuf) {
                maxbufs.add(mb.toString());
            }
            this.add(ReservedKeys.RECORD_HOST_NET_TCP_MAXBUFFER_RECV, maxbufs);
        } else {
            throw new QueryException(ReservedKeys.RECORD_HOST_NET_TCP_MAXBUFFER_RECV + " is empty");
        }
    }

    public List<Integer> getSendTcpAutotuneMaxBuffer() {

        List<Integer> ambs = new LinkedList<Integer>();
        for (String mb : (List<String>) this.getValue(ReservedKeys.RECORD_HOST_NET_TCP_AUTOTUNEMAXBUFFER_SEND)) {
            ambs.add(Integer.decode(mb));
        }
        return ambs;
    }

    public void setSendTcpAutotuneMaxBuffer(List<Integer> autotune) throws QueryException {

        if (autotune != null && !autotune.isEmpty()) {

            List<String> autotunes = new LinkedList<String>();
            for (Integer at : autotune) {
                autotunes.add(at.toString());
            }
            this.add(ReservedKeys.RECORD_HOST_NET_TCP_AUTOTUNEMAXBUFFER_SEND, autotunes);
        } else {
            throw new QueryException(ReservedKeys.RECORD_HOST_NET_TCP_AUTOTUNEMAXBUFFER_SEND + " is empty");
        }
    }

    public List<Integer> getReceiveTcpAutotuneMaxBuffer() {

        List<Integer> ambs = new LinkedList<Integer>();
        for (String mb : (List<String>) this.getValue(ReservedKeys.RECORD_HOST_NET_TCP_AUTOTUNEMAXBUFFER_SEND)) {
            ambs.add(Integer.decode(mb));
        }
        return ambs;
    }

    public void setReceiveTcpAutotuneMaxBuffer(List<Integer> autotune) throws QueryException {

        if (autotune != null && !autotune.isEmpty()) {

            List<String> autotunes = new LinkedList<String>();
            for (Integer at : autotune) {
                autotunes.add(at.toString());
            }
            this.add(ReservedKeys.RECORD_HOST_NET_TCP_AUTOTUNEMAXBUFFER_RECV, autotunes);
        } else {
            throw new QueryException(ReservedKeys.RECORD_HOST_NET_TCP_AUTOTUNEMAXBUFFER_RECV + " is empty");
        }
    }

    public List<Integer> getTcpMaxBackLog() {

        List<Integer> mbls = new LinkedList<Integer>();
        for (String mbl : (List<String>) this.getValue(ReservedKeys.RECORD_HOST_NET_TCP_MAXBACKLOG)) {
            mbls.add(Integer.decode(mbl));
        }
        return mbls;
    }

    public void setTcpMaxBackLog(List<Integer> maxbacklog) throws QueryException {

        if (maxbacklog != null && !maxbacklog.isEmpty()) {

            List<String> maxbacklogs = new LinkedList<String>();
            for (Integer mbl : maxbacklog) {
                maxbacklogs.add(mbl.toString());
            }
            this.add(ReservedKeys.RECORD_HOST_NET_TCP_MAXBACKLOG, maxbacklogs);
        } else {
            throw new QueryException(ReservedKeys.RECORD_HOST_NET_TCP_MAXBACKLOG + " is empty");
        }
    }

    public List<String> getInterfaces() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_HOST_NET_INTERFACES);
    }

    public void setInterfaces(List<String> interfaces) throws QueryException {

        if (interfaces != null && !interfaces.isEmpty()) {
            this.add(ReservedKeys.RECORD_HOST_NET_INTERFACES, interfaces);
        } else {
            throw new QueryException(ReservedKeys.RECORD_HOST_NET_INTERFACES + " is empty");
        }
    }

    public List<String> getDomains() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_GROUP_DOMAINS);
    }

    public void setDomains(List<String> domains) throws QueryException {

        if (domains != null && !domains.isEmpty()) {
            this.add(ReservedKeys.RECORD_GROUP_DOMAINS, domains);
        } else {
            throw new QueryException(ReservedKeys.RECORD_GROUP_DOMAINS + " is empty");
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

        if (siteName != null && !siteName.isEmpty()) {
            this.add(ReservedKeys.RECORD_LOCATION_SITENAME, siteName);
        } else {
            throw new QueryException(ReservedKeys.RECORD_LOCATION_SITENAME + " is empty");
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
            throw new QueryException(ReservedKeys.RECORD_SERVICE_ADMINISTRATORS + " is empty");
        }
    }

    @Override
    public void setRecordType(List<String> types) throws QueryException {

        if (types.size() != 1 || !types.contains(ReservedValues.RECORD_VALUE_TYPE_HOST)) {
            throw new QueryException(ReservedKeys.RECORD_TYPE + " is restricted to \"" + ReservedValues.RECORD_VALUE_TYPE_HOST + "\" for HostQuery");
        }
    }
}
