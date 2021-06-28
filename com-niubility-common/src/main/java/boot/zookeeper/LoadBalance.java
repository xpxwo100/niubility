package boot.zookeeper;

import java.util.List;
import java.util.Map;

public interface LoadBalance {

    public String selectHost(String service_name,  String param,Map<String, List<String>> host);
}
