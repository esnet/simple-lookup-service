package net.es.mp.scheduler.jobs;

public class ExecCommandOpt {
    private String keyName;
    private String optionName;
    private String defaultValue;
    private boolean flag;
    private Object reqValueForFlag;
    
    public ExecCommandOpt(String keyName, String optionName){
        this(keyName, optionName, null, false, null);
    }
    
    
    public ExecCommandOpt(String keyName, String optionName, 
            String defaultValue){
        this(keyName, optionName, defaultValue, false, null);
    }
    
    public ExecCommandOpt(String keyName, String optionName, 
            boolean flag, Object reqValueForFlag){
        this(keyName, optionName, null, flag, reqValueForFlag);
    }
    
    public ExecCommandOpt(String keyName, String optionName, 
            String defaultValue, boolean flag, Object reqValueForFlag){
        this.keyName = keyName;
        this.optionName = optionName;
        this.flag = flag;
        this.defaultValue = defaultValue;
        this.reqValueForFlag = reqValueForFlag;
    }
    
    /**
     * @return the keyName
     */
    public String getKeyName() {
        return this.keyName;
    }
    /**
     * @param keyName the keyName to set
     */
    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }
    /**
     * @return the optionName
     */
    public String getOptionName() {
        return this.optionName;
    }
    /**
     * @param optionName the optionName to set
     */
    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }
    /**
     * @return the flag
     */
    public boolean isFlag() {
        return this.flag;
    }
    /**
     * @param flag the flag to set
     */
    public void setFlag(boolean flag) {
        this.flag = flag;
    }
    /**
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return this.defaultValue;
    }
    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }


    /**
     * @return the reqValueForFlag
     */
    public Object getReqValueForFlag() {
        return this.reqValueForFlag;
    }


    /**
     * @param reqValueForFlag the reqValueForFlag to set
     */
    public void setReqValueForFlag(Object reqValueForFlag) {
        this.reqValueForFlag = reqValueForFlag;
    }
    
}
