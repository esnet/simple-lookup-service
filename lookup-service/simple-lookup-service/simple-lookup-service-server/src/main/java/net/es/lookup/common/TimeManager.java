package net.es.lookup.common;


public class TimeManager {

    private static TimeManager instance = null;
    private static long start = 0;
    private static final int delta = 1;

    static {
        TimeManager.instance = new TimeManager();
    }

    public static TimeManager getInstance(){
        return TimeManager.instance;
    }

    private void startTime(){
        start = System.currentTimeMillis();
    }

    public boolean hasElapsed(){
        long current = System.currentTimeMillis();
        if (start == 0){
            startTime();
            return true;
        }
        long diff = current - start;
        if( diff >= delta * 1000){
            startTime();
            return true;
        }
        return false;
    }
}
