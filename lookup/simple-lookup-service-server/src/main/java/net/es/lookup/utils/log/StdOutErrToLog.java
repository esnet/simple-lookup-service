package net.es.lookup.utils.log;

        import org.apache.log4j.Logger;

        import java.io.PrintStream;

/**
 * Author: sowmya
 * Date: 4/12/16
 * Time: 12:15 PM
 */
public class StdOutErrToLog {

        private static Logger LOG = Logger.getLogger(StdOutErrToLog.class);

        public static void redirectStdOutErrToLog(){
            System.setOut(createLoggingPrintStream(System.out));
            System.setErr(createLoggingPrintStream(System.err));
        }

        private static PrintStream createLoggingPrintStream(final PrintStream printStream) {

            return new PrintStream(printStream){

                public void print(String s){
                    LOG.debug(s);
                }

                public void print(Object o){
                    LOG.debug(o);
                }

                public void println(String s){
                    LOG.debug(s);
                }

                public void println(Object o){
                    LOG.debug(o);
                }

            };

        }
}
