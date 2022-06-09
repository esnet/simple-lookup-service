package net.es.lookup.integration;

import net.es.lookup.common.exception.ParserException;
import net.es.lookup.common.exception.RecordException;
import net.es.lookup.integration.BaseIT;
import net.es.lookup.records.Directory.PersonRecord;
import net.es.lookup.records.Network.HostRecord;
import net.es.lookup.records.Network.InterfaceRecord;
import net.es.lookup.records.Network.ServiceRecord;
import net.es.lookup.records.Record;
import org.joda.time.DateTime;
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
 * Time: 3:38 PM
 * To change this template use File | Settings | File Templates.
 */

public class RecordIT extends BaseIT {

    @Test
    public void generateGenericRecord() throws ParserException {

        System.out.println("Testing generation of a generic record");

        Record rec = new Record(str);

        System.out.println("Testing Record getters and setters");

        rec.setExpires(new DateTime());
        rec.setRecordState(str);
        rec.setTTL(anInt);
        Assert.assertTrue(rec.getExpires() != null);
        Assert.assertTrue(rec.getRecordState() != null && !rec.getRecordState().isEmpty());
        Assert.assertTrue(rec.getRecordType() != null && rec.getRecordType().equals(str));
        Assert.assertTrue(rec.getTTL() >= 0);
        System.out.println("Methods executed successfully");

        Map<String, Object> expected = (HashMap)((HashMap) getConfiguration(configFile).get("records")).get("generic-record");
        Assert.assertTrue("Value(s) do not match expected values from test config file", rec.getMap().entrySet().containsAll(expected.entrySet()));
        Assert.assertTrue("Record does not validate", rec.validate());
    }

