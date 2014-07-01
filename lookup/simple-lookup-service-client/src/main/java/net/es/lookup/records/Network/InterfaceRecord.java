package net.es.lookup.records.Network;

import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.RecordException;
import net.es.lookup.records.Record;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * User: sowmya
 * Date: 12/25/12
 * Time: 1:18 PM
 */
public class InterfaceRecord extends Record {
    public InterfaceRecord(){
        super(ReservedValues.RECORD_VALUE_TYPE_INTERFACE);
    }


    public String getInterfaceName() {
        try {
            return (((List<String>) this.getValue(ReservedKeys.RECORD_INTERFACE_NAME)).get(0));
        }
        catch (NullPointerException e) {
            return null;
        }
    }

    public void setInterfaceName(String interfaceName) throws RecordException {

        if(interfaceName !=null && !(interfaceName.isEmpty())){
            List<String> l = new LinkedList<String>();
            l.add(interfaceName);
            this.add(ReservedKeys.RECORD_INTERFACE_NAME, l);
        }else{
            throw new RecordException(ReservedKeys.RECORD_INTERFACE_NAME+" is empty");
        }

    }

    public List<InetAddress> getAddresses() throws RecordException {

        List<InetAddress> addresses = new LinkedList<InetAddress>();
        for (String a : (List<String>) this.getValue(ReservedKeys.RECORD_INTERFACE_ADDRESSES)) {
            // The string representation of an InetAddress can include a "/" followed
            // by an IPv4/IPv6 literal.  If we see this, strip it off.
            try {
                a = a.substring(0, a.lastIndexOf('/'));
            }
            catch (StringIndexOutOfBoundsException e) {
                // It's OK if there was no slash, it was just an IP literal
            }
            try {
                addresses.add(InetAddress.getByName(a));
            }
            catch (UnknownHostException e) {

                throw new RecordException(ReservedKeys.RECORD_INTERFACE_ADDRESSES + " could not be converted back to InetAddress");
            }
        }
        return addresses;
    }

    public void setAddresses(List<InetAddress> addresses) throws RecordException {

        List<String> addList = new LinkedList<String>();
        if(addresses != null || !(addresses.isEmpty())){
            for (InetAddress ia : addresses) {
                if (ia != null) {
                    addList.add(ia.toString());
                } else {
                    throw new RecordException(ReservedKeys.RECORD_INTERFACE_ADDRESSES + " contains null values");
                }
            }
            this.add(ReservedKeys.RECORD_INTERFACE_ADDRESSES, addList);
        }else{
            throw new RecordException(ReservedKeys.RECORD_INTERFACE_ADDRESSES + " is empty");
        }
    }

    public int getCapacity() {
        try {
            return Integer.decode(((List<String>) this.getValue(ReservedKeys.RECORD_INTERFACE_CAPACITY)).get(0));
        }
        catch (NullPointerException e) {
            return 0;
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setCapacity(int capacity){
        List<String> l = new LinkedList<String>();
        l.add(Integer.toString(capacity));
        this.add(ReservedKeys.RECORD_INTERFACE_CAPACITY, l);
    }

    public String getMacAddress(){
        try {
            return (((List<String>) this.getValue(ReservedKeys.RECORD_INTERFACE_MACADDRESS)).get(0));
        }
        catch (NullPointerException e) {
            return null;
        }
    }

    public void setMacAddress(String macAddress) throws RecordException {
        if(macAddress != null && !macAddress.isEmpty()){
            List<String> l = new LinkedList<String>();
            l.add(macAddress);
            this.add(ReservedKeys.RECORD_INTERFACE_MACADDRESS, l);
        }  else{
            throw new RecordException(ReservedKeys.RECORD_INTERFACE_MACADDRESS + " is empty");
        }
    }

    public List<String> getDomains() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_GROUP_DOMAINS);
    }

    public void setDomains(List<String> domains) throws RecordException {

        if(domains !=null && !(domains.isEmpty())){
            this.add(ReservedKeys.RECORD_GROUP_DOMAINS, domains);
        }else{
            throw new RecordException(ReservedKeys.RECORD_GROUP_DOMAINS+" is empty");
        }
    }

    public int getMtu() {
        try {
            return Integer.decode(((List<String>) this.getValue(ReservedKeys.RECORD_INTERFACE_MTU)).get(0));
        }
        catch (NullPointerException e) {
            return 0;
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setMtu(int mtu) throws RecordException {

        if (mtu > 0) {
            List<String> l = new LinkedList<String>();
            l.add(Integer.toString(mtu));
            this.add(ReservedKeys.RECORD_INTERFACE_MTU, l);
        } else {
            throw new RecordException(ReservedKeys.RECORD_INTERFACE_MTU + " must be a positive integer.");
        }
    }
}
