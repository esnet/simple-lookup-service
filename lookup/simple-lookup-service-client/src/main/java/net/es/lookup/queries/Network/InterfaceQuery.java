package net.es.lookup.queries.Network;

import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.QueryException;
import net.es.lookup.queries.Query;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: luke
 * Date: 6/16/13
 * Time: 4:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class InterfaceQuery extends Query {

    public InterfaceQuery() {

        super();
        List<String> tmp = new LinkedList<String>();
        tmp.add(ReservedValues.RECORD_VALUE_TYPE_INTERFACE);

        try {

            this.add(ReservedKeys.RECORD_TYPE, tmp);
        } catch (QueryException ignored) { }
    }

    public List<String> getInterfaceName() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_SERVICE_NAME);
    }

    public void setInterfaceName(List<String> interfaceName) throws QueryException {

        if (interfaceName != null && !interfaceName.isEmpty()) {
            this.add(ReservedKeys.RECORD_SERVICE_NAME, interfaceName);
        } else {
            throw new QueryException(ReservedKeys.RECORD_SERVICE_NAME + " is empty");
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

    public List<InetAddress> getAddresses() throws QueryException {

        List<InetAddress> addresses = new LinkedList<InetAddress>();
        for (String a : (List<String>) this.getValue(ReservedKeys.RECORD_INTERFACE_ADDRESSES)) {
            a = a.substring(0, a.lastIndexOf("/"));
            try {
                addresses.add(InetAddress.getByName(a));
            } catch (UnknownHostException e) {

                throw new QueryException(ReservedKeys.RECORD_INTERFACE_ADDRESSES + " could not be converted back to InetAddress");
            }
        }
        return addresses;
    }

    public void setAddresses(List<InetAddress> addresses) throws QueryException {

        List<String> addressList = new LinkedList<String>();
        if (addresses != null && !addresses.isEmpty()) {
            for (InetAddress a : addresses) {
                addressList.add(a.toString());
            }
            this.add(ReservedKeys.RECORD_INTERFACE_ADDRESSES, addressList);
        } else {
            throw new QueryException(ReservedKeys.RECORD_INTERFACE_ADDRESSES + " is empty");
        }
    }

    public List<InetAddress> getSubnet() throws QueryException {

        List<InetAddress> addresses = new LinkedList<InetAddress>();
        for (String a : (List<String>) this.getValue(ReservedKeys.RECORD_INTERFACE_ADDRESSES)) {
            a = a.substring(0, a.lastIndexOf("/"));
            try {
                addresses.add(InetAddress.getByName(a));
            } catch (UnknownHostException e) {

                throw new QueryException(ReservedKeys.RECORD_INTERFACE_ADDRESSES + " could not be converted back to InetAddress");
            }
        }
        return addresses;
    }

    public void setSubnet(InetAddress address, List<Integer> mask) throws QueryException {

        if (address != null && mask != null && !mask.isEmpty()) {
            List<String> subnets = new LinkedList<String>();
            for (int m : mask) {
                subnets.add(address.getHostAddress() + "/" + m);
            }
            this.add(ReservedKeys.RECORD_INTERFACE_SUBNET, subnets);
        } else {
            throw new QueryException(ReservedKeys.RECORD_INTERFACE_SUBNET + " is empty");
        }
    }

    public void setSubnet(List<String> subnet) throws QueryException {

        if (subnet != null && !subnet.isEmpty()) {
            Pattern subnetPattern = Pattern.compile("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\/\\d{1,3})");
            for (String s : subnet) {
                if (subnetPattern.matcher(s).find()) {
                } else {
                    throw new QueryException(ReservedKeys.RECORD_INTERFACE_SUBNET + " is not of the format: xxx.xxx.xxx.xxx/yy");
                }
            }
            this.add(ReservedKeys.RECORD_INTERFACE_SUBNET, subnet);
        } else {
            throw new QueryException(ReservedKeys.RECORD_INTERFACE_SUBNET + "s empty");
        }
    }

    public List<Integer> getCapacity() {

        List<Integer> capacities = new LinkedList<Integer>();
        for (String capacity : (List<String>) this.getValue(ReservedKeys.RECORD_INTERFACE_CAPACITY)) {
            capacities.add(Integer.decode(capacity));
        }
        return capacities;
    }

    public void setCapacity(List<Integer> capacity) throws QueryException {

        List<String> capacities = new LinkedList<String>();
        if (capacity != null && !capacity.isEmpty()) {
            for (Integer c : capacity) {
                capacities.add(c.toString());
            }
            this.add(ReservedKeys.RECORD_INTERFACE_CAPACITY, capacities);
        } else {
            throw new QueryException(ReservedKeys.RECORD_INTERFACE_CAPACITY + " is empty");
        }
    }

    public List<String> getMacAddress() {

        return (List<String>) this.getValue(ReservedKeys.RECORD_INTERFACE_MACADDRESS);
    }

    public void setMacAddress(List<String> macAddress) throws QueryException {

        if (macAddress != null && !macAddress.isEmpty()) {
            this.add(ReservedKeys.RECORD_INTERFACE_MACADDRESS, macAddress);
        } else {
            throw new QueryException(ReservedKeys.RECORD_INTERFACE_MACADDRESS + " is empty");
        }
    }

    public List<Integer> getMtu() {

        List<Integer> mtus = new LinkedList<Integer>();
        for (String mtu : (List<String>) this.getValue(ReservedKeys.RECORD_INTERFACE_MTU)) {
            mtus.add(Integer.decode(mtu));
        }
        return mtus;
    }

    public void setMtu(List<Integer> mtu) throws QueryException {

        List<String> mtus = new LinkedList<String>();
        if (mtu != null && !mtu.isEmpty()) {
            for (Integer m : mtu) {
                if (m > 0) {
                    mtus.add(m.toString());
                } else {
                    throw new QueryException(ReservedKeys.RECORD_INTERFACE_MTU + " must be a positive integer.");
                }
            }
            this.add(ReservedKeys.RECORD_INTERFACE_MTU, mtus);
        } else {
            throw new QueryException(ReservedKeys.RECORD_INTERFACE_MTU + " is empty");
        }
    }

    @Override
    public void setRecordType(List<String> types) throws QueryException {

        if (types.size() != 1 || !types.contains(ReservedValues.RECORD_VALUE_TYPE_INTERFACE)) {
            throw new QueryException(ReservedKeys.RECORD_TYPE + " is restricted to \"" + ReservedValues.RECORD_VALUE_TYPE_INTERFACE + "\" for InterfaceQuery");
        }
    }
}
