package boot.zookeeper;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HashBalance extends AbstractLoadBalance {

    private Map<String,ConsistentHash<String>> serviceHashMap;
    private String service_name;
    public HashBalance(String service_name, Map<String, ConsistentHash<String>> serviceHashMap) {
        this.service_name = service_name;
        this.serviceHashMap = serviceHashMap;
    }

    @Override
    protected String doSelect(String service_name, String param,Map<String, List<String>> host) {
        if(serviceHashMap == null){
            return null;
        }
        ConsistentHash<String> consistentHash = serviceHashMap.get(service_name);
        if(consistentHash == null){
            return null;
        }

        //String serverNodeName = consistentHash.getNode(UUID.randomUUID().toString());
        String serverNodeName = consistentHash.getNode(param);
        return serverNodeName;
    }
}
