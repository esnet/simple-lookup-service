package net.es.mp.util;

import java.util.Date;

public class TimeUtil {
    
    final static public long JAN_1970 = 0x83aa7e80;
    
    static public Date owpTimeToDate(String first32Bits, String next32Bits){
        long n1 = Long.parseLong(first32Bits);
        long n2 = Long.parseLong(next32Bits);
        long combined = n1 << 32;
        combined += n2; 
        combined /= Math.pow(2, 32);
        combined -= JAN_1970;
        
        System.out.println("first32Bits=" + first32Bits);
        System.out.println("next32Bits=" + next32Bits);
        System.out.println("combined=" + combined);
        
        return new Date(combined * 1000);
    }
}