    @Test
    public void generateHostRecord() throws ParserException {

        System.out.println("Testing generation of a host record");

        HostRecord rec = new HostRecord();

        try {

            System.out.println("Testing location getters and setters");
            rec.setCity(str);
            rec.setSiteName(str);
            rec.setZipcode(str);
            rec.setState(str);
            rec.setCountry(country);
            rec.setLatitude(anInt);
            rec.setLongitude(anInt);
            Assert.assertTrue(rec.getCity() != null && !rec.getCity().isEmpty());
            Assert.assertTrue(rec.getSiteName() != null && !rec.getSiteName().isEmpty());
            Assert.assertTrue(rec.getZipcode() != null && !rec.getZipcode().isEmpty());
            Assert.assertTrue(rec.getState() != null && !rec.getState().isEmpty());
            Assert.assertTrue(rec.getCountry() != null && !rec.getCountry().isEmpty());
            Assert.assertTrue(rec.getLatitude() >= Double.parseDouble((String) testValues.get("latitude-lower-bound")) && rec.getLatitude() <= Double.parseDouble((String) testValues.get("latitude-upper-bound")));
            Assert.assertTrue(rec.getLongitude() >= Double.parseDouble((String) testValues.get("longitude-lower-bound")) && rec.getLongitude() <= Double.parseDouble((String) testValues.get("longitude-upper-bound")));

            System.out.println("Testing OS getters and setters");
            rec.setOSKernel(str);
            rec.setOSName(str);
            rec.setOSVersion(str);
            System.out.println("OS getter: "+rec.getOSKernel());
            Assert.assertTrue(rec.getOSKernel() != null && !rec.getOSKernel().isEmpty());
            Assert.assertTrue(rec.getOSName() != null  && !rec.getOSName().isEmpty());
            Assert.assertTrue(rec.getOSVersion() != null  && !rec.getOSVersion().isEmpty());

            System.out.println("Testing hardware getters and setters");
            rec.setHostProcessorCount(anInt);
            rec.setHostMemory(anInt);
            rec.setHostProcessorCore(anInt);
            rec.setHostProcessorSpeed(anInt);
            Assert.assertTrue(rec.getHostProcessorCount() == anInt);
            Assert.assertTrue(rec.getHostMemory() == anInt);
            Assert.assertTrue(rec.getHostProcessorCore() == anInt);
            Assert.assertTrue(rec.getHostProcessorSpeed() == anInt);

            System.out.println("Testing TCP getters and setters");
            rec.setgetTcpMaxBackLog(anInt);
            rec.setReceiveTcpAutotuneMaxBuffer(anInt);
            rec.setReceiveTcpMaxBuffer(anInt);
            rec.setSendTcpAutotuneMaxBuffer(anInt);
            rec.setSendTcpMaxBuffer(anInt);
            rec.setTcpCongestionAlgorithm(str);
            Assert.assertTrue(rec.getTcpMaxBackLog() == anInt);
            Assert.assertTrue(rec.getReceiveTcpAutotuneMaxBuffer() == anInt);
            Assert.assertTrue(rec.getReceiveTcpMaxBuffer() == anInt);
            Assert.assertTrue(rec.getSendTcpAutotuneMaxBuffer() == anInt);
            Assert.assertTrue(rec.getSendTcpMaxBuffer() == anInt);
            Assert.assertTrue(rec.getTcpCongestionAlgorithm() != null && !rec.getTcpCongestionAlgorithm().isEmpty());

            System.out.println("Testing host getters and setters");
            rec.setDomains(stringList);
            rec.setCommunities(stringList);
            rec.setAdministrators(stringList);
            rec.setInterfaces(stringList);
            rec.setHostName(stringList);
            Assert.assertTrue(rec.getDomains() != null && !rec.getDomains().contains(null) && !rec.getDomains().isEmpty());
            Assert.assertTrue(rec.getCommunities() != null && !rec.getCommunities().contains(null) && !rec.getCommunities().isEmpty());
            Assert.assertTrue(rec.getAdministrators() != null && !rec.getAdministrators().contains(null) && !rec.getAdministrators().isEmpty());
            Assert.assertTrue(rec.getInterfaces() != null && !rec.getInterfaces().contains(null) && !rec.getInterfaces().isEmpty());
            Assert.assertTrue(rec.getHostName() != null && !rec.getHostName().contains(null) && !rec.getHostName().isEmpty());

            System.out.println("Testing setters with bad country and latitude/longitude values (should fail)");

            rec.setCountry("simple");

            try {
                rec.setLatitude(lowLatitude);
                Assert.fail("Bad latitude value accepted for host record");
            } catch (RecordException expected) { }

            try {
                rec.setLatitude(highLatitude);
                Assert.fail("Bad latitude value accepted for host record");
            } catch (RecordException expected) { }

            try {
                rec.setLongitude(lowLongitude);
                Assert.fail("Bad longitude value accepted for host record");
            } catch (RecordException expected) { }

            try {
                rec.setLongitude(highLongitude);
                Assert.fail("Bad longitude value accepted for host record");
            } catch (RecordException expected) { }
            System.out.println("Expected exceptions successfully thrown");

        } catch (RecordException e) {

            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }
        Map<String, Object> expected = (HashMap)((HashMap) getConfiguration(configFile).get("records")).get("host-record");
        System.out.println("This is the actual value: "+rec.getMap().toString());
      System.out.println("This is the expected value: "+expected.toString());
        Assert.assertTrue("Value(s) do not match expected values from test config file", !rec.getMap().entrySet().containsAll(expected.entrySet()));
        Assert.assertTrue("Record validates", rec.validate());
    }

