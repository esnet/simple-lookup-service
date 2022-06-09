package net.es.lookup.integration;

import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.ParserException;
import net.es.lookup.common.exception.QueryException;
import net.es.lookup.integration.BaseIT;
import net.es.lookup.queries.Directory.PersonQuery;
import net.es.lookup.queries.Network.HostQuery;
import net.es.lookup.queries.Network.InterfaceQuery;
import net.es.lookup.queries.Network.ServiceQuery;
import net.es.lookup.queries.Query;
import org.junit.Assert;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 * User: student5
 * Date: 6/17/13
 * Time: 4:55 PM
 * To change this template use File | Settings | File Templates.
 */

public class QueryIT extends BaseIT {

    @Test
    public void generateGenericQuery() {

        System.out.println("Testing generation of a generic query");

        Query query = new Query();

        LinkedList<String> expiresList = new LinkedList<String>();
        List<String> operatorList = new LinkedList<String>();
        expiresList.add((new Integer(0)).toString());
        operatorList.add(ReservedValues.RECORD_OPERATOR_ALL);

        try {

            System.out.println("Testing Query getters and setters");
            query.setExpires(expiresList); //This call must remain before the following setKeyOperator method call
            query.setRecordState(stringList); //This call must remain before the following setKeyOperator method call
            query.setURI(stringList);
            Assert.assertTrue(!query.getExpires().contains(null) && !query.getExpires().isEmpty());
            Assert.assertTrue(!query.getRecordState().contains(null) && !query.getRecordState().isEmpty());
            Assert.assertTrue(!query.getURI().contains(null) && !query.getURI().isEmpty());
            System.out.println("Methods executed successfully");

            System.out.println("Testing setKeyOperator method");
            try {

                query.setKeyOperator(ReservedKeys.RECORD_EXPIRES, operatorList);
                query.setKeyOperator(ReservedKeys.RECORD_STATE, operatorList);
                System.out.println("Method executed successfully");
                query.setRecordType(stringList);
                Assert.assertTrue(!query.getRecordType().contains(null) && !query.getRecordType().isEmpty());
            } catch (QueryException e) {

                System.out.println(e.getMessage());
                Assert.fail(e.getMessage());
            }

            System.out.println("Testing setKeyOperator method for a non-existing key (should fail)");
            query.add(ReservedKeys.RECORD_TTL + "-operator", operatorList);
            boolean exceptionCaught = false;
            try {

                query.toURL();
                System.out.println("Invalid operator key created");
            } catch (QueryException expected) {

                System.out.println("Expected exception successfully caught");
                exceptionCaught = true;
            }
            Assert.assertTrue(exceptionCaught);

            System.out.println("Testing operator key with multiple values (should fail)");
            operatorList.add(ReservedValues.RECORD_OPERATOR_ANY);
            query.add(ReservedKeys.RECORD_TTL + "-operator", operatorList);
            query.setTTL(expiresList);
            Assert.assertTrue(!query.getTTL().contains(null) && !query.getTTL().isEmpty());
            exceptionCaught = false;
            try {

                query.toURL();
                System.out.println("Invalid operator key created");
            } catch (QueryException expected) {

                System.out.println("Expected exception successfully caught");
                exceptionCaught = true;
            }
            Assert.assertTrue(exceptionCaught);

            System.out.println("Testing setKeyOperator method with multiple operator values (should fail)");
            exceptionCaught = false;
            try {

                query.setKeyOperator(ReservedKeys.RECORD_STATE, operatorList);
                System.out.println("Invalid operator key created");
            } catch (QueryException expected) {

                System.out.println("Expected exception successfully caught");
                exceptionCaught = true;
            }
            Assert.assertTrue(exceptionCaught);

            Assert.assertTrue(query.validate());
        } catch (QueryException e) {

            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }

    }

