import net.es.lookup.client.RegistrationClient;
import net.es.lookup.common.exception.LSClientException;
import net.es.lookup.common.exception.ParserException;
import net.es.lookup.protocol.json.JSONParser;
import net.es.lookup.records.ErrorRecord;
import net.es.lookup.records.Record;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: student5
 * Date: 6/5/13
 * Time: 3:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class RegistrationClientTest extends BaseTest {

    @Test
    public void registerRecord() {

        System.out.println("Testing record registration");

        Record rec;
        int tmp = rand.nextInt();
        registrationData = registrationData.replace(registrationData.substring(registrationData.indexOf("unique")), "unique\":[\"" + Integer.toString(tmp) + "\"]}");

        try {
            rec = JSONParser.toRecord(registrationData);

            rc = new RegistrationClient(regLS, rec);
            registrationResult = rc.register();

            if (registrationResult instanceof ErrorRecord) {

                Assert.fail(regLS.getErrorMessage());
            } else if (registrationResult instanceof Record) {

                int index = ((Record) registrationResult).getURI().lastIndexOf("/") + 1;
                recordID = ((Record) registrationResult).getURI().substring(index);
                recordIDs.add(recordID);
            }

        } catch (LSClientException e) {

            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        } catch (ParserException e) {

            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }
        System.out.println(regLS.getResponseCode() + ": " + regLS.getErrorMessage());
        Assert.assertTrue(((Record) registrationResult).validate() && regLS.getResponseCode() == 200);
    }
}