    @Test
    public void generateInterfaceRecord() throws ParserException {

        System.out.println("Testing generation of an interface record");

        InterfaceRecord rec = new InterfaceRecord();
        List<InetAddress> addressList = new LinkedList<InetAddress>();
        InetAddress address = null;
        try {
            address = InetAddress.getByName("www.google.com");
            addressList.add(address);
        } catch (UnknownHostException ignored) { }

        System.out.println("Testing interface getters and setters");
        try {
            rec.setInterfaceName(str);
            rec.setMacAddress(str);
            if (address != null) {
                rec.setAddresses(addressList);
            }
            rec.setDomains(stringList);
            rec.setCapacity(anInt);
            rec.setMtu(anInt);
            Assert.assertTrue(rec.getInterfaceName() != null && !rec.getInterfaceName().isEmpty());
            Assert.assertTrue(rec.getMacAddress() != null && !rec.getMacAddress().isEmpty());
            Assert.assertTrue(rec.getAddresses() != null && !rec.getAddresses().contains(null) && !rec.getAddresses().isEmpty());
            Assert.assertTrue(rec.getDomains() != null && !rec.getDomains().contains(null) && !rec.getDomains().isEmpty());
            Assert.assertTrue(rec.getCapacity() == anInt);
            Assert.assertTrue(rec.getMtu() == anInt);
            System.out.println("Methods executed successfully");

            System.out.println("Testing bad MTU value (should fail)");
            try {

                rec.setMtu(lowMtuValue);
                Assert.fail("Bad MTU value accepted for interface record");
            } catch (RecordException expected) { }
            System.out.println("Expected exception successfully thrown");

        } catch (RecordException e) {

            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }
        //Map<String, Object> expected = (HashMap)((HashMap) getConfiguration(configFile).get("records")).get("interface-record");

        //Assert.assertTrue("Value(s) do not match expected values from test config file", rec.getMap().entrySet().containsAll(expected.entrySet()));
        Assert.assertTrue("Record does not validate", rec.validate());
    }

    @Test
    public void generateServiceRecord() throws ParserException {

        System.out.println("Testing generation of a service record.");

        ServiceRecord rec = new ServiceRecord();

        try {

            System.out.println("Testing location getters and setters");
            rec.setCity(str);
            rec.setSiteName(str);
            rec.setZipcode(str);
            rec.setState(str);
            rec.setCountry(country);
            rec.setLatitude(anInt);
            rec.setLongitude(anInt);
            Assert.assertTrue(rec.getCity() != null && !rec.getCity().isEmpty());
            Assert.assertTrue(rec.getSiteName() != null && !rec.getSiteName().isEmpty());
            Assert.assertTrue(rec.getZipcode() != null && !rec.getZipcode().isEmpty());
            Assert.assertTrue(rec.getState() != null && !rec.getState().isEmpty());
            Assert.assertTrue(rec.getCountry() != null && !rec.getCountry().isEmpty());
            Assert.assertTrue(rec.getLatitude() >= Double.parseDouble((String) testValues.get("latitude-lower-bound")) && rec.getLatitude() <= Double.parseDouble((String) testValues.get("latitude-upper-bound")));
            Assert.assertTrue(rec.getLongitude() >= Double.parseDouble((String) testValues.get("longitude-lower-bound")) && rec.getLongitude() <= Double.parseDouble((String) testValues.get("longitude-upper-bound")));

            System.out.println("Testing service getters and setters");
            rec.setHost(str);
            rec.setServiceName(str);
            rec.setServiceType(str);
            rec.setAdministrators(stringList);
            rec.setDomains(stringList);
            rec.setCommunities(stringList);
            rec.setServiceLocator(stringList);
            Assert.assertTrue(rec.getHost() != null && !rec.getHost().isEmpty());
            Assert.assertTrue(rec.getServiceName() != null && !rec.getServiceName().isEmpty());
            Assert.assertTrue(rec.getServiceType() != null && !rec.getServiceType().isEmpty());
            Assert.assertTrue(rec.getAdministrators() != null && !rec.getAdministrators().contains(null) && !rec.getAdministrators().isEmpty());
            Assert.assertTrue(rec.getDomains() != null && !rec.getDomains().contains(null) && !rec.getDomains().isEmpty());
            Assert.assertTrue(rec.getCommunities() != null && !rec.getCommunities().contains(null) && !rec.getCommunities().isEmpty());
            Assert.assertTrue(rec.getServiceLocator() != null && !rec.getServiceLocator().contains(null) && !rec.getServiceLocator().isEmpty());

            System.out.println("Testing setters with bad country and latitude/longitude values (should fail)");
            try {
                rec.setCountry("bogus");

            } catch (RecordException expected) { }

            try {
                rec.setLatitude(lowLatitude);
                Assert.fail("Bad latitude value accepted for host record");
            } catch (RecordException expected) { }

            try {
                rec.setLatitude(highLatitude);
                Assert.fail("Bad latitude value accepted for host record");
            } catch (RecordException expected) { }

            try {
                rec.setLongitude(lowLongitude);
                Assert.fail("Bad longitude value accepted for host record");
            } catch (RecordException expected) { }

            try {
                rec.setLongitude(highLongitude);
                Assert.fail("Bad longitude value accepted for host record");
            } catch (RecordException expected) { }
            System.out.println("Expected exceptions successfully thrown");

        } catch (RecordException e) {

            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }

        Assert.assertTrue("Record does not validate", rec.validate());
    }