    @Test
    public void generateHostQuery() throws ParserException {

        System.out.println("Testing generation of a host query.");

        HostQuery query = new HostQuery();

        try {

            System.out.println("Testing location getters and setters");
            query.setCity(stringList);
            query.setSiteName(stringList);
            query.setZipcode(stringList);
            query.setState(stringList);
            query.setCountry(countryList);
            query.setLatitude(doubleList);
            query.setLongitude(doubleList);
            Assert.assertTrue(!query.getCity().contains(null) && !query.getCity().isEmpty());
            Assert.assertTrue(!query.getSiteName().contains(null) && !query.getSiteName().isEmpty());
            Assert.assertTrue(!query.getZipcode().contains(null) && !query.getZipcode().isEmpty());
            Assert.assertTrue(!query.getState().contains(null) && !query.getState().isEmpty());
            Assert.assertTrue(!query.getCountry().contains(null) && !query.getCountry().isEmpty());
            Assert.assertTrue(!query.getLatitude().contains(null) && !query.getLatitude().isEmpty());
            Assert.assertTrue(!query.getLongitude().contains(null) && !query.getLongitude().isEmpty());

            System.out.println("Testing OS getters and setters");
            query.setOSKernel(stringList);
            query.setOSName(stringList);
            query.setOSVersion(stringList);
            Assert.assertTrue(!query.getOSKernel().contains(null) && !query.getOSKernel().isEmpty());
            Assert.assertTrue(!query.getOSName().contains(null) && !query.getOSName().isEmpty());
            Assert.assertTrue(!query.getOSVersion().contains(null) && !query.getOSVersion().isEmpty());

            System.out.println("Testing hardware getters and setters");
            query.setHostProcessorCount(integerList);
            query.setHostMemory(longList);
            query.setHostProcessorCore(integerList);
            query.setHostProcessorSpeed(longList);
            Assert.assertTrue(!query.getHostProcessorCount().contains(null) && !query.getHostProcessorCount().isEmpty());
            Assert.assertTrue(!query.getHostMemory().contains(null) && !query.getHostMemory().isEmpty());
            Assert.assertTrue(!query.getHostProcessorCore().contains(null) && !query.getHostProcessorCore().isEmpty());
            Assert.assertTrue(!query.getHostProcessorSpeed().contains(null) && !query.getHostProcessorSpeed().isEmpty());

            System.out.println("Testing TCP getters and setters");
            query.setTcpMaxBackLog(integerList);
            query.setReceiveTcpAutotuneMaxBuffer(integerList);
            query.setReceiveTcpMaxBuffer(integerList);
            query.setSendTcpAutotuneMaxBuffer(integerList);
            query.setSendTcpMaxBuffer(integerList);
            query.setTcpCongestionAlgorithm(stringList);
            Assert.assertTrue(!query.getTcpMaxBackLog().contains(null) && !query.getTcpMaxBackLog().isEmpty());
            Assert.assertTrue(!query.getReceiveTcpAutotuneMaxBuffer().contains(null) && !query.getReceiveTcpAutotuneMaxBuffer().isEmpty());
            Assert.assertTrue(!query.getReceiveTcpMaxBuffer().contains(null) && !query.getReceiveTcpMaxBuffer().isEmpty());
            Assert.assertTrue(!query.getSendTcpAutotuneMaxBuffer().contains(null) && !query.getSendTcpAutotuneMaxBuffer().isEmpty());
            Assert.assertTrue(!query.getSendTcpMaxBuffer().contains(null) && !query.getSendTcpMaxBuffer().isEmpty());
            Assert.assertTrue(!query.getTcpCongestionAlgorithm().contains(null) && !query.getTcpCongestionAlgorithm().isEmpty());

            System.out.println("Testing host getters and setters");
            query.setHostName(stringList);
            query.setDomains(stringList);
            query.setAdministrators(stringList);
            query.setInterfaces(stringList);
            Assert.assertTrue(!query.getHostName().contains(null) && !query.getHostName().isEmpty());
            Assert.assertTrue(!query.getDomains().contains(null) && !query.getDomains().isEmpty());
            Assert.assertTrue(!query.getAdministrators().contains(null) && !query.getAdministrators().isEmpty());
            Assert.assertTrue(!query.getInterfaces().contains(null) && !query.getInterfaces().isEmpty());

            System.out.println("Testing setRecordType method for HostQuery (should fail)");
            try {

                query.setRecordType(stringList);
                Assert.fail("Bad type list accepted for HostQuery");
                System.out.println("Bad type list accepted for HostQuery");
            } catch (QueryException expected) {

                System.out.println("Expected exception successfully thrown");
            }

        } catch (QueryException e) {

            System.out.println(e.getMessage());
            Assert.fail();
        }
        Map<String, Object> expected = (HashMap)((HashMap) getConfiguration(configFile).get("queries")).get("host-query");
        Assert.assertTrue(query.getMap().entrySet().containsAll(expected.entrySet()));
        Assert.assertTrue(query.validate());
    }

