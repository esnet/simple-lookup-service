package net.es.lookup.integration;

import net.es.lookup.client.QueryClient;
import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.LSClientException;
import net.es.lookup.common.exception.ParserException;
import net.es.lookup.common.exception.QueryException;
import net.es.lookup.integration.BaseIT;
import net.es.lookup.protocol.json.JSONParser;
import net.es.lookup.queries.Query;
import net.es.lookup.records.Record;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: student5
 * Date: 6/25/13
 * Time: 4:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryClientIT extends BaseIT {

    @Test
    public void queryUnspecified() {

        System.out.println("Testing query with unspecified parameters");

        int tmp = rand.nextInt();
        registrationData = registrationData.replace(registrationData.substring(registrationData.indexOf("unique")), "unique\":[\"" + Integer.toString(tmp) + "\"]}");

        try {

            regLS.setData(registrationData);
            regLS.send();
            String response = regLS.getResponse();

            if (regLS.getResponseCode() == 403) {
                fail("Test record is duplicate of existing record");
            }

            if (regLS.getResponseCode() == 200) {

                int index = (JSONParser.toRecord(response)).getURI().lastIndexOf("/") + 1;
                recordID = (JSONParser.toRecord(response)).getURI().substring(index);
                recordIDs.add(recordID);

                qc = new QueryClient(queryLS);
                result = qc.query();
            } else {

                System.out.println("Test setup failed with error code " + regLS.getResponseCode() + ". Could not register test record with specified LS.");
                fail(regLS.getErrorMessage());
            }
        } catch (LSClientException e) {

            System.out.println(e.getMessage());
            fail();
        } catch (ParserException e) {

            System.out.println(e.getMessage());
            fail();
        } catch (QueryException e) {

            System.out.println(e.getMessage());
            fail();
        }
        System.out.println(queryLS.getResponseCode() + ": " + queryLS.getErrorMessage());
        assertTrue(result != null && !result.isEmpty());
    }

    @Test
    public void querySingleParam() {

        System.out.println("Testing query with a single parameter");

        Query query = new Query();
        Record rec = null;
        int tmp = rand.nextInt();
        registrationData = registrationData.replace(registrationData.substring(registrationData.indexOf("unique")), "unique\":[\"" + Integer.toString(tmp) + "\"]}");

        try {

            query.add(key1, list);
            regLS.setData(registrationData);
            regLS.send();
            String response = regLS.getResponse();

            if (regLS.getResponseCode() == 403) {
                fail("Test record is duplicate of existing record");
            }

            if (regLS.getResponseCode() == 200) {

                int index = (JSONParser.toRecord(response)).getURI().lastIndexOf("/") + 1;
                recordID = (JSONParser.toRecord(response)).getURI().substring(index);
                recordIDs.add(recordID);

                qc = new QueryClient(queryLS);
                qc.setQuery(query);
                result = qc.query();
                rec = (Record) result.get(0);

            } else {

                System.out.println("Test setup failed with error code " + regLS.getResponseCode() + ". Could not register test record with specified LS.");
                fail(regLS.getErrorMessage());
            }
        } catch (LSClientException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        } catch (ParserException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        } catch (QueryException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        }
        System.out.println(queryLS.getResponseCode() + ": " + queryLS.getErrorMessage());
        assertTrue(result != null && !result.isEmpty() && rec.validate());
    }

    @Test
    public void queryMultipleParams() {

        System.out.println("Testing query with multiple parameters");

        Query query = new Query();
        Record rec = null;
        int tmp = rand.nextInt();
        registrationDataMultiKey = registrationDataMultiKey.replace(registrationDataMultiKey.substring(registrationDataMultiKey.indexOf("unique")), "unique\":[\"" + Integer.toString(tmp) + "\"]}");

        try {

            query.add(key1, list);
            query.add(key2, list);
            regLS.setData(registrationDataMultiKey);
            regLS.send();
            String response = regLS.getResponse();

            if (regLS.getResponseCode() == 403) {
                fail("Test record is duplicate of existing record");
            }

            if (regLS.getResponseCode() == 200) {

                int index = (JSONParser.toRecord(response)).getURI().lastIndexOf("/") + 1;
                recordID = (JSONParser.toRecord(response)).getURI().substring(index);
                recordIDs.add(recordID);

                qc = new QueryClient(queryLS);
                qc.setQuery(query);
                result = qc.query();
                rec = (Record) result.get(0);

            } else {

                System.out.println("Test setup failed with error code " + regLS.getResponseCode() + ". Could not register test record with specified LS.");
                fail(regLS.getErrorMessage());
            }
        } catch (LSClientException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        } catch (ParserException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        } catch (QueryException e) {

            System.out.println(e.getMessage());
           fail(e.getMessage());
        }
        System.out.println(queryLS.getResponseCode() + ": " + queryLS.getErrorMessage());
        assertTrue(result != null && !result.isEmpty() && rec.validate());
    }

    @Test
    public void queryWithWildCards() {

        System.out.println("Testing query with a wild card");

        Query query = new Query();
        Record rec = null;
        int tmp = rand.nextInt();
        registrationData = registrationData.replace(registrationData.substring(registrationData.indexOf("unique")), "unique\":[\"" + Integer.toString(tmp) + "\"]}");

        try {

            query.add(key1, wcList);
            regLS.setData(registrationData);
            regLS.send();
            String response = regLS.getResponse();

            if (regLS.getResponseCode() == 403) {
                fail("Test record is duplicate of existing record");
            }

            if (regLS.getResponseCode() == 200) {

                int index = (JSONParser.toRecord(response)).getURI().lastIndexOf("/") + 1;
                recordID = (JSONParser.toRecord(response)).getURI().substring(index);
                recordIDs.add(recordID);

                qc = new QueryClient(queryLS);
                qc.setQuery(query);
                result = qc.query();
                rec = (Record) result.get(0);
            } else {

                System.out.println("Test setup failed with error code " + regLS.getResponseCode() + ". Could not register test record with specified LS.");
                fail(regLS.getErrorMessage());
            }
        } catch (LSClientException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        } catch (ParserException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        } catch (QueryException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        }
        System.out.println(queryLS.getResponseCode() + ": " + queryLS.getErrorMessage());
        assertTrue(result != null && !result.isEmpty() && rec.validate());
    }

    @Test
    public void queryWithOperatorFlag() {

        System.out.println("Testing queries with operator flags");

        List<String> operatorList;
        Query query;
        String response;
        int tmp = rand.nextInt();
        registrationDataPartial = registrationDataPartial.replace(registrationDataPartial.substring(registrationDataPartial.indexOf("unique")), "unique\":[\"" + Integer.toString(tmp) + "\"]}");

        try {

            regLS.setData(registrationDataPartial);
            regLS.send();
            response = regLS.getResponse();

            if (regLS.getResponseCode() == 403) {
                fail("Test record is duplicate of existing record");
            }

            if (regLS.getResponseCode() == 200) {

                int index = (JSONParser.toRecord(response)).getURI().lastIndexOf("/") + 1;
                recordID = (JSONParser.toRecord(response)).getURI().substring(index);
                recordIDs.add(recordID);

            } else {

                System.out.println("Test setup failed with error code " + regLS.getResponseCode() + ". Could not register test record with specified LS.");
                fail(regLS.getErrorMessage());
            }

            tmp = rand.nextInt();
            registrationData = registrationData.replace(registrationData.substring(registrationData.indexOf("unique")), "unique\":[\"" + Integer.toString(tmp) + "\"]}");

            regLS.setData(registrationData);
            regLS.send();
            response = regLS.getResponse();

            if (regLS.getResponseCode() == 403) {
                fail("Test record is duplicate of existing record");
            }

            if (regLS.getResponseCode() == 200) {

                int index = (JSONParser.toRecord(response)).getURI().lastIndexOf("/") + 1;
                recordID = (JSONParser.toRecord(response)).getURI().substring(index);
                recordIDs.add(recordID);

                operatorList = new LinkedList<String>();
                qc = new QueryClient(queryLS);

                //Test "operator":["any"] with multiple elements (should pass)
                System.out.println("Testing operator value \"ANY\"");
                operatorList.add(ReservedValues.RECORD_OPERATOR_ANY);
                query = new Query();
                query.add(ReservedKeys.RECORD_OPERATOR, operatorList);
                query.add(ReservedKeys.RECORD_TYPE, typeList);
                query.add(key1, list);

                qc.setQuery(query);
                result = qc.query();

                System.out.println(queryLS.getResponseCode() + ": " + queryLS.getErrorMessage());
                assertTrue(result != null && !result.isEmpty() && result.size() > 1);

                //Test "operator":["all"] with multiple elements (should pass)
                System.out.println("Testing operator value \"ALL\"");
                operatorList.clear();
                operatorList.add(ReservedValues.RECORD_OPERATOR_ALL);
                query.add(ReservedKeys.RECORD_OPERATOR, operatorList);

                qc.setQuery(query);
                result = qc.query();
                qc.getRelativeUrl(); //Testing that this doesn't throw an exception

                System.out.println(queryLS.getResponseCode() + ": " + queryLS.getErrorMessage());
                assertTrue(result != null && !result.isEmpty() && result.size() >= 1);

                //Test "<key1>-operator":["all"] with multiple elements (should pass)
                System.out.println("Testing key-specific operator value \"ALL\"");
                operatorList.clear();
                operatorList.add(ReservedValues.RECORD_OPERATOR_ALL);
                query.add(key1 + "-operator", operatorList);

                qc.setQuery(query);
                result = qc.query();

                System.out.println(queryLS.getResponseCode() + ": " + queryLS.getErrorMessage());
                assertTrue(result != null && !result.isEmpty() && result.size() >= 1);

                //Test "<key1>-operator":["any"] with multiple elements (should pass)
                System.out.println("Testing key-specific operator value \"ANY\"");
                operatorList.clear();
                operatorList.add(ReservedValues.RECORD_OPERATOR_ANY);
                query.add(key1 + "-operator", operatorList);

                qc.setQuery(query);
                result = qc.query();

                System.out.println(queryLS.getResponseCode() + ": " + queryLS.getErrorMessage());
                assertTrue(result != null && !result.isEmpty() && result.size() >= 1);
            } else {

                System.out.println("Test setup failed with error code " + regLS.getResponseCode() + ". Could not register test record with specified LS.");
                fail(regLS.getErrorMessage());
            }
        } catch (LSClientException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        } catch (ParserException e) {

            System.out.println(e.getMessage());

            fail(e.getMessage());
        } catch (QueryException e) {

            System.out.println(e.getMessage());

            fail(e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    @Test
    public void parseQueryResults() {

        System.out.println("Testing parsing of a list of query results");

        Query query = new Query();
        boolean listIsValid = true;
        int tmp = rand.nextInt();
        registrationData = registrationData.replace(registrationData.substring(registrationData.indexOf("unique")), "unique\":[\"" + Integer.toString(tmp) + "\"]}");
        try {

            query.add(ReservedKeys.RECORD_TYPE, typeList);
            regLS.setData(registrationData);
            regLS.send();
            String response = regLS.getResponse();

            if (regLS.getResponseCode() == 403) {
                fail("Test record is duplicate of existing record");
            }

            if (regLS.getResponseCode() == 200) {

                int index = (JSONParser.toRecord(response)).getURI().lastIndexOf("/") + 1;
                recordID = (JSONParser.toRecord(response)).getURI().substring(index);
                recordIDs.add(recordID);

            } else {

                System.out.println("Test setup failed with error code " + regLS.getResponseCode() + ". Could not register test record with specified LS.");
                fail();
            }

            tmp = rand.nextInt();
            registrationData = registrationData.replace(registrationData.substring(registrationData.indexOf("unique")), "unique\":[\"" + Integer.toString(tmp) + "\"]}");

            regLS.setData(registrationData.replace(key1, key2));
            regLS.send();
            response = regLS.getResponse();

            if (regLS.getResponseCode() == 403) {
               fail("Test record is duplicate of existing record");
            }

            if (regLS.getResponseCode() == 200) {

                int index = (JSONParser.toRecord(response)).getURI().lastIndexOf("/") + 1;
                recordID = (JSONParser.toRecord(response)).getURI().substring(index);
                recordIDs.add(recordID);

                qc = new QueryClient(queryLS);
                qc.setQuery(query);
                result = qc.query();
            } else {

                System.out.println("Test setup failed with error code " + regLS.getResponseCode() + ". Could not register test record with specified LS.");
                fail(regLS.getErrorMessage());
            }

            if (result != null) {

                for (Object o : result) {

                    listIsValid &= ((Record) o).validate();
                }
            } else {
                listIsValid = false;
            }
        } catch (LSClientException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        } catch (ParserException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        } catch (QueryException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        }
        System.out.println(queryLS.getResponseCode() + ": " + queryLS.getErrorMessage());
        assertTrue(result != null && !result.isEmpty() && listIsValid);
    }
}
