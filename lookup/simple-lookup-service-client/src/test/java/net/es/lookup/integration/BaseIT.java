package net.es.lookup.integration;

import junit.framework.Assert;
import net.es.lookup.client.QueryClient;
import net.es.lookup.client.RecordManager;
import net.es.lookup.client.RegistrationClient;
import net.es.lookup.client.SimpleLS;
import net.es.lookup.common.exception.LSClientException;
import org.ho.yaml.Yaml;
import org.junit.After;
import org.junit.BeforeClass;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.*;

import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: student5
 * Date: 7/25/13
 * Time: 2:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseIT {

    protected static boolean connectedToLS = false;
    private static boolean initialized = false;
    protected static int maxAttempts = 3;
    protected static String host = "localhost";
    protected static int port = 8090;
    protected static String configFile = "./src/test/config/testvalues.yaml";
    protected static SimpleLS queryLS = null;
    protected static SimpleLS regLS = null;
    protected static SimpleLS simpleLS = null;
    protected static RecordManager rmc = null;
    protected static QueryClient qc = null;
    protected static RegistrationClient rc = null;
    protected static HashMap testValues;
    protected static String type;
    protected static String key1;
    protected static String key2;
    protected static List<String> list;
    protected static List<String> typeList;
    protected static List<String> wcList;
    protected static Random rand;
    protected static String registrationData;
    protected static String registrationDataMultiKey;
    protected static String registrationDataPartial;
    protected static List<Object> result = null;
    protected static Object registrationResult = null;
    protected static String recordID = null;
    protected static LinkedList<String> recordIDs;
    protected static String str;
    protected static String country;
    protected static int anInt;
    protected static List<String> stringList;
    protected static List<String> countryList;
    protected static List<Integer> integerList;
    protected static List<Long> longList = new LinkedList<Long>();
    protected static List<Double> doubleList;
    protected static List<String> phoneList;
    protected static double lowLatitude;
    protected static double highLatitude;
    protected static double lowLongitude;
    protected static double highLongitude;
    protected static int lowMtuValue;

    @BeforeClass
    public static void init() {

        if (!initialized) {

            System.out.println("Initializing test values and configuring LS references");

            recordIDs = new LinkedList<String>();
            rand = new Random();

            //These assignments are for valid values used in the tests
            testValues = (HashMap) getConfiguration(configFile).get("test-values");
            type = (String) testValues.get("type");
            key1 = (String) testValues.get("key-1");
            key2 = (String) testValues.get("key-2");
            registrationData = (String) testValues.get("registration-data");
            registrationDataMultiKey = (String) testValues.get("registration-data-complex");
            registrationDataPartial = (String) testValues.get("registration-data-partial");
            list = (List) testValues.get("list-of-string");
            typeList = (List) testValues.get("type-list");
            wcList = (List) testValues.get("wild-card-list");
            stringList = ((List<String>) testValues.get("list-of-string"));
            countryList = ((List<String>) testValues.get("list-of-country"));
            integerList = ((List<Integer>) testValues.get("list-of-int"));
            longList.add(new Long(1));
            doubleList = ((List<Double>) testValues.get("list-of-double"));
            phoneList = ((List<String>) testValues.get("list-of-phone"));
            str = (String) testValues.get("string");
            country = (String) testValues.get("country");
            anInt = Integer.decode((String) testValues.get("positive-int-string"));
            lowLatitude = Double.parseDouble((String) testValues.get("latitude-lower-bound")) - 0.1;
            highLatitude = Double.parseDouble((String) testValues.get("latitude-upper-bound")) + 0.1;
            lowLongitude = Double.parseDouble((String) testValues.get("longitude-lower-bound")) - 0.1;
            highLongitude = Double.parseDouble((String) testValues.get("longitude-upper-bound")) + 0.1;
            lowMtuValue = Integer.decode((String) testValues.get("mtu-lower-bound")) - 1;

            HashMap lookupServiceMap = (HashMap) getConfiguration(configFile).get("lookupservice");
            host = (String) lookupServiceMap.get("host");
            port = (Integer) lookupServiceMap.get("port");

            if (!connectedToLS) {

                boolean refused = true;
                Socket socket = null;

                try {

                    while (refused && maxAttempts-- > 0) {

                        System.out.println("Testing connection to SLS server...");
                        try {

                            socket = new Socket(host, port);

                            System.out.println("Connected to server");
                            refused = false;

                            PrintWriter writer = new PrintWriter(socket.getOutputStream());
                            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                            writer.println("GET /lookup/records\n");
                            writer.flush();
                            String line = reader.readLine();
                            connectedToLS = (line != null);
                        } catch (ConnectException ignored) {

                            Thread.sleep(1000);
                        } catch (IOException ignored) {

                            Thread.sleep(1000);
                        } catch (NullPointerException e) {

                            System.out.println("socket is null");
                            Assert.fail("socket is null");
                        }
                    }
                }catch (InterruptedException e) {

                    System.out.println("Interrupted while waiting for socket connection");
                    fail("Interrupted while waiting for socket connection");
                } finally {

                    try {

                        if (socket != null) {

                            socket.close();
                        }
                    } catch (Exception ignored) { }
                }
            }

            if (!connectedToLS) {

                System.out.println("Unable to connect to SLS");
                Assert.fail("Unable to connect to SLS");
            } else {

                try {

                    System.out.println("Connecting to LS");
                    regLS = new SimpleLS(host, port, "POST");
                    regLS.setRelativeUrl("lookup/records");
                    regLS.connect();
                    queryLS = new SimpleLS(host, port, "GET");
                    queryLS.connect();
                    simpleLS = new SimpleLS(host, port);
                    simpleLS.connect();

                    initialized = true;
                } catch (LSClientException e) {

                    System.out.println(e.getMessage());
                    fail(e.getMessage());
                }
            }
        }
    }

    @After
    public void clearTestCases() {

        if (!recordIDs.isEmpty()) {
            System.out.println("Cleaning up test records");

            for (String id : recordIDs) {

                String action = "/lookup/" + type + "/" + id;
                try {

                    SimpleLS deleteLS = new SimpleLS(host, port, "DELETE");
                } catch (LSClientException e) {

                    System.out.println("An exception occurred while trying to delete the test record: " + id);
                    System.out.println("Delete record manually");
                }
            }
        }

        recordIDs.clear();
        result = null;
    }

    protected static Map getConfiguration(String configFile) {

        Map configuration;
        InputStream yamlFile = QueryClientIT.class.getClassLoader().getSystemResourceAsStream(configFile);

        try {

            configuration = (Map) Yaml.load(yamlFile);
        } catch (NullPointerException ex) {

            try {

                yamlFile = new FileInputStream(new File(configFile));
            } catch (FileNotFoundException e) {

                System.out.println(configFile + " not found\n. Config file required to start tests");
                Assert.fail(configFile + " not found\n. Config file required to start tests");
            }

            configuration = (Map) Yaml.load(yamlFile);
        }

        return configuration;
    }
}