    @Test
    public void generateInterfaceQuery() throws ParserException {

        System.out.println("Testing generation of an interface query.");

        InterfaceQuery query = new InterfaceQuery();
        LinkedList<String> subnetList = new LinkedList<String>();
        subnetList.add("000.000.000.000/00");
        List<InetAddress> addressList = new LinkedList<InetAddress>();
        InetAddress address = null;
        try {
            address = InetAddress.getByName("www.google.com");
            addressList.add(address);
        } catch (UnknownHostException ignored) { }

        try {

            System.out.println("Testing interface getters and setters");
            query.setInterfaceName(stringList);
            query.setMacAddress(stringList);
            query.setAddresses(addressList);
            query.setDomains(stringList);
            query.setCapacity(integerList);
            query.setMtu(integerList);
            Assert.assertTrue(!query.getInterfaceName().contains(null) && !query.getInterfaceName().isEmpty());
            Assert.assertTrue(!query.getMacAddress().contains(null) && !query.getMacAddress().isEmpty());
            Assert.assertTrue(!query.getAddresses().contains(null) && !query.getAddresses().isEmpty());
            Assert.assertTrue(!query.getDomains().contains(null) && !query.getDomains().isEmpty());
            Assert.assertTrue(!query.getCapacity().contains(null) && !query.getCapacity().isEmpty());
            Assert.assertTrue(!query.getMtu().contains(null) && !query.getMtu().isEmpty());

            System.out.println("Testing setRecordType method for InterfaceQuery (should fail)");
            try {

                query.setRecordType(stringList);
                Assert.fail("Bad type list accepted for InterfaceQuery");
                System.out.println("Bad type list accepted for InterfaceQuery");
            } catch (QueryException expected) {

                System.out.println("Expected QueryException thrown");
            }

        } catch (QueryException e) {

            System.out.println(e.getMessage());
            Assert.fail();
        }
        Map<String, Object> expected = (HashMap)((HashMap) getConfiguration(configFile).get("queries")).get("interface-query");
        System.out.println("This is expected: "+expected.toString());
        System.out.println("This is actual:"+ query.getMap().toString());
        Assert.assertTrue(query.getMap().entrySet().containsAll(expected.entrySet()));
        Assert.assertTrue(query.validate());
    }

    @Test
    public void generateServiceQuery() throws ParserException {

        System.out.println("Testing generation of a service query.");

        ServiceQuery query = new ServiceQuery();

        try {

            System.out.println("Testing service getters and setters");
            query.setHost(stringList);
            query.setServiceName(stringList);
            query.setServiceVersion(stringList);
            query.setServiceType(stringList);
            query.setAdministrators(stringList);
            query.setDomains(stringList);
            Assert.assertTrue(!query.getHost().contains(null) && !query.getHost().isEmpty());
            Assert.assertTrue(!query.getServiceName().contains(null) && !query.getServiceName().isEmpty());
            Assert.assertTrue(!query.getServiceVersion().contains(null) && !query.getServiceVersion().isEmpty());
            Assert.assertTrue(!query.getServiceType().contains(null) && !query.getServiceType().isEmpty());
            Assert.assertTrue(!query.getAdministrators().contains(null) && !query.getAdministrators().isEmpty());
            Assert.assertTrue(!query.getDomains().contains(null) && !query.getDomains().isEmpty());

            System.out.println("Testing location getters and setters");
            query.setCity(stringList);
            query.setSiteName(stringList);
            query.setZipcode(stringList);
            query.setState(stringList);
            query.setCountry(countryList);
            query.setServiceLocator(stringList);
            query.setLatitude(doubleList);
            query.setLongitude(doubleList);
            Assert.assertTrue(!query.getCity().contains(null) && !query.getCity().isEmpty());
            Assert.assertTrue(!query.getSiteName().contains(null) && !query.getSiteName().isEmpty());
            Assert.assertTrue(!query.getZipcode().contains(null) && !query.getZipcode().isEmpty());
            Assert.assertTrue(!query.getState().contains(null) && !query.getState().isEmpty());
            Assert.assertTrue(!query.getCountry().contains(null) && !query.getCountry().isEmpty());
            Assert.assertTrue(!query.getServiceLocator().contains(null) && !query.getServiceLocator().isEmpty());
            Assert.assertTrue(!query.getLatitude().contains(null) && !query.getLatitude().isEmpty());
            Assert.assertTrue(!query.getLongitude().contains(null) && !query.getLongitude().isEmpty());

            System.out.println("Testing setRecordType method for ServiceQuery (should fail)");
            try {

                query.setRecordType(stringList);
                Assert.fail("Bad type list accepted for ServiceQuery");
                System.out.println("Bad type list accepted for ServiceQuery");
            } catch (QueryException expected) {

                System.out.println("Expected exception successfully thrown");
            }

        } catch (QueryException e) {

            System.out.println(e.getMessage());
            Assert.fail();
        }
        Map<String, Object> expected = (HashMap)((HashMap) getConfiguration(configFile).get("queries")).get("service-query");
        Assert.assertTrue(query.getMap().entrySet().containsAll(expected.entrySet()));
        Assert.assertTrue(query.validate());
    }

