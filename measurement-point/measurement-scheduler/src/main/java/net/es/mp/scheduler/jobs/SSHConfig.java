package net.es.mp.scheduler.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class SSHConfig {
    static private Logger log = Logger.getLogger(SSHConfig.class);
    
    protected boolean enabled;
    protected String command;
    protected String user;
    protected String key;
    protected String localhost;
    protected List<String> hostPrefs;
    protected String scheduleHostField;
    
    static final public String HOST_LOCAL = "local";
    static final public String HOST_SSH_PREFIX = "ssh://";
    static final public String PROP_SSH = "ssh";
    static final private String PROP_SSH_ENABLED = "enabled";
    static final private String PROP_SSH_COMMAND = "sshCommand";
    static final private String PROP_SSH_USER = "user";
    static final private String PROP_SSH_KEY = "key";
    static final private String PROP_SSH_HOST_PREFS = "hostPreferences";
    
    public SSHConfig(){
        this.enabled = false;
        this.command = "ssh";
        this.user = null;
        this.key = null;
        this.localhost = "";
        this.hostPrefs = new ArrayList<String>();
        this.scheduleHostField = null;
    }
    
    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return this.enabled;
    }
    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    /**
     * @return the command
     */
    public String getCommand() {
        return this.command;
    }
    /**
     * @param command the command to set
     */
    public void setCommand(String command) {
        this.command = command;
    }
    /**
     * @return the user
     */
    public String getUser() {
        return this.user;
    }
    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }
    /**
     * @return the key
     */
    public String getKey() {
        return this.key;
    }
    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the localhost
     */
    public String getLocalhost() {
        return this.localhost;
    }

    /**
     * @param localhost the localhost to set
     */
    public void setLocalhost(String localhost) {
        this.localhost = localhost;
    }

    /**
     * @return the hostPrefs
     */
    public List<String> getHostPrefs() {
        return this.hostPrefs;
    }

    /**
     * @param hostPrefs the hostPrefs to set
     */
    public void setHostPrefs(List<String> hostPrefs) {
        this.hostPrefs = hostPrefs;
    }
    
    /**
     * @return the scheduleHostField
     */
    public String getScheduleHostField() {
        return this.scheduleHostField;
    }

    /**
     * @param scheduleHostField the scheduleHostField to set
     */
    public void setScheduleHostField(String scheduleHostField) {
        this.scheduleHostField = scheduleHostField;
    }

    static public SSHConfig fromYAML(Map yaml, String[] allowedHostPrefs, String localController){
        SSHConfig sshConfig = new SSHConfig();
        
       //if not provided then return
        if(yaml == null){
            return sshConfig;
        }

        //if not enabled then return
        if(!yaml.containsKey(PROP_SSH_ENABLED) || yaml.get(PROP_SSH_ENABLED) == null ||
                (Boolean)yaml.get(PROP_SSH_ENABLED) == false){
            return sshConfig;
        }

        //if no host information provided then return
        if(!yaml.containsKey(PROP_SSH_HOST_PREFS) || yaml.get(PROP_SSH_HOST_PREFS) == null){
            log.warn("Missing property " + PROP_SSH_HOST_PREFS + " in ssh config. Continuing without ssh.");
            return sshConfig;
        }
        HashMap<String, Boolean> allowedHostPrefsMap = new HashMap<String,Boolean>();
        for(String allowedHostPref : allowedHostPrefs){
            allowedHostPrefsMap.put(allowedHostPref, true);
        }
        for(String hostPref : (List<String>)yaml.get(PROP_SSH_HOST_PREFS)){
            if(SSHConfig.HOST_LOCAL.equals(hostPref)){
                sshConfig.getHostPrefs().add(hostPref);
            }else if(hostPref.startsWith(SSHConfig.HOST_SSH_PREFIX)){
                sshConfig.getHostPrefs().add(hostPref);
            }else if(allowedHostPrefsMap.containsKey(hostPref)){
                sshConfig.getHostPrefs().add(hostPref);
            }else{
                log.warn("Skipping unrecognized " + PROP_SSH_HOST_PREFS + " value " + hostPref );
            }
        }

        //set as enabled
        sshConfig.setEnabled(true);
        
        //set localhost
        sshConfig.setLocalhost(localController);
        
        //set command option if provided. defaults to ssh.
        if(yaml.containsKey(PROP_SSH_COMMAND) && yaml.get(PROP_SSH_COMMAND) != null){
            sshConfig.setCommand((String)yaml.get(PROP_SSH_COMMAND));
        }

        //set user option if provided.
        if(yaml.containsKey(PROP_SSH_USER) && yaml.get(PROP_SSH_USER) != null){
            sshConfig.setUser((String)yaml.get(PROP_SSH_USER));
        }

        //set key option if provided.
        if(yaml.containsKey(PROP_SSH_KEY) && yaml.get(PROP_SSH_KEY) != null){
            sshConfig.setKey((String)yaml.get(PROP_SSH_KEY));
        }

        return sshConfig;
    }
}
