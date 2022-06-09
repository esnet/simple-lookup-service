package net.es.lookup.records;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Author: sowmya
 * Date: 5/2/13
 * Time: 2:50 PM
 */
public class DataValidator {

    private static HashMap<String, Integer> countryCode = new HashMap<String, Integer>();

    static {
        Scanner sc = null;
        try {
            String current = new File( "." ).getCanonicalPath();
            sc = new Scanner(new File(current + "/etc/countries.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        while (sc.hasNext()) {
            countryCode.put(sc.next(), 1);
        }
    }

    public static boolean isValidCountry(String code) {


        if (code == null || code.isEmpty() || code.length() > 2) {
            return false;
        } else {
            if (countryCode.containsKey(code)) {
                return true;
            } else {
                return false;
            }
        }
    }


    public static boolean isValidLatitude(Double latitude){
        if(latitude>=-90.00 && latitude<=90.00){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isValidLongitude(Double longitude){
        if(longitude>=-180.00 && longitude<=180.00){
            return true;
        }else{
            return false;
        }
    }
}
