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

        return (String) this.getValue(ReservedKeys.RECORD_SERVICE_NAME);
    }
    //TODO: should the reserved key for this be: interface-name?
    public void setInterfaceName(String interfaceName) throws RecordException {

        if(interfaceName !=null && !(interfaceName.isEmpty())){
            this.add(ReservedKeys.RECORD_SERVICE_NAME, interfaceName);
        }else{
            throw new RecordException(ReservedKeys.RECORD_SERVICE_NAME+" is empty");
        }

    }

    public List<InetAddress> getAddresses() throws RecordException {

        List<InetAddress> addresses = new LinkedList<InetAddress>();
        for (String a : (List<String>) this.getValue(ReservedKeys.RECORD_INTERFACE_ADDRESSES)) {
            a = a.substring(0, a.lastIndexOf("/"));
            try {
                addresses.add(InetAddress.getByName(a));
            } catch (UnknownHostException e) {

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

    public String getSubnet() throws RecordException {

        return (String)this.getValue(ReservedKeys.RECORD_INTERFACE_SUBNET);
    }

    public void setSubnet(InetAddress address, int mask) throws RecordException {

        if(address != null){
            String subnet = address.getHostAddress()+"/"+mask;
            this.add(ReservedKeys.RECORD_INTERFACE_SUBNET, subnet);
        }
    }

    public void setSubnet(String subnet) throws RecordException {

        if(subnet != null && !subnet.isEmpty()){
            Pattern subnetPattern = Pattern.compile("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\/\\d{1,3})");
            if(subnetPattern.matcher(subnet).find()){
                this.add(ReservedKeys.RECORD_INTERFACE_SUBNET, subnet);
            }else{
                throw new RecordException(ReservedKeys.RECORD_INTERFACE_SUBNET + " is not of the format: xxx.xxx.xxx.xxx/yy");
            }

        }else{
            throw new RecordException(ReservedKeys.RECORD_INTERFACE_SUBNET + "s empty");
        }
    }

    public void setCapacity(int capacity){
        this.add(ReservedKeys.RECORD_INTERFACE_CAPACITY, Integer.toString(capacity));
    }

    public int getCapacity(){
        return Integer.decode((String) this.getValue(ReservedKeys.RECORD_INTERFACE_CAPACITY));
    }

    public String getMacAddress(){
        return (String) this.getValue(ReservedKeys.RECORD_INTERFACE_MACADDRESS);
    }

    public void setMacAddress(String macAddress) throws RecordException {
        if(macAddress != null && !macAddress.isEmpty()){
            this.add(ReservedKeys.RECORD_INTERFACE_MACADDRESS, macAddress);
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

        return Integer.decode((String) this.getValue(ReservedKeys.RECORD_INTERFACE_MTU));
    }

    public void setMtu(int mtu) throws RecordException {

        if (mtu > 0) {
            this.add(ReservedKeys.RECORD_INTERFACE_MTU, Integer.toString(mtu));
        } else {
            throw new RecordException(ReservedKeys.RECORD_INTERFACE_MTU + " must be a positive integer.");
        }
    }
}
