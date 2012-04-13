package net.es.mp.container;

import java.util.List;
import java.util.Map;

public interface MPService {
    
    public void init(MPContainer mpc, Map config);
    
    public void addServiceResources(List<String> resourceList);
}