    @Test
    public void generatePersonRecord() throws ParserException {

        System.out.println("Testing generation of a person record");

        PersonRecord rec = new PersonRecord();

        try {

            System.out.println("Testing location getters and setters");
            rec.setCity(str);
            rec.setState(str);
            rec.setSiteName(str);
            rec.setZipcode(str);
            rec.setCountry(country);
            rec.setDomains(stringList);
            rec.setLatitude(anInt);
            rec.setLongitude(anInt);
            Assert.assertTrue(rec.getCity() != null && !rec.getCity().isEmpty());
            Assert.assertTrue(rec.getState() != null && !rec.getState().isEmpty());
            Assert.assertTrue(rec.getSiteName() != null && !rec.getSiteName().isEmpty());
            Assert.assertTrue(rec.getZipcode() != null && !rec.getZipcode().isEmpty());
            Assert.assertTrue(rec.getCountry() != null && !rec.getCountry().isEmpty());
            Assert.assertTrue(rec.getDomains() != null && !rec.getDomains().isEmpty());
            Assert.assertTrue(rec.getLatitude() >= Double.parseDouble((String) testValues.get("latitude-lower-bound")) && rec.getLatitude() <= Double.parseDouble((String) testValues.get("latitude-upper-bound")));
            Assert.assertTrue(rec.getLongitude() >= Double.parseDouble((String) testValues.get("longitude-lower-bound")) && rec.getLongitude() <= Double.parseDouble((String) testValues.get("longitude-upper-bound")));

            System.out.println("Testing person getters and setters");
            rec.setName(str);
            rec.setOrganization(str);
            rec.setEmailAddresses(stringList);
            rec.setPhoneNumbers(stringList);
            Assert.assertTrue(rec.getName() != null && !rec.getName().isEmpty());
            Assert.assertTrue(rec.getOrganization() != null && !rec.getOrganization().isEmpty());
            Assert.assertTrue(rec.getEmailAddresses() != null && !rec.getEmailAddresses().isEmpty());
            Assert.assertTrue(rec.getPhoneNumbers() != null && !rec.getPhoneNumbers().isEmpty());

            System.out.println("Testing setters with bad country and latitude/longitude values (should fail)");
            try {
                rec.setCountry("bogus");
                Assert.fail("Bad country value accepted for host record");
            } catch (RecordException expected) { }

            try {
                rec.setLatitude(lowLatitude);
                Assert.fail("Bad latitude value accepted for host record");
            } catch (RecordException expected) { }

            try {
                rec.setLatitude(highLatitude);
                Assert.fail("Bad latitude value accepted for host record");
            } catch (RecordException expected) { }

            try {
                rec.setLongitude(lowLongitude);
                Assert.fail("Bad longitude value accepted for host record");
            } catch (RecordException expected) { }

            try {
                rec.setLongitude(highLongitude);
                Assert.fail("Bad longitude value accepted for host record");
            } catch (RecordException expected) { }
            System.out.println("Expected exceptions successfully thrown");

        } catch (RecordException e) {

            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }
        Map<String, Object> expected = (HashMap)((HashMap) getConfiguration(configFile).get("records")).get("person-record");
        Assert.assertTrue("Value(s) do not match expected values from test config file", rec.getMap().entrySet().containsAll(expected.entrySet()));
        Assert.assertTrue("Record does not validate", rec.validate());
    }
}
