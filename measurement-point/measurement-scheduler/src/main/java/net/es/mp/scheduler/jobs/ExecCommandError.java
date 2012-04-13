package net.es.mp.scheduler.jobs;

import java.util.regex.Pattern;

public class ExecCommandError {
    private Pattern errorPattern;
    private String errorStatus;
    private String errorMessage;
    
    public ExecCommandError(Pattern pattern, String status){
        this(pattern, status, null);
    }
    
    public ExecCommandError(Pattern pattern, String status, String errorMessage){
        this.errorPattern = pattern;
        this.errorStatus = status;
        this.errorMessage = errorMessage;
    }
    
    /**
     * @return the errorPattern
     */
    public Pattern getErrorPattern() {
        return this.errorPattern;
    }
    /**
     * @param errorPattern the errorPattern to set
     */
    public void setErrorPattern(Pattern errorPattern) {
        this.errorPattern = errorPattern;
    }
    /**
     * @return the errorStatus
     */
    public String getErrorStatus() {
        return this.errorStatus;
    }
    /**
     * @param errorStatus the errorStatus to set
     */
    public void setErrorStatus(String errorStatus) {
        this.errorStatus = errorStatus;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
