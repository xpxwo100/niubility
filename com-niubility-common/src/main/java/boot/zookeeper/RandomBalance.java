package boot.zookeeper;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomBalance extends AbstractLoadBalance {
    private String service_name;
    public RandomBalance(String service_name) {
        this.service_name = service_name;
    }

    @Override
    protected String doSelect(String service_name, String param,Map<String, List<String>> host) {
        List<String> list = host.get(service_name);
        Random random = new Random();
        return list.get(random.nextInt(list.size()));
    }
}
