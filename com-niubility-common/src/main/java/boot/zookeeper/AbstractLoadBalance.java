package boot.zookeeper;

import java.util.List;
import java.util.Map;

public abstract class AbstractLoadBalance implements LoadBalance {
    @Override
    public String selectHost(String service_name,  String param ,Map<String, List<String>> host) {
        if(host == null || host.size() == 0){
            return null;
        }
        if(host.size() == 1){
            List<String> list = host.get(service_name);
            if(list == null){
                return null;
            }
            if(list.size() == 1){
                return list.get(0);
            }
        }
        return doSelect(service_name,param,host);
    }

    protected abstract String doSelect(String service_name,String param, Map<String, List<String>> host);
}