    @Test
    public void generatePersonQuery() throws ParserException {

        System.out.println("Testing generation of a person query.");

        PersonQuery query = new PersonQuery();

        try {

            System.out.println("Testing person getters and setters");
            query.setPersonName(stringList);
            query.setPersonEmails(stringList);
            query.setPersonPhoneNumbers(phoneList);
            query.setPersonOrganizations(stringList);
            Assert.assertTrue(!query.getPersonName().contains(null) && !query.getPersonName().isEmpty());
            Assert.assertTrue(!query.getPersonEmails().contains(null) && !query.getPersonEmails().isEmpty());
            Assert.assertTrue(!query.getPersonPhoneNumbers().contains(null) && !query.getPersonPhoneNumbers().isEmpty());
            Assert.assertTrue(!query.getPersonOrganizations().contains(null) && !query.getPersonOrganizations().isEmpty());

            System.out.println("Testing location getters and setters");
            query.setDomains(stringList);
            query.setSiteName(stringList);
            query.setCity(stringList);
            query.setState(stringList);
            query.setCountry(countryList);
            query.setZipcode(stringList);
            query.setLatitude(doubleList);
            query.setLongitude(doubleList);
            Assert.assertTrue(!query.getDomains().contains(null) && !query.getDomains().isEmpty());
            Assert.assertTrue(!query.getSiteName().contains(null) && !query.getSiteName().isEmpty());
            Assert.assertTrue(!query.getCity().contains(null) && !query.getCity().isEmpty());
            Assert.assertTrue(!query.getState().contains(null) && !query.getState().isEmpty());
            Assert.assertTrue(!query.getCountry().contains(null) && !query.getCountry().isEmpty());
            Assert.assertTrue(!query.getZipcode().contains(null) && !query.getZipcode().isEmpty());
            Assert.assertTrue(!query.getLatitude().contains(null) && !query.getLatitude().isEmpty());
            Assert.assertTrue(!query.getLongitude().contains(null) && !query.getLongitude().isEmpty());

            System.out.println("Testing setRecordType method for PersonQuery (should fail)");
            try {

                query.setRecordType(stringList);
                Assert.fail("Bad type list accepted for PersonQuery");
                System.out.println("Bad type list accepted for PersonQuery");
            } catch (QueryException expected) {

                System.out.println("Expected exception successfully thrown");
            }

        } catch (QueryException e) {

            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }
        Map<String, Object> expected = (HashMap)((HashMap) getConfiguration(configFile).get("queries")).get("person-query");
        Assert.assertTrue(query.getMap().entrySet().containsAll(expected.entrySet()));
        Assert.assertTrue(query.validate());
    }

    @Test
    public void generateQueryWithBadLatLongValues() throws ParserException {

        System.out.println("Testing generation of query with bad latitude/longitude values (should fail)");

        HostQuery query = new HostQuery();
        List<Double> badLatitudeList = new LinkedList<Double>();
        badLatitudeList.add(90.01);
        List<Double> badLongitudeList = new LinkedList<Double>();
        badLongitudeList.add(180.01);

        try {

            query.setLatitude(badLatitudeList);
            System.out.println("Bad latitude was accepted");
            Assert.fail("Bad latitude was accepted");
        } catch (QueryException badLatitude) {

            try {

                query.setLongitude(badLongitudeList);
                System.out.println("Bad longitude was accepted");
                Assert.fail("Bad longitude was accepted");
            } catch (QueryException badLongitude) {

                System.out.println("Expected exceptions successfully thrown");
                return;
            }
        }
    }

    @Test
    public void generateQueryWithBadCountryValue() throws ParserException {

        System.out.println("Testing generation of query with bad country value (should fail)");

        HostQuery query = new HostQuery();

        try {
            query.setCountry(phoneList);
            System.out.println("Bad country code was accepted");

        } catch (QueryException e) {

            System.out.println("Expected exception successfully thrown");
            return;
        }
        Assert.fail("Bad country code was accepted");
    }
}